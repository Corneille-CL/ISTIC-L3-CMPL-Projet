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
	private static int indVarGlob;
	private static int indIdentAff; //indice dans tabSymb de l'ident qui recoit la valeur
	private static int nbConstLoc; //nombre de constantes locales dans proc
	private static int nbParamDecl; //nombre de paramètres de la proc lors de la déclaraton
	private static int nbVarLoc; //nombre de variables locales dans proc
	private static int nbParamFixe; //nombre de param fixe lors de l'appel
	private static int nbParamMod; //nobre de param fixe lors de l'appel
	private static int indProc; //indice dans tabSymb de proc appelé
	private static int indNbVar; //indice dans tabSymb de nombre de param du proc quand decl
	private static int nbRef;
	private static int nbDef;
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
		nbConstLoc = 0;
		indIdentAff = 0;
		nbParamDecl = 0;
		nbVarLoc = 0;
		nbParamFixe = 0;
		nbParamMod = 0;
		indNbVar = 0;
		nbDef = 0;
		nbRef = 0;

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
			vCour = UtilLex.valEnt;
			tCour = ENT;
			break;
		case 002:
			vCour = -UtilLex.valEnt;
			tCour = ENT;
			break;
		case 003:
			vCour = VRAI;
			tCour = BOOL;
			break;
		case 004:
			vCour = FAUX;
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
				placeIdent(UtilLex.numIdCourant, CONSTANTE, tCour, vCour);
			}
			if(bc != 1){
				nbConstLoc ++;//on compte les constante locales
			}
			break;
		case 104 :
			if(bc == 1){//dans main
				if(presentIdent(1) != 0){
					UtilLex.messErr("ident déjà déclaré");
				}
				placeIdent(UtilLex.numIdCourant, VARGLOBALE, tCour, indVarGlob);
				indVarGlob += 1;
			} else {//dans proc
				if(presentIdent(bc) != 0){
					UtilLex.messErr("ident déjà déclaré");
				}
				placeIdent(UtilLex.numIdCourant, VARLOCALE, tCour, nbParamDecl + 2 + nbVarLoc);// on compte les ra et bp
				nbVarLoc += 1;
			}
			
			break;
		case 105 :
			if (desc.getUnite().equals("programme")) {
				po.produire(RESERVER);
				if (bc == 1) {
					po.produire(indVarGlob); //on reserve tout le temps
					desc.setTailleGlobaux(indVarGlob);
				} else {
					po.produire(nbVarLoc);
				}
			} else {
				if(bc != 1){//on reserve les var loc dans les proc des modules
					po.produire(RESERVER);
					po.produire(nbVarLoc);
				} else {
					desc.setTailleGlobaux(indVarGlob);
				}
			}
			
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
			if(bc == 1){//dans main
				if(tabSymb[indIdentAff].type != tCour){
					UtilLex.messErr("les types ne correspondent pas");
				}
				po.produire(AFFECTERG);
				po.produire(tabSymb[indIdentAff].info);
				if(desc.getUnite().equals("module")){
					po.vecteurTrans(TRANSDON);
					desc.incrNbTansExt();
				}
				
			} else {//dans proc
				if(tabSymb[indIdentAff].categorie == VARGLOBALE){
					po.produire(AFFECTERG);
					po.produire(tabSymb[indIdentAff].info);
					if(desc.getUnite().equals("module")){
						po.vecteurTrans(TRANSDON);
						desc.incrNbTansExt();
					}
				} else {
					po.produire(AFFECTERL);
					po.produire(tabSymb[indIdentAff].info);
					if(desc.getUnite().equals("module")){
						po.vecteurTrans(TRANSDON);
						desc.incrNbTansExt();
					}
					if(tabSymb[indIdentAff].categorie == PARAMMOD){
						po.produire(1);
					} else {
						po.produire(0);
					}
				}
			}
			break;
		
		case 201:
			po.produire(EMPILER);
			po.produire(vCour);
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
				if(desc.getUnite().equals("module")){
					po.vecteurTrans(TRANSDON);
					desc.incrNbTansExt();
				}
			}
			if(eltTabSymb.categorie == VARLOCALE  || eltTabSymb.categorie == PARAMMOD || eltTabSymb.categorie == PARAMFIXE) {
				po.produire(CONTENUL);
				po.produire(tabSymb[presentIdent(1)].info);
				if(eltTabSymb.categorie == VARLOCALE || eltTabSymb.categorie == PARAMFIXE){
					po.produire(0);
				} else {
					po.produire(1);
				}
			}
			tCour = eltTabSymb.type;
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
			if(tabSymb[indIdentAff].type == ENT){
				verifEnt();
			} else {
				verifBool();
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
			
			if(bc == 1){//dans main
				po.produire(AFFECTERG);
				po.produire(tabSymb[indiceIdent].info);
				if(desc.getUnite().equals("module")){
					po.vecteurTrans(TRANSDON);
					desc.incrNbTansExt();
				}
			} else {//dans proc
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
		case 401 ://bsifaux du si
			po.produire(BSIFAUX);
			po.produire(-1);
			if(desc.getUnite().equals("module")){
				po.vecteurTrans(TRANSCODE);
				desc.incrNbTansExt();
			}
			pileRep.empiler(po.getIpo());
			System.out.println("DEBUG : 401");
			break;
		case 402 ://bincond du sinon
			po.produire(BINCOND);
			po.produire(-1);
			if(desc.getUnite().equals("module")){
				po.vecteurTrans(TRANSCODE);
				desc.incrNbTansExt();
			}
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			pileRep.empiler(po.getIpo());
			System.out.println("DEBUG : 402");
			break;
		case 403 ://reprise en fsi
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			System.out.println("DEBUG : 403");
			break;
		
		case 411://prep du cond -1 en pileRep
			pileRep.empiler(-1);
			System.out.println("DEBUG : 411");
			break;
		case 412://empiler bsifaux + pileRep
			po.produire(BSIFAUX);
			po.produire(-1);
			if(desc.getUnite().equals("module")){
				po.vecteurTrans(TRANSCODE);
				desc.incrNbTansExt();
			}
			pileRep.empiler(po.getIpo());
			System.out.println("DEBUG : 412");
			break;
		case 413://rep du bsifaux + @du bincond vers le dernier + emp @ dans pileRep
			po.produire(BINCOND);
			po.modifier(pileRep.depiler(), po.getIpo()+2);
			po.produire(pileRep.depiler());
			if(desc.getUnite().equals("module")){
				po.vecteurTrans(TRANSCODE);
				desc.incrNbTansExt();
			}
			pileRep.empiler(po.getIpo());
			System.out.println("DEBUG : 413");
			break;
		case 414://rep des bincond
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			int nextIpo = pileRep.depiler();
			while (nextIpo != -1){
				int tmpIpo = po.getElt(nextIpo);
				po.modifier(nextIpo, po.getIpo()+1);
				nextIpo = tmpIpo;
			}
			System.out.println("DEBUG : 414");
			
			break;
		
		case 421://ttq
			pileRep.empiler(po.getIpo()+1);//empile adr avant de faire la condition
			System.out.println("DEBUG : 421");
			break;
		case 422://bsifaux du ttq pour la cond
			po.produire(BSIFAUX);
			po.produire(-1);
			if(desc.getUnite().equals("module")){
				po.vecteurTrans(TRANSCODE);
				desc.incrNbTansExt();
			}
			pileRep.empiler(po.getIpo());//empile adr bsifaux et y mettre fin de proc
			System.out.println("DEBUG : 422");
			break;
		case 423://bincond du ttq vers le début
			po.produire(BINCOND);
			po.modifier(pileRep.depiler(), po.getIpo()+2);
			po.produire(pileRep.depiler());
			if(desc.getUnite().equals("module")){
				po.vecteurTrans(TRANSCODE);
				desc.incrNbTansExt();
			}
			System.out.println("DEBUG : 423");
			break;
		
		case 500:
			if(desc.getUnite().equals("programme")){
				po.produire(BINCOND);
				po.produire(-1);
				if(desc.getUnite().equals("module")){
					po.vecteurTrans(TRANSCODE);
					desc.incrNbTansExt();
				}
				System.out.println("DEBUG : 500");
				pileRep.empiler(po.getIpo());// on empile le adressage du bincond
			}
			
			
			break;
		case 501:
			nbDef += 1;
			if(desc.getUnite().equals("module")){
				desc.modifDefAdPo(nbDef, po.getIpo()+1);
			}
			nbParamDecl = 0;
			nbVarLoc = 0;
			nbConstLoc = 0;
			placeIdent(UtilLex.numIdCourant, PROC, NEUTRE, po.getIpo()+1);
			int indDef = desc.presentDef(UtilLex.chaineIdent(UtilLex.numIdCourant));
			if(indDef != 0){
				placeIdent(-1, DEF, NEUTRE, -1);
			} else {
				placeIdent(-1, PRIVEE, NEUTRE, -1);//on connait pas le nb de param
			}
			indNbVar = presentIdent(1)+1;//ou est ce qu'on doit changer tabsymb pour le nb de param
			bc = it+1;
			break;
		case 502:
			if(desc.getUnite().equals("module")){
				desc.modifDefNbParam(nbDef,nbParamDecl);
			}
			tabSymb[indNbVar].info = nbParamDecl;//changer nb de param
			break;
		case 503:
			bc = 1;
			afftabSymb();
			it = it-nbVarLoc-nbConstLoc;
			System.out.println("\n nbVL, nbCL : "+nbVarLoc+", "+nbConstLoc +"\n");
			for (int i = it-nbParamDecl; i <= it; i++) {
				tabSymb[i].code = -1; //on cache les noms des params
			}
			po.produire(RETOUR);
			po.produire(nbParamDecl);
			break;
		case 504:
			placeIdent(UtilLex.numIdCourant, PARAMFIXE, tCour, nbParamDecl);
			nbParamDecl += 1;
			break;
		case 505:
			placeIdent(UtilLex.numIdCourant, PARAMMOD, tCour, nbParamDecl);
			nbParamDecl += 1;
			break;
		case 506:
			if(desc.getUnite().equals("programme")){
				po.modifier(pileRep.depiler(), po.getIpo()+1); //une fois les procs déclaré on redirige le bincond
			
				System.out.println("DEBUG : 506");
			}
			break;
		case 507:
			po.produire(APPEL);
			po.produire(tabSymb[indProc].info);
			po.vecteurTrans(REFEXT);
			desc.incrNbTansExt();
			po.produire(tabSymb[indProc+1].info);
			break;
		case 508:
			nbParamFixe ++;
			if(nbParamFixe>tabSymb[indProc+1].info){
				UtilLex.messErr("nombre de paramètres incorrect");
			}
			if(tabSymb[indProc+1].categorie != REF){
				if(tabSymb[indProc+1+nbParamFixe].categorie == PARAMMOD){
					UtilLex.messErr("nombre de parametre fixes trop élevé");
				}
				if(tCour != tabSymb[indProc+1+nbParamFixe].type){
					//DEBUG IndPROC a 0 lors de pb
					System.out.println("DEBUG indProc : "+indProc);
					System.out.println("DEBUG nbParFixe : "+nbParamFixe+" et type : "+ tabSymb[indProc+1+nbParamFixe].type);
					afftabSymb();
					UtilLex.messErr("paramètre fixe numero "+nbParamFixe+" de mauvais type");
				}
			}
			break;
		case 509:
			nbParamMod ++;
			if(nbParamFixe+nbParamMod>tabSymb[indProc+1].info){
				UtilLex.messErr("nombre de paramètres incorrect");
			}
			if(tabSymb[indProc+1+nbParamFixe+nbParamMod].categorie == PARAMFIXE){
				UtilLex.messErr("nombre de paramètres modifiables trop élevé");
			}
			if(tabSymb[presentIdent(1)].type != tabSymb[indProc+1+nbParamFixe+nbParamMod].type){
				UtilLex.messErr("paramètre modifiable numero "+nbParamMod+" de mauvais type");
			}
			if(tabSymb[presentIdent(1)].categorie == VARGLOBALE){
				po.produire(EMPILERADG);
				if(desc.getUnite().equals("module")){
					if(desc.getUnite().equals("module")){
						po.vecteurTrans(TRANSDON);
						desc.incrNbTansExt();
					}
				}
				po.produire(tabSymb[presentIdent(1)].info);
			}else if(tabSymb[presentIdent(1)].categorie == VARLOCALE || tabSymb[presentIdent(1)].categorie == PARAMMOD){
				po.produire(EMPILERADL);
				po.produire(tabSymb[presentIdent(1)].info);
				if(tabSymb[presentIdent(1)].categorie == VARLOCALE){
					po.produire(0);
				}else{
					po.produire(1);
				}
			}
			
			break;
		case 510:
			nbParamFixe = 0;
			nbParamMod = 0;
			indProc = presentIdent(1);
			System.out.println("DEBUG indPROC 510 : " +indProc);
			break;
		case 511:
			if(nbParamFixe+nbParamMod<tabSymb[indProc+1].info){
				UtilLex.messErr("nombre de paramètres trop petit");
			}
			break;

		case 601:
			desc.setUnite("programme");
		break;
		case 602:
			desc.setUnite("module");
		break;
		case 603:
			nbRef += 1;
			nbParamDecl = 0;
			placeIdent(UtilLex.numIdCourant, PROC, NEUTRE, nbRef);
			desc.ajoutRef(UtilLex.chaineIdent(UtilLex.numIdCourant));
		break;
		case 604:
			nbParamDecl += 1;
		break;
		case 605:
			desc.modifRefNbParam(nbRef, nbParamDecl);
			placeIdent(-1, REF, NEUTRE, nbParamDecl);
		break;
		case 606:
			desc.ajoutDef(UtilLex.chaineIdent(UtilLex.numIdCourant));
		break;
		
		case 999 : 
			afftabSymb(); // affichage de la table des symboles en fin de compilation
			if(desc.getUnite().equals("programme")){
				po.produire(ARRET);
			}
			desc.setTailleCode(po.getIpo());
			po.constGen();
			po.constObj();
			desc.ecrireDesc(UtilLex.nomSource);
			break;

		
		default:
			System.out.println("Point de generation non prevu dans votre liste");
			break;

		}
	}
}