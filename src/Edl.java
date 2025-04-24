import java.io.*;

import antlr.collections.impl.Vector;
 // : Renseigner le champs auteur : ElAroussi_Omayma_Leffondre_Corentin_Hanine_Yousra
 /**
 * 
 * @author Hanine_Yousra, Leffondre_Corentin, ElAroussi_Omayma
 * @version 2024
 *
 */


public class Edl {

	// nombre max de modules, taille max d'un code objet d'une unite
	static final int MAXMOD = 5, MAXOBJ = 1000;
	// nombres max de references externes (REF) et de points d'entree (DEF)
	// pour une unite
	private static final int MAXREF = 10, MAXDEF = 10;

	// typologie des erreurs
	private static final int FATALE = 0, NONFATALE = 1;

	// valeurs possibles du vecteur de translation
	private static final int TRANSDON=1,TRANSCODE=2,REFEXT=3;

	// table de tous les descripteurs concernes par l'edl
	static Descripteur[] tabDesc = new Descripteur[MAXMOD + 1];

	// : declarations de variables A COMPLETER SI BESOIN	
	//tabDesc[0] = new Descripteur();
	//tabDesc[0].lireDesc(nomProg);
	static int[] transDon = new int[MAXMOD +1];
	static int[] transCode = new int[MAXMOD +1];
	static int[][] adFinale = new int[MAXMOD +1][MAXREF];
	static String[] dicoDefNomProc = new String[(MAXMOD +1)*MAXDEF];
	static int[] dicoDefAdPo = new int[(MAXMOD +1)* MAXDEF];
	static int[] dicoDefNbParam = new int[(MAXMOD +1)*MAXDEF]; 
	static int nbDefTotal;
	static int ipo, nMod, nbErr;
	static String nomProg;
	static int nbVarGlob;
	static String[] tabNomProg = new String[MAXMOD+1];

	// utilitaire de traitement des erreurs
	// ------------------------------------
	static void erreur(int te, String m) {
		System.out.println(m);
		if (te == FATALE) {
			System.out.println("ABANDON DE L'EDITION DE LIENS");
			System.exit(1);
		}
		nbErr = nbErr + 1;
	}

	// utilitaire de remplissage de la table des descripteurs tabDesc
	// --------------------------------------------------------------
	static void lireDescripteurs() {
		String s;
		System.out.println("les noms doivent etre fournis sans suffixe");
		System.out.print("nom du programme : ");
		s = Lecture.lireString();
		tabDesc[0] = new Descripteur();
		tabDesc[0].lireDesc(s);
		if (!tabDesc[0].getUnite().equals("programme"))
			erreur(FATALE, "programme attendu");
		nomProg = s;
		tabNomProg[0] = s;

		nMod = 0;
		int i = 1;
		while (!s.equals("") && nMod < MAXMOD) {
			System.out.print("nom de module " + (nMod + 1)
					+ " (RC si termine) ");
			s = Lecture.lireString();
			tabNomProg[i] = s;
			if (!s.equals("")) {
				nMod = nMod + 1;
				tabDesc[nMod] = new Descripteur();
				tabDesc[nMod].lireDesc(s);

				if (!tabDesc[nMod].getUnite().equals("module"))
					erreur(FATALE, "module attendu");
			i++;
			}
		}
	}


	static void constMap() {
		
		// pour construire le code concatene de toutes les unités
		int[] po = new int[(nMod + 1) * MAXOBJ + 1];

		ipo = 1;

		// Récupérer les doublets du programme principale
		int nbTrans;// = tabDesc[0].getNbTransExt();
		int[][] transExt;// = new int [nbTrans][2];
		//for(int t = 0; t<nbTrans; t++){
		//	transExt[t] = new int [] {Lecture.lireInt(f1), Lecture.lireIntln(f1)};
		//}

		for(int indMod=0; indMod<=nMod; indMod++){
			//lire dans le fichier
			String nomModule = tabNomProg[indMod];
			InputStream fModule = Lecture.ouvrir(nomModule + ".obj");
			if(fModule == null){
				erreur(FATALE, "impossible d'ouvrir " + nomModule);
			}
			
			//récupère le nombre de modifications à réaliser 
			nbTrans = tabDesc[indMod].getNbTransExt();
			transExt = new int [nbTrans][2];
			for(int t = 0; t<nbTrans; t++){
				int ad = Lecture.lireInt(fModule);
				int code =  Lecture.lireInt(fModule);
				transExt[t] = new int [] {ad, code};
			}
			int adPo = 0;
			int code = 0;
			int indTrans = 0;  
			if( nbTrans != 0){
				adPo = transExt[0][0];
				code =  transExt[0][1]; // type de modification à réaliser
				indTrans ++;
			}

			for(int j=1; j<= tabDesc[indMod].getTailleCode(); j++){
				po[ipo] = Lecture.lireInt(fModule);
				if(j == adPo){
					if( code == TRANSDON){ //tranDon
						po[ipo] += transDon[indMod];
					}else if (code == TRANSCODE){ //transCode
						po[ipo] += transCode[indMod];
					}else if(code == REFEXT){ //RefExt
						po[ipo] = adFinale[indMod][po[ipo]];
					}else {
						erreur(FATALE, "Type de modification incorrecte "); 
					}
					if(indTrans < nbTrans){
						adPo = transExt[indTrans][0];
						code = transExt[indTrans][1];
						indTrans ++;
					}
				}
				

				ipo ++; 
			}
			
			Lecture.fermer(fModule);
		}
		po[2] = nbVarGlob;

		OutputStream f2 = Ecriture.ouvrir(nomProg + ".map");
		// f2 = fichier executable .map construit
		if (f2 == null)
			erreur(FATALE, "creation du fichier " + nomProg
					+ ".map impossible");
		for (int i = 1; i < ipo; i++) {
			Ecriture.ecrireInt(po[ipo]);
			Ecriture.ecrireString("\n");
		}
		Ecriture.fermer(f2);

		// creation du fichier en mnemonique correspondant
		Mnemo.creerFichier(ipo-1, po, nomProg + ".ima");
	}

	public static void main(String argv[]) {
		System.out.println("EDITEUR DE LIENS / PROJET LICENCE");
		System.out.println("---------------------------------");
		System.out.println(""); 
		nbErr = 0;

		// Phase 1 de l'edition de liens
		// -----------------------------static
		lireDescripteurs();		// : lecture des descripteurs a completer si besoin
		nbVarGlob = tabDesc[0].getTailleGlobaux();
		transDon[0] = 0;
		transCode[0] = 0;
		for(int i=1; i<=nMod; i++){
			transDon[i] = transDon[i-1] + tabDesc[i-1].getTailleGlobaux();
			transCode[i] = transCode[i-1] + tabDesc[i-1].getTailleCode();
			nbVarGlob += tabDesc[i].getTailleGlobaux();
		}
		nbDefTotal = 0;
		for(int i =0; i<=nMod; i++){
			for(int j=1; j<= tabDesc[i].getNbDef(); j++){//pour chaque proc declaré
				String nom = tabDesc[i].getDefNomProc(j);
				int ad = tabDesc[i].getDefAdPo(j) + transCode[i];
				int nbParam = tabDesc[i].getDefNbParam(j);

				for(int k=1; k<= nbDefTotal; k++){ // parcours les déf qu'on a déjà enregistré pour vérifié qu'aucune a le même nom que celle qu'on veut ajouter
					if(dicoDefNomProc[k].equals(nomProg)){
						erreur(FATALE, "double définition de" + nomProg);
					}
				}

				nbDefTotal ++;
				dicoDefNomProc[nbDefTotal] = nom;
				dicoDefAdPo[nbDefTotal] = ad;
				dicoDefNbParam[nbDefTotal] = nbParam;

			}
		}


		//Construction de adFinale pour les références 
		for(int indMod = 0; indMod<=nMod; indMod++){
			for(int indRef=1; indRef<=tabDesc[indMod].getNbRef(); indRef++){
				String nomRef = tabDesc[indMod].getRefNomProc(indRef);
				int nbParam = tabDesc[indMod].getRefNbParam(indRef);
				boolean trouve = false;

				for(int indDef=1; indDef<=nbDefTotal; indDef++){
					if(dicoDefNomProc[indDef].equals(nomRef)){
						if(dicoDefNbParam[indDef] != nbParam){
							erreur(FATALE, "mauvais nombre de paramètres pour" + nomRef);
						}

						adFinale[indMod][indRef] = dicoDefAdPo[indDef];
						trouve = true;
						break;
					}
				}
				if(!trouve){
					erreur(FATALE, "reference non resolue pour" + nomRef);
				}
			}
		}
		

		if (nbErr > 0) {
			System.out.println("programme executable non produit");
			System.exit(1);
		}

		// Phase 2 de l'edition de liens
		// -----------------------------
		constMap();				//TODO : ... A COMPLETER ...
		System.out.println("Edition de liens terminee");
	}
}
