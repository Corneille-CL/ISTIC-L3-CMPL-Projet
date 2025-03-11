Fichiers de test fournis pour le Projet compilateur

Les fichiers de tests fournis correspondent à des programmes en langage Projet (donc suffixés par .pro) que votre compilateur doit compiler.

Tests compilation des déclarations et expressions:
	fichier DeclExp-T1.pro : teste un certain nombre de déclarations et expressions
	fichiers DeclExp-Err1.pro à DeclExp-Err5.pro: testent un certain nombre d'erreurs (un type d'erreur par fichier)

Autres tests de compilation:
	fichiers polyPxx-exoyy.pro: exercice yy, page xx du poly de TP
	fichiers polyPxx-exempleyy.pro: exemple (DONT VOUS AVEZ LA SOLUTION dans le poly), page xx du poly de TP 
	fichiers polyPxx-exempleyy.genENS et fichiers polyPxx-exempleyy.objENS: solutions des exemples précédents
	NB: suffixes .gen et .obj modifies en .genENS et .objENS pour que votre compilateur ne les ecrase pas!
	fichiers TDexoxx.pro: exo xx (de la partie C) du TD (DONT VOUS AVEZ LA SOLUTION dans le TD)
RQ: la solution s'entend ici comme le code mnémonique obtenu après compilation.

ATTENTION: l'ensemble des fichiers fournis ne sont que des exemples de programmes à compiler 
et ne permettent pas de tester l'ensemble des possibilités.
En particulier, aucun programme erroné n'est fourni (à part pour les déclarations/expressions).
C'EST A VOUS DE COMPLETER L'ENSEMBLE DES PROGRAMMES A COMPILER (par exemple en créant des variantes
des programmes proposés).


0XX : valeur
1XX : déclarations / affections
2XX : calcul d'expressions
3XX : instructions entrée et sortie

001 : ent pos
002 : ent neg
003 : bool vrai
004 : bool faux

101 : tCour = ent
102 : tCour = bool
103 : ajouter une const a tabSymb de type tCour et de valeur valEnt avec un if en fonction du type
104 : ajouter une var à tabSymb de type tCour et de valeur valEnt avec un if en fonction du type
105 : reserver les variables
106 : sauv l'ident qui doit etre modifié (son adresse sans doute)
107 : affecter le sommet de pile à la variable correspondante

201 : empile la valeur en dure
202 : empile la valeur de l'ident (varglo ou const)
203 : pile OU
204 : pile ET
205 : pile NON
206 : pile Eg
207 : pile DIFF
208 : pile SUP
209 : pile SUPEG
210 : pile INF
211 : pile INFEG
212 : pile ADD
213 : pile SOUS
214 : pile MUL
215 : pile DIV
216 : tCour = ent
217 : tCour = bool
218 : verification du type ent
219 : verification du type bool
220 : verif tCour = type de l'ident affecté

301 : produire lirent ou lirebool en fonction du type
302 : produire ecrent ou ecrbool en fonction du type

401 : produire bsifaux et ligne de reprise ?
402 : on connait ligne de reprise à changer plus haut avec un bincond a fsi
403 : on connait la fin du si on ecris la ou y a un trou
404 : produire bsifaux cond
405 : premier bincond du cond
406 : bincond suivant qui chaines a la suite des autres
407 : resoudre la chaine de bincond
408 : marquer debut expression
409 : prod bsifaux ttq
410 : prod bincond ttq + resolution

1000: exit
