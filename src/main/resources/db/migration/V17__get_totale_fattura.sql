CREATE OR REPLACE FUNCTION public.get_totale_fattura(bigint)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$DECLARE
	vIdFattura ALIAS FOR $1;
	lTotale numeric (10,2);
	lTotaleSpese numeric (10,2);
	lRivalsa numeric (10,2);
	lIvaRivalsa numeric (10,2);
BEGIN
	select get_totalespese_fattura(vIdFattura) into lTotaleSpese;

	select sum(get_prezzoscontato_prodottofattura(k_d_e_prodotti_fatture))
	  into lTotale
	from d_e_prodotti_fatture
	where k_d_e_fatture = vIdFattura;

	if lTotale is null then
		lTotale := 0;
	end if;

	-- Recupero Rivalsa INPS
	select coalesce(importo_rivalsa_inps, 0) into lRivalsa from d_e_fatture where k_d_e_fatture = vIdFattura;

	-- Calcolo IVA su rivalsa (usa l'aliquota del primo prodotto, fallback a 22%)
	select (lRivalsa * coalesce(ai.imposta, 22) / 100) into lIvaRivalsa
	from d_e_fatture f
	left join d_e_prodotti_fatture pf on pf.k_d_e_fatture = f.k_d_e_fatture
	left join d_e_aliquoteiva ai on ai.k_d_e_aliquoteiva = pf.k_d_e_aliquoteiva
	where f.k_d_e_fatture = vIdFattura
	limit 1;

	if lIvaRivalsa is null then
		lIvaRivalsa := 0;
	end if;

	lTotale := lTotale + lTotaleSpese + lRivalsa + lIvaRivalsa;

	RETURN ROUND(lTotale, 2);

END;$function$;
