programme exo6:		{calcul de factorielle n}

var ent n, fn;

proc fact fixe (ent i) mod (ent fi)
	var ent fi1;
        bool t; 
debut
	si i=1 alors fi:=1 sinon fact(i-t)(fi1); fi:=i*fi1 fsi          {erreur: expression type entière attendue}
fin;

debut
	lire(n);
	fact(n)(fn); 
	ecrire(fn);
fin
