-- Migration to fix the calculation of line totals in get_prezzoscontato_senzaiva_prodottofattura
-- This function was returning 0 for items without a master product ID (fuori magazzino) because the prezzo_scontato column was null/zero.

CREATE OR REPLACE FUNCTION public.get_prezzoscontato_senzaiva_prodottofattura(integer)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$DECLARE
	vIdProdottoFattura ALIAS FOR $1;
	lPrezzoScontato numeric(10,2);
BEGIN
	SELECT prezzo_scontato
	  INTO lPrezzoScontato
	  FROM d_e_prodotti_fatture
	 WHERE k_d_e_prodotti_fatture = vIdProdottoFattura; 

    -- FALLBACK: If the stored total is zero but price/quantity are present, calculate it on the fly
	IF lPrezzoScontato IS NULL OR lPrezzoScontato = 0 THEN
		SELECT (prezzo * quantita)
          INTO lPrezzoScontato
          FROM d_e_prodotti_fatture
         WHERE k_d_e_prodotti_fatture = vIdProdottoFattura;
	END IF;

	IF lPrezzoScontato IS NULL THEN
		lPrezzoScontato := 0;
	END IF;
	RETURN lPrezzoScontato;
END;$function$;
