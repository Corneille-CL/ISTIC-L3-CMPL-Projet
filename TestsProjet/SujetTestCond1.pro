programme simple:	{exemple d'execution dans poly}

	const moinscinq=-5;
	var ent i, n, x, s; bool b;

debut
	lire(n); i:=n; s:=0; b:=faux;
	cond
		i=0: b:=vrai; x:=0,
		i=-1: b:=vrai; x:=1  
	fcond;
	ecrire(s, b);
fin
