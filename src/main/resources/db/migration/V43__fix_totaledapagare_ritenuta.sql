-- Le funzioni get_totaledapagare_fattura e get_totaledapagare_fatturafornitore calcolavano
-- l'importo da saldare come "totale - totale pagato", ignorando completamente la ritenuta
-- d'acconto: un documento con ritenuta continuava a risultare "da saldare" per l'intero
-- importo lordo anche dopo il pagamento del netto, e non azzerava mai il residuo se il
-- pagamento avveniva solo per il netto (lordo - ritenuta).
--
-- NOTA: queste funzioni esistevano gia' nel database ma non erano mai state tracciate in una
-- migration (create/modificate manualmente in passato). Questa migration le riporta sotto
-- controllo di versione e corregge il bug della ritenuta in un unico passaggio.
--
-- Se il tenant dispone anche di uno schema aggiuntivo con una propria copia di queste funzioni
-- (es. schema "temp_romax" individuato durante il debug), quella copia NON viene toccata da
-- questa migration e va aggiornata manualmente con lo stesso fix, se ancora in uso.

CREATE OR REPLACE FUNCTION public.get_totaledapagare_fatturafornitore(integer)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE
	vIdFatturaFornitore ALIAS FOR $1;
	lTotale numeric(19,2);
	lTotalePagato numeric(10,2);
	lRitenuta numeric(19,2);
BEGIN
	SELECT get_totale_fatturafornitore(vIdFatturaFornitore)
	  INTO lTotale;
	SELECT get_totalepagato_fatturafornitore(vIdFatturaFornitore)
	  INTO lTotalePagato;
	SELECT CASE WHEN fl_ritenuta_acconto = 1 THEN COALESCE(importo_ritenuta_acconto, 0) ELSE 0 END
	  INTO lRitenuta
	  FROM d_e_fatturefornitore
	 WHERE k_d_e_fatturefornitore = vIdFatturaFornitore;

	RETURN COALESCE(lTotale, 0) - COALESCE(lTotalePagato, 0) - COALESCE(lRitenuta, 0);
END;
$function$;

CREATE OR REPLACE FUNCTION public.get_totaledapagare_fattura(integer)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE
	vIdFattura ALIAS FOR $1;
	lTotaleFattura numeric(19,2);
	lTotalePagato numeric(10,2);
	lRitenuta numeric(19,2);
BEGIN
	SELECT get_totale_fattura(vIdFattura)
	  INTO lTotaleFattura;
	SELECT get_totalepagato_fattura(vIdFattura)
	  INTO lTotalePagato;
	SELECT CASE WHEN fl_ritenuta_acconto = 1 THEN COALESCE(importo_ritenuta_acconto, 0) ELSE 0 END
	  INTO lRitenuta
	  FROM d_e_fatture
	 WHERE k_d_e_fatture = vIdFattura;

	RETURN COALESCE(lTotaleFattura, 0) - COALESCE(lTotalePagato, 0) - COALESCE(lRitenuta, 0);
END;
$function$;
