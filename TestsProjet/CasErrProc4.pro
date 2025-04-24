programme test4:		{calcul de factorielle n}

var ent n, fn;

proc fact fixe (ent i) mod (ent fi)
	var ent fi1;
debut
	fi:=1
fin;

debut
	n:=3;
    fn:=6;
    fact(n)(fn);
    nonDefini();        {erreur: faire appel à une procédure qui n'est pas défini}
fin