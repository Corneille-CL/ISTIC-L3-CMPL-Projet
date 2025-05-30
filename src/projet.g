// Grammaire du langage PROJET
// CMPL L3info 
// Nathalie Girard, Veronique Masson, Laurent Perraudeau
// il convient d'y inserer les appels a {PtGen.pt(k);}
// relancer Antlr apres chaque modification et raffraichir le projet Eclipse le cas echeant

// attention l'analyse est poursuivie apres erreur si l'on supprime la clause rulecatch

// {PtGen.pt(0);}

grammar projet;

options {
  language=Java; k=1;
 }

@header {           
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileInputStream;
} 


// partie syntaxique :  description de la grammaire //
// les non-terminaux doivent commencer par une minuscule


@members {

 
// variables globales et methodes utiles a placer ici
  
}
// la directive rulecatch permet d'interrompre l'analyse a la premiere erreur de syntaxe
@rulecatch {
catch (RecognitionException e) {reportError (e) ; throw e ; }}


unite  :  {PtGen.pt(601);} unitprog {PtGen.pt(999);} EOF
      |   {PtGen.pt(602);} unitmodule {PtGen.pt(999);} EOF
  ;
  
unitprog
  : 'programme' ident ':'  
     declarations  
     corps { System.out.println("succes, arret de la compilation "); }
  ;
  
unitmodule
  : 'module' ident ':' 
     declarations   
  ;
  
declarations
  : partiedef? partieref? consts? vars? decprocs? 
  ;
  
partiedef
  : 'def' ident {PtGen.pt(606);} (',' ident {PtGen.pt(606);} )* ptvg
  ;
  
partieref: 'ref'  specif (',' specif)* ptvg
  ;
  
specif  : ident {PtGen.pt(603);} ( 'fixe' '(' type {PtGen.pt(604);} ( ',' type {PtGen.pt(604);} )* ')' )? 
                 ( 'mod'  '(' type {PtGen.pt(604);} ( ',' type {PtGen.pt(604);} )* ')' )? {PtGen.pt(605);}
  ;
  
consts  : 'const' ( ident '=' valeur {PtGen.pt(103);} ptvg  ) +
  ;
  
vars  : 'var' ( type ident {PtGen.pt(104);}  ( ','  ident {PtGen.pt(104);} )* ptvg  )+ {PtGen.pt(105);}
  ;
  
type  : 'ent' {PtGen.pt(101);}  
  |     'bool' {PtGen.pt(102);}
  ;
  
decprocs: {PtGen.pt(500);} (decproc ptvg)+  {PtGen.pt(506);}
  ;
  
decproc :  'proc' ident {PtGen.pt(501);} parfixe? parmod? {PtGen.pt(502);} consts? vars? corps  {PtGen.pt(503);}
  ;
  
ptvg  : ';'
  | 
  ;
  
corps : 'debut' instructions 'fin'
  ;
  
parfixe: 'fixe' '(' pf ( ';' pf)* ')'
  ;
  
pf  : type ident  {PtGen.pt(504);} ( ',' ident  {PtGen.pt(504);} )*  
  ;

parmod  : 'mod' '(' pm ( ';' pm)* ')'
  ;
  
pm  : type ident  {PtGen.pt(505);} ( ',' ident  {PtGen.pt(505);} )*
  ;
  
instructions
  : instruction ( ';' instruction)*
  ;
  
instruction
  : inssi
  | inscond
  | boucle
  | lecture
  | ecriture
  | affouappel
  |
  ;
  
inssi : 'si' expression {PtGen.pt(219);} {PtGen.pt(401);}'alors' instructions ('sinon' {PtGen.pt(402);} instructions)? 'fsi' {PtGen.pt(403);}
  ;
  
inscond : 'cond' {PtGen.pt(411);} expression {PtGen.pt(219);} ':' {PtGen.pt(412);} instructions 
          (',' {PtGen.pt(413);} expression {PtGen.pt(219);} ':' {PtGen.pt(412);} instructions)* 
          ( 'aut' {PtGen.pt(413);} instructions  {PtGen.pt(415);} |  {PtGen.pt(414);} ) 
          'fcond' 
  ;
  
boucle  : 'ttq' {PtGen.pt(421);} expression {PtGen.pt(219);}{PtGen.pt(422);} 'faire' instructions {PtGen.pt(423);} 'fait'
  ;
  
lecture: 'lire' '(' ident {PtGen.pt(301);} ( ',' ident {PtGen.pt(301);} )* ')' 
  ;
  
ecriture: 'ecrire' '(' expression {PtGen.pt(302);} ( ',' expression {PtGen.pt(302);} )* ')'
   ;
  
affouappel
  : ident  ( {PtGen.pt(106);} ':=' expression {PtGen.pt(107);} {PtGen.pt(220);}
            | {PtGen.pt(510);} (effixes (effmods)? {PtGen.pt(511);})?  {PtGen.pt(507);}
           )
  ;
  
effixes : '(' (expression {PtGen.pt(508);} (',' expression {PtGen.pt(508);} )*)? ')'
  ;
  
effmods :'(' (ident {PtGen.pt(509);} (',' ident {PtGen.pt(509);} )*)? ')'
  ; 
  
expression: (exp1) ('ou'{PtGen.pt(219);} exp1 {PtGen.pt(219);} {PtGen.pt(203);} )* 
  ;
  
exp1  : exp2 ('et'{PtGen.pt(219);} exp2 {PtGen.pt(219);} {PtGen.pt(204);} )*
  ;
  
exp2  : 'non' exp2 {PtGen.pt(219);}  {PtGen.pt(205);} 
  | exp3
  ;
  
exp3  : exp4 
  ( '=' {PtGen.pt(218);} exp4 {PtGen.pt(218);} {PtGen.pt(217);}  {PtGen.pt(206);} 
  | '<>' {PtGen.pt(218);} exp4 {PtGen.pt(218);} {PtGen.pt(217);} {PtGen.pt(207);} 
  | '>'  {PtGen.pt(218);} exp4 {PtGen.pt(218);} {PtGen.pt(217);} {PtGen.pt(208);} 
  | '>=' {PtGen.pt(218);} exp4 {PtGen.pt(218);} {PtGen.pt(217);} {PtGen.pt(209);} 
  |  '<'  {PtGen.pt(218);} exp4 {PtGen.pt(218);} {PtGen.pt(217);} {PtGen.pt(210);} 
  |  '<='{PtGen.pt(218);}  exp4 {PtGen.pt(218);} {PtGen.pt(217);} {PtGen.pt(211);} 
  ) ?
  ;
  
exp4  : exp5 
        (  '+'{PtGen.pt(218);} exp5 {PtGen.pt(218);}  {PtGen.pt(212);} 
        |  '-' {PtGen.pt(218);} exp5 {PtGen.pt(218);}  {PtGen.pt(213);} 
        )*
  ;
  
exp5  : primaire 
        (    '*'{PtGen.pt(218);}   primaire  {PtGen.pt(218);} {PtGen.pt(214);} 
          |  'div'{PtGen.pt(218);} primaire  {PtGen.pt(218);} {PtGen.pt(215);} 
        )*
  ;
  
primaire: valeur {PtGen.pt(201);} 
  |  ident {PtGen.pt(202);} 
  | '(' expression ')'
  ;
  
valeur  :  nbentier {PtGen.pt(001);} 
  | {PtGen.pt(216);}'+' nbentier {PtGen.pt(001);} 
  | {PtGen.pt(216);} '-' nbentier {PtGen.pt(002);} 
  | {PtGen.pt(217);} 'vrai' {PtGen.pt(003);} 
  | {PtGen.pt(217);} 'faux' {PtGen.pt(004);} 
  ;

// partie lexicale  : cette partie ne doit pas etre modifiee  //
// les unites lexicales de ANTLR doivent commencer par une majuscule
// Attention : ANTLR n'autorise pas certains traitements sur les unites lexicales, 
// il est alors ncessaire de passer par un non-terminal intermediaire 
// exemple : pour l'unit lexicale INT, le non-terminal nbentier a du etre introduit
 
      
nbentier  :   INT { UtilLex.valEnt = Integer.parseInt($INT.text);}; // mise a jour de valEnt

ident : ID  { UtilLex.traiterId($ID.text); } ; // mise a jour de numIdCourant
     // tous les identificateurs seront places dans la table des identificateurs, y compris le nom du programme ou module
     // (NB: la table des symboles n'est pas geree au niveau lexical mais au niveau du compilateur)
        
  
ID  :   ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ; 
     
// zone purement lexicale //

INT :   '0'..'9'+ ;
WS  :   (' '|'\t' |'\r')+ {skip();} ; // definition des "blocs d'espaces"
RC  :   ('\n') {UtilLex.incrementeLigne(); skip() ;} ; // definition d'un unique "passage a la ligne" et comptage des numeros de lignes

COMMENT
  :  '\{' (.)* '\}' {skip();}   // toute suite de caracteres entouree d'accolades est un commentaire
  |  '#' ~( '\r' | '\n' )* {skip();}  // tout ce qui suit un caractere diese sur une ligne est un commentaire
  ;

// commentaires sur plusieurs lignes
ML_COMMENT    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;	   



	   
