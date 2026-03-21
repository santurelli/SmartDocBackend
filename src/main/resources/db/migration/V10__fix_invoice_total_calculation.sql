CREATE OR REPLACE FUNCTION public.get_prezzoscontato_prodottofattura(integer)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$DECLARE
	vIdProdottoFattura ALIAS FOR $1;
	lPrezzoScontato numeric(10,2);
	lAliquotaIva numeric(10,2);
	lSplitPayment d_e_fatture.split_payment%TYPE;
BEGIN

	SELECT COALESCE(split_payment,0)
	  INTO lSplitPayment
	  FROM d_e_fatture
	 WHERE k_d_e_fatture = (SELECT k_d_e_fatture FROM d_e_prodotti_fatture WHERE k_d_e_prodotti_fatture = vIdProdottoFattura);
	
	SELECT get_prezzoscontato_senzaiva_prodottofattura(vIdProdottoFattura)
	  INTO lPrezzoScontato;
	IF COALESCE(lSplitPayment,0) = 0 THEN
		SELECT d_e_aliquoteiva.imposta
		  INTO lAliquotaIva
		  FROM d_e_prodotti_fatture
		  JOIN d_e_aliquoteiva
		    ON d_e_aliquoteiva.k_d_e_aliquoteiva = d_e_prodotti_fatture.k_d_e_aliquoteiva
		  WHERE k_d_e_prodotti_fatture = vIdProdottoFattura;

		lPrezzoScontato := lPrezzoScontato + COALESCE(lPrezzoScontato * lAliquotaIva/100,0);
		if lPrezzoScontato is null then
			lPrezzoScontato := 0;
		end if;
	END IF;
	RETURN lPrezzoScontato;
	
END;$function$
;

-- Ricalcola il totale per tutte le fatture esistenti
UPDATE d_e_fatture SET totale = get_totale_fattura(k_d_e_fatture) WHERE fl_deleted = 0;