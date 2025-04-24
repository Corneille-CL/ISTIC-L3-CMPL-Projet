programme test2:

const v=10;
var ent n, fn;


proc fact fixe (ent i) mod (ent fi)
	var ent fi1;
debut
	si i=1 alors fi:=1 sinon fact(i-1)(fi1); fi:=i*fi1 fsi
fin;

debut
	lire(n);
	fact(n)(v);   {erreur : mettre une constante pour un paramètre modifiable}
	ecrire(fn);
fin