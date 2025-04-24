programme test1: 
    var ent a;

    proc p fixe (ent x) mod (ent y)
    debut 
        y:= x + 1;

    fin;


debut 
    p();    {erreur : il manque un paramètre en entrée }
fin 