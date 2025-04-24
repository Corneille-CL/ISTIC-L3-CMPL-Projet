programme test7:        {erreur: expression booleenne attendue}

const val = 10; b=faux; 
var ent u, v; 
    bool x;
    ent k;
proc q fixe (ent a) mod (ent b; bool c)
    var ent k;
debut   
    ecrire (5+a*k-val>b et x);
    lire(k);
    c:=k=-1;
    si k>a alors b:=b+1 fsi
fin;

debut
    lire(u); v:=0;
    q(u*val)(v, k);         {erreur: paramètre modifiable de mauvais type}
fin
