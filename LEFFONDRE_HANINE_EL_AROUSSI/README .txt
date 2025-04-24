Trinôme : Leffondre ElAroussi Hanine

---- CE QUI FONCTIONNE ----
- Compilation de programmes  
- Instructions
- Affectation / Declaration
- Lecture / écriture
- Appel
- Les instructions conditionnelles sont opérationnelles
- Boucles 'ttq ... faire ... fait' fonctionnelles
- Procédures (déclaration, appel, paramètres 'fixe'/ 'mod') 
- Table des symboles complète 
- La majorité des points de génération ('PtGen.pt(XXX)') est bien couverte
- Production correcte des instructions Mapile dans 'po'
- La majorité des erreurs sont couvertes pour la compilation basique
- Compilation séparée (pas de gestion d'erreur)
- Edition de liens (pas de gestion d'erreur)


---- CE QUI FONCTIONNE PAS ----
- Gestion des erreurs lors de la compliation séparée + edition de lien
-

---- CE QUI A POSÉ PROBLÈME ----
- Gestion des « cond »
- L'emplacement des points de génération

AIDE POUR COMPRENDRE LA NOMENCLATURE

0XX : Controle de valeur et d'ident
1XX : Déclarations / Affections
2XX : Calcul d'expressions
3XX : Entrée et sortie
4XX : Conditions
5XX : Procédures
6XX : Compilation séparée

001 : Entier positif
002 : Entier négatif
003 : Booleen vrai
004 : Booleen faux

101 : tCour = ent
102 : tCour = bool
103 : ajouter une const a tabSymb
104 : ajouter une var à tabSymb
105 : reserver les variables
106 : mise en mémoire de l'ident qui doit etre modifié
107 : affectation

201 : empile une valeur
202 : empile la valeur de l'ident
203 : OU
204 : ET
205 : NON
206 : Eg
207 : DIFF
208 : SUP
209 : SUPEG
210 : INF
211 : INFEG
212 : ADD
213 : SOUS
214 : MUL
215 : DIV
216 : tCour = ent
217 : tCour = bool
218 : verification du type ent
219 : verification du type bool
220 : verif tCour = type de l'ident affecté

301 : produire lirent ou lirebool et affecter
302 : produire ecrent ou ecrbool

401 : SI ALORS SINON produire bsifaux 
402 : SI ALORS SINON produire bincond + reprise 
403 : SI ALORS SINON reprise
411 : COND preparation reprise
412 : COND bsifaux
413 : COND bincond
414 : COND reprise sans aut
415 : COND reprise avec aut
421 : TTQ preparation 
422 : TTQ bsifaux
423 : TTQ bincond

500 : bincond en prog
501 : init et placeident du proc
502 : mettre nb param
503 : retour de procédure
504 : ajoute un paramfixe dans la table de symboles
505 : ajoute un parammod dans la table des symboles
506 : redirige bincond de 500
507 : appel
508 : verif param fixe
509 : verif param mod
510 : verif appel
511 : verif nb param

601 : init programme
602 : init module
603 : gestion des REF
604 : gestion des params
605 : modif nb param de REF
606 : ajoute DEF

999: exit