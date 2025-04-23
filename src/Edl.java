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
	static int[][] adFinale = new int[MAXMOD +1][MAXREF +1];
	static String[] dicoDefNom = new String[(MAXMOD +1)*MAXDEF +1];
	static int[] dicoDefAdPo = new int[(MAXMOD +1)*MAXDEF +1];
	static int[] dicoDefParam = new int[(MAXMOD +1)*MAXDEF +1]; 
	static int nbDefTotal;
	static int ipo, nMod, nbErr;
	static String nomProg;


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

		nMod = 0;
		while (!s.equals("") && nMod < MAXMOD) {
			System.out.print("nom de module " + (nMod + 1)
					+ " (RC si termine) ");
			s = Lecture.lireString();
			if (!s.equals("")) {
				nMod = nMod + 1;
				tabDesc[nMod] = new Descripteur();
				tabDesc[nMod].lireDesc(s);

				if (!tabDesc[nMod].getUnite().equals("module"))
					erreur(FATALE, "module attendu");
			}
		}
	}


	static void constMap() {
		// f2 = fichier executable .map construit
		OutputStream f2 = Ecriture.ouvrir(nomProg + ".map");
		InputStream f1 = Lecture.ouvrir(nomProg + ".obj");
		if (f2 == null)
			erreur(FATALE, "creation du fichier " + nomProg
					+ ".map impossible");
		// pour construire le code concatene de toutes les unités
		int[] po = new int[(nMod + 1) * MAXOBJ + 1];

		ipo = 0;

		// Récupérer les doublets du programme principale

		int nbTrans = tabDesc[0].getNbTransExt();
		int[][] transExt = new int [nbTrans][2];
		for(int t = 0; t<=nbTrans; t++){
			transExt[t] = new int [] {Lecture.lireInt(f1), Lecture.lireIntln(f1)};
		}

		for(int i=1; i<=nMod; i++){
			//lire dans le fichier
			String nomModule = tabDesc[i].getDefNomProc(i);
			InputStream fModule = Lecture.ouvrir(nomModule + ".obj");
			
			//récupère le nombre de modifications à réaliser 
			nbTrans = tabDesc[i].getNbTransExt();
			transExt = new int [nbTrans][2];
			for(int t = 0; t<=nbTrans; t++){
				transExt[t] = new int [] {Lecture.lireInt(fModule), Lecture.lireIntln(fModule)};
			}
			int adPo = 0;
			int code = 0;
			int indTrans = 0;  
			if( nbTrans != 0){
				adPo = transExt[0][0];
				code =  transExt[0][1]; // type de modification à réaliser
				indTrans ++;
			}

			for(int j=0; j<= tabDesc[i].getTailleCode(); j++){
				po[ipo] = Lecture.lireIntln(fModule);
				if(j == adPo ){
					if( code == 1){ //tranDon
						po[adPo] += transDon[i];
					}else if (code == 2){ //transCode
						po[adPo] += transCode[i];
					}else if(code ==3 ){ //RefExt
						po[adPo] += adFinale[i][adPo]; //revoir
					}else{
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
		
		Ecriture.fermer(f2);

		// creation du fichier en mnemonique correspondant
		Mnemo.creerFichier(ipo, po, nomProg + ".ima");
	}

	public static void main(String argv[]) {
		System.out.println("EDITEUR DE LIENS / PROJET LICENCE");
		System.out.println("---------------------------------");
		System.out.println(""); 
		nbErr = 0;

		// Phase 1 de l'edition de liens
		// -----------------------------static
		lireDescripteurs();		// : lecture des descripteurs a completer si besoin
		transDon[0] = 0;
		transCode[0] = 0;
		for(int i=1; i<=nMod; i++){
			transDon[i] = transDon[i-1] + tabDesc[i-1].getTailleGlobaux();
			transCode[i] = transCode[i-1] + tabDesc[i-1].getTailleCode();
		}
		nbDefTotal = 0;
		for(int i =0; i<=nMod; i++){
			for(int j=1; j<= tabDesc[i].getNbDef(); j++){
				String nom = tabDesc[i].getDefNomProc(j);
				int ad = tabDesc[i].getDefAdPo(j) + transCode[i];
				int nbParam = tabDesc[i].getDefNbParam(j);

				for(int k=1; k<= nbDefTotal; k++){ // parcours les déf qu'on a déjà enregistré pour vérifié qu'aucune a le même nom que celle qu'on veut ajouter
					if(dicoDefNom[k].equals(nomProg)){
						erreur(FATALE, "double définition de" + nomProg);
					}
				}

				nbDefTotal ++;
				dicoDefNom[nbDefTotal] = nom;
				dicoDefAdPo[nbDefTotal] = ad;
				dicoDefParam[nbDefTotal] = nbParam;

			}
		}


		//Construction de adFinale pour les références 
		for(int i = 0; i<=nMod; i++){
			for(int j=1; j<=tabDesc[i].getNbRef(); j++){
				String nom = tabDesc[i].getRefNomProc(j);
				int nbParam = tabDesc[i].getRefNbParam(j);
				boolean trouve = false;

				for(int k=1; k<=nbDefTotal; k++){
					if(dicoDefNom[k].equals(nom)){
						if(dicoDefParam[k] != nbParam){
							erreur(FATALE, "mauvais nombre de paramètres pour" + nom);
						}

						adFinale[i][j] = dicoDefAdPo[k];
						trouve = true;
						break;
					}
				}
				if(!trouve){
					erreur(FATALE, "reference non resolue pour" + nom);
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
