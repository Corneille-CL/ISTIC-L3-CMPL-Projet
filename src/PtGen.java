/*********************************************************************************
 * VARIABLES ET METHODES FOURNIES PAR LA CLASSE UtilLex (cf libClass_Projet)     *
 *       complement à l'ANALYSEUR LEXICAL produit par ANTLR                      *
 *                                                                               *
 *                                                                               *
 *   nom du programme compile, sans suffixe : String UtilLex.nomSource           *
 *   ------------------------                                                    *
 *                                                                               *
 *   attributs lexicaux (selon items figurant dans la grammaire):                *
 *   ------------------                                                          *
 *     int UtilLex.valEnt = valeur du dernier nombre entier lu (item nbentier)   *
 *     int UtilLex.numIdCourant = code du dernier identificateur lu (item ident) *
 *                                                                               *
 *                                                                               *
 *   methodes utiles :                                                           *
 *   ---------------                                                             *
 *     void UtilLex.messErr(String m)  affichage de m et arret compilation       *
 *     String UtilLex.chaineIdent(int numId) delivre l'ident de codage numId     *
 *     void afftabSymb()  affiche la table des symboles                          *
 *********************************************************************************/


import java.io.*;

/**
 * classe de mise en oeuvre du compilateur
 * =======================================
 * (verifications semantiques + production du code objet)
 * 
 * @author Girard, Masson, Perraudeau
 * @author El Aroussi, Hanine, Leffondré
 *
 */

public class PtGen {
    

    // constantes manipulees par le compilateur
    // ----------------------------------------

	private static final int 
	
	// taille max de la table des symboles
	MAXSYMB=300,

	// codes MAPILE :
	RESERVER=1,EMPILER=2,CONTENUG=3,AFFECTERG=4,OU=5,ET=6,NON=7,INF=8,
	INFEG=9,SUP=10,SUPEG=11,EG=12,DIFF=13,ADD=14,SOUS=15,MUL=16,DIV=17,
	BSIFAUX=18,BINCOND=19,LIRENT=20,LIREBOOL=21,ECRENT=22,ECRBOOL=23,
	ARRET=24,EMPILERADG=25,EMPILERADL=26,CONTENUL=27,AFFECTERL=28,
	APPEL=29,RETOUR=30,

	// codes des valeurs vrai/faux
	VRAI=1, FAUX=0,

    // types permis :
	ENT=1,BOOL=2,NEUTRE=3,

	// categories possibles des identificateurs :
	CONSTANTE=1,VARGLOBALE=2,VARLOCALE=3,PARAMFIXE=4,PARAMMOD=5,PROC=6,
	DEF=7,REF=8,PRIVEE=9,

    //valeurs possible du vecteur de translation 
    TRANSDON=1,TRANSCODE=2,REFEXT=3;


    // utilitaires de controle de type
    // -------------------------------
    /**
     * verification du type entier de l'expression en cours de compilation 
     * (arret de la compilation sinon)
     */
	private static void verifEnt() {
		if (tCour != ENT)
			UtilLex.messErr("expression entiere attendue");
	}
	/**
	 * verification du type booleen de l'expression en cours de compilation 
	 * (arret de la compilation sinon)
	 */
	private static void verifBool() {
		if (tCour != BOOL)
			UtilLex.messErr("expression booleenne attendue");
	}

    // pile pour gerer les chaines de reprise et les branchements en avant
    // -------------------------------------------------------------------

    private static TPileRep pileRep;  


    // production du code objet en memoire
    // -----------------------------------

    private static ProgObjet po;
    
    
    // COMPILATION SEPAREE 
    // -------------------
    //
    /** 
     * modification du vecteur de translation associe au code produit 
     * + incrementation attribut nbTransExt du descripteur
     *  NB: effectue uniquement si c'est une reference externe ou si on compile un module
     * @param valeur : TRANSDON, TRANSCODE ou REFEXT
     */
    private static void modifVecteurTrans(int valeur) {
		if (valeur == REFEXT || desc.getUnite().equals("module")) {
			po.vecteurTrans(valeur);
			desc.incrNbTansExt();
		}
	}
    // descripteur associe a un programme objet (compilation separee)
    private static Descripteur desc;

     
    // autres variables fournies
    // -------------------------
    
 // MERCI de renseigner ici un nom pour le trinome, constitue EXCLUSIVEMENT DE LETTRES
    public static String trinome="LeffondreElAroussiHanine";
    
    private static int tCour; // type de l'expression compilee
    private static int vCour; // sert uniquement lors de la compilation d'une valeur (entiere ou boolenne)
   
    // TABLE DES SYMBOLES
    // ------------------
    //
    private static EltTabSymb[] tabSymb = new EltTabSymb[MAXSYMB + 1];
    
    // it = indice de remplissage de tabSymb
    // bc = bloc courant (=1 si le bloc courant est le programme principal)
	private static int it, bc;

	//VARIABLES PAR NOUS
	private static int valAct; // valeur actuelle de l'item pour les declarations et expressions
	private static int indVarGlob;
	private static int indIdentAff;
	private static int nbConst;
	private static int nbParam;
	private static int numIdentProc;
	private static boolean dansProc;
	private static int nbVarLoc;
	//TODO : initialiser les belles variables
	/** 
	 * utilitaire de recherche de l'ident courant (ayant pour code UtilLex.numIdCourant) dans tabSymb
	 * 
	 * @param borneInf : recherche de l'indice it vers borneInf (=1 si recherche dans tout tabSymb)
	 * @return : indice de l'ident courant (de code UtilLex.numIdCourant) dans tabSymb (O si absence)
	 */
	private static int presentIdent(int borneInf) {
		int i = it;
		while (i >= borneInf && tabSymb[i].code != UtilLex.numIdCourant)
			i--;
		if (i >= borneInf)
			return i;
		else
			return 0;
	}

	/**
	 * utilitaire de placement des caracteristiques d'un nouvel ident dans tabSymb
	 * 
	 * @param code : UtilLex.numIdCourant de l'ident
	 * @param cat : categorie de l'ident parmi CONSTANTE, VARGLOBALE, PROC, etc.
	 * @param type : ENT, BOOL ou NEUTRE
	 * @param info : valeur pour une constante, ad d'exécution pour une variable, etc.
	 */
	private static void placeIdent(int code, int cat, int type, int info) {
		if (it == MAXSYMB)
			UtilLex.messErr("debordement de la table des symboles");
		it = it + 1;
		tabSymb[it] = new EltTabSymb(code, cat, type, info);
	}

	/**
	 *  utilitaire d'affichage de la table des symboles
	 */
	private static void afftabSymb() { 
		System.out.println("       code           categorie      type    info");
		System.out.println("      |--------------|--------------|-------|----");
		for (int i = 1; i <= it; i++) {
			if (i == bc) {
				System.out.print("bc=");
				Ecriture.ecrireInt(i, 3);
			} else if (i == it) {
				System.out.print("it=");
				Ecriture.ecrireInt(i, 3);
			} else
				Ecriture.ecrireInt(i, 6);
			if (tabSymb[i] == null)
				System.out.println(" reference NULL");
			else
				System.out.println(" " + tabSymb[i]);
		}
		System.out.println();
	}
    

	/**
	 *  initialisations A COMPLETER SI BESOIN
	 *  -------------------------------------
	 */
	public static void initialisations() {
	
		// indices de gestion de la table des symboles
		it = 0;
		bc = 1;
		
		// pile des reprises pour compilation des branchements en avant
		pileRep = new TPileRep(); 
		// programme objet = code Mapile de l'unite en cours de compilation
		po = new ProgObjet();
		// COMPILATION SEPAREE: desripteur de l'unite en cours de compilation
		desc = new Descripteur();
		
		// initialisation necessaire aux attributs lexicaux
		UtilLex.initialisation();
	
		// initialisation du type de l'expression courante
		tCour = NEUTRE;

		indVarGlob = 0;
		nbConst = 0;
		indIdentAff = 0;
		nbParam = 0;
		numIdentProc = 0;
		dansProc = false;
		nbVarLoc = 0;

		//TODO si necessaire

	} // initialisations

	/**
	 *  code des points de generation A COMPLETER
	 *  -----------------------------------------
	 * @param numGen : numero du001 point de generation a executer
	 */
	public static void pt(int numGen) {
		switch (numGen) {
		case 0:
			initialisations();
			break;
		case 001:
			valAct = UtilLex.valEnt;
			tCour = ENT;
			break;
		case 002:
			valAct = -UtilLex.valEnt;
			tCour = ENT;
			break;
		case 003:
			valAct = 1;
			tCour = BOOL;
			break;
		case 004:
			valAct = 0;
			tCour = BOOL;
			break;
		
		case 101 :
			tCour = ENT;
			break;
		case 102 :
			tCour = BOOL;
			break;
		case 103 :
			if(presentIdent(1) != 0){
				UtilLex.messErr("ident déjà déclaré");
			}else{
				placeIdent(UtilLex.numIdCourant, CONSTANTE, tCour, valAct);
				nbConst ++;
			}
			break;
		case 104 ://TODO verif si on est dans proc et changer varloc
			if(!dansProc){
				if(presentIdent(1) != 0){
					UtilLex.messErr("ident déjà déclaré");
				}else{
					placeIdent(UtilLex.numIdCourant, VARGLOBALE, tCour, indVarGlob);
					indVarGlob += 1;
				}
			} else {
				//une var locale peut cacher une var globale donc ok si elle est deja la pe pas enfaite
				placeIdent(UtilLex.numIdCourant, VARLOCALE, tCour, nbParam + 2 + nbVarLoc);// on compte les ra et bp
				afftabSymb();
				nbVarLoc += 1;
			}
			
			break;
		case 105 :
			po.produire(RESERVER);
			po.produire(indVarGlob);
			break;
		case 106 :
			indIdentAff = presentIdent(1);
			if(indIdentAff == 0){
				UtilLex.messErr("ident non déclaré");
			}
			if(tabSymb[indIdentAff].categorie == CONSTANTE){
				UtilLex.messErr("une constante ne peut être modifiée");
			}
			if(tabSymb[indIdentAff].categorie == PARAMFIXE){
				UtilLex.messErr("un paramètre fixe ne peut être modifiée");
			}
			
			break;
		case 107 :
			if(!dansProc){
				if(tabSymb[indIdentAff].type != tCour){
					//UtilLex.messErr("les types ne correspondent pas");
				}
				po.produire(AFFECTERG);
				po.produire(tabSymb[indIdentAff].info);
			} else {
				po.produire(AFFECTERL);
				po.produire(tabSymb[indIdentAff].info);
				if(tabSymb[indIdentAff].categorie == PARAMMOD || tabSymb[indIdentAff].categorie == VARLOCALE){
					po.produire(0);
				} else {
					po.produire(1);
				}
			}
			
			break;
		
		case 201:
			po.produire(EMPILER);
			po.produire(valAct);
			break;
		case 202:
			EltTabSymb eltTabSymb = tabSymb[presentIdent(1)];
			if(eltTabSymb.categorie == CONSTANTE){
				po.produire(EMPILER);
				po.produire(tabSymb[presentIdent(1)].info);
			} 
			if(eltTabSymb.categorie == VARGLOBALE) {
				po.produire(CONTENUG);
				po.produire(tabSymb[presentIdent(1)].info);
			}
			if(eltTabSymb.categorie == VARLOCALE) {
				po.produire(CONTENUL);
				po.produire(tabSymb[presentIdent(1)].info);
				if(eltTabSymb.categorie == PARAMMOD || eltTabSymb.categorie == VARLOCALE){
					po.produire(0);
				} else {
					po.produire(1);
				}
			}
			
			tCour = tabSymb[presentIdent(1)].type;
			break;
		case 203:
			po.produire(OU);
			break;
		case 204:
			po.produire(ET);
			break;
		case 205:
			po.produire(NON);
			break;
		case 206:
			po.produire(EG);
			
			break;
		case 207:
			po.produire(DIFF);
			break;
		case 208:
			po.produire(SUP);
			break;
		case 209:
			po.produire(SUPEG);
			break;
		case 210:
			po.produire(INF);
			break;
		case 211:
			po.produire(INFEG);
			break;
		case 212:
			po.produire(ADD);
			break;
		case 213:
			po.produire(SOUS);
			break;
		case 214:
			po.produire(MUL);
			break;
		case 215:
			po.produire(DIV);
			break;
		case 216:
			tCour = ENT;
			break;
		case 217:
			tCour = BOOL;
			break;
		case 218:
			verifEnt();
			break;
		case 219:
			verifBool();
			break;
		case 220:
			if(false){
				if(tabSymb[indIdentAff].type == ENT){
					verifEnt();
				} else {
					verifBool();
				}
			}
			
			break;

		case 301:
			int indiceIdent = presentIdent(1);
			if(indiceIdent == 0){
				UtilLex.messErr("variable non déclarée");
			}
			if(tabSymb[indiceIdent].categorie == CONSTANTE){
				UtilLex.messErr("une constante ne peut être modifiée");
			}
			if(tabSymb[indiceIdent].categorie == PARAMFIXE){
				UtilLex.messErr(("un paramètre fixe ne peut être modifié"));
			}
			if(tabSymb[indiceIdent].type == BOOL){
				po.produire(LIREBOOL);
			} else {
				po.produire(LIRENT);
			}
			
			if(!dansProc){
				po.produire(AFFECTERG); //TODO pb avec les procs
				po.produire(tabSymb[indiceIdent].info);
			} else {
				po.produire(AFFECTERL);
				po.produire(tabSymb[indiceIdent].info);
				if(tabSymb[indiceIdent].categorie == PARAMMOD || tabSymb[indiceIdent].categorie == VARLOCALE){
					po.produire(0);
				} else {
					po.produire(1);
				}
			}
			
			break;
		case 302:
			if(presentIdent(1) == 0){
				UtilLex.messErr("variable non déclarée");
			}
			if(tabSymb[presentIdent(1) ].type == ENT){
				po.produire(ECRENT);
			} else {
				po.produire(ECRBOOL);
			}
			break;

		case 401:
			pileRep.empiler(-1);
			break;
		case 402:
			po.produire(BSIFAUX);
			po.produire(-1);
			pileRep.empiler(po.getIpo());
			break;
		case 403:
			po.produire(BINCOND);
			po.modifier(pileRep.depiler(), po.getIpo()+2);
			po.produire(pileRep.depiler());
			pileRep.empiler(po.getIpo());
			break;
		case 404:
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			int nextIpo = pileRep.depiler();
			while (nextIpo != -1){
				int tmpIpo = po.getElt(nextIpo);
				po.modifier(nextIpo, po.getIpo()+1);
				nextIpo = tmpIpo;
			}
			break;
		case 408:
			pileRep.empiler(po.getIpo()+1);
			break;
		
		case 410:
			po.produire(BINCOND);
			po.modifier(pileRep.depiler(), po.getIpo()+2);
			po.produire(pileRep.depiler());
			break;

		case 501:
			po.produire(BINCOND);
			po.produire(-1);
			pileRep.empiler(po.getIpo());// on empile le adressage du bincond
			dansProc = true;
			nbParam = 0;
			nbVarLoc = 0;
			placeIdent(UtilLex.numIdCourant, PROC, NEUTRE, po.getIpo());
			placeIdent(-1, PRIVEE, NEUTRE, -1);//on connait pas le nb de param
			pileRep.empiler(presentIdent(1)+1);//on empile en reprise ou est ce qu'on doit changer tabsymb
			bc = it+1;
			break;
		case 502:
			tabSymb[pileRep.depiler()].info = nbParam;
			break;
		case 503:
			
			bc = 1;
			it = it-nbVarLoc;
			for (int i = it-nbParam; i <= it; i++) {
				tabSymb[i].code = -1; //on cache les noms des params
			}
			
			po.produire(RETOUR);
			po.produire(nbParam);
			break;
		case 504:
			placeIdent(UtilLex.numIdCourant, PARAMFIXE, tCour, nbParam);
			nbParam += 1;
			break;
		case 505:
			placeIdent(UtilLex.numIdCourant, PARAMMOD, tCour, nbParam);
			nbParam += 1;
			break;
		case 506:
			po.modifier(pileRep.depiler(), po.getIpo()+1); //une fois les procs déclaré on redirige le bincond
			dansProc = false;
			break;
		case 507:
			if(tabSymb[indIdentAff].categorie == PROC){
				po.produire(APPEL);
				po.produire(tabSymb[indIdentAff].info);
				po.produire(tabSymb[indIdentAff+1].info);
			}
			break;
		case 508:
			//empiler la valeur 
			break;
		case 509:
			//empiler l'adr
			if(tCour != tabSymb[presentIdent(1)].type){
				UtilLex.messErr("type du paramètre incorrect");
			}
			break;
		case 999 : 
			afftabSymb(); // affichage de la table des symboles en fin de compilation
			po.produire(ARRET);
			po.constGen();
			po.constObj();
			break;

		
		default:
			System.out.println("Point de generation non prevu dans votre liste");
			break;

		}
	}
}