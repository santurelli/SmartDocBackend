CREATE OR REPLACE FUNCTION public.get_totale_disponibile(integer, integer)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$DECLARE
	vIdProdotto ALIAS FOR $1;
	vIdMagazzino ALIAS FOR $2;
	lTotale numeric (10,3);
	lTotaleIngresso numeric (10,3);
	lTotaleUscita numeric (10,3);
BEGIN
	SELECT sum(quantita)
	  INTO lTotaleIngresso
	  FROM (
		SELECT sum(quantita) AS quantita
		  FROM d_e_prodotti_notecredito
		  JOIN d_e_notecredito
		    ON d_e_prodotti_notecredito.k_d_e_notecredito = d_e_notecredito.k_d_e_notecredito
		 WHERE d_e_notecredito.fl_deleted = 0
		   AND d_e_prodotti_notecredito.k_d_e_prodotti = vIdProdotto
	           AND d_e_prodotti_notecredito.fl_scarica = 1
	           AND (vIdMagazzino IS NULL OR d_e_notecredito.k_d_e_magazzini IS NULL OR d_e_notecredito.k_d_e_magazzini = vIdMagazzino)
	   UNION
		SELECT SUM(quantita) AS quantita
		  FROM d_e_movimenti_magazzino
		 WHERE d_e_movimenti_magazzino.fl_deleted = 0
		   AND d_e_movimenti_magazzino.tipo_movimento = 'I'
		   AND d_e_movimenti_magazzino.k_d_e_prodotti = vIdProdotto
	           AND (vIdMagazzino IS NULL OR d_e_movimenti_magazzino.k_d_e_magazzini IS NULL OR d_e_movimenti_magazzino.k_d_e_magazzini = vIdMagazzino)
          UNION
		SELECT SUM(quantita) AS quantita
		  FROM d_e_prodotti_bollecarico
		  JOIN d_e_bollecarico
		    ON d_e_prodotti_bollecarico.k_d_e_bollecarico = d_e_bollecarico.k_d_e_bollecarico
		 WHERE d_e_bollecarico.fl_deleted = 0
		   AND d_e_prodotti_bollecarico.k_d_e_prodotti = vIdProdotto
	           AND (vIdMagazzino IS NULL OR d_e_bollecarico.k_d_e_magazzini IS NULL OR d_e_bollecarico.k_d_e_magazzini = vIdMagazzino)
	       ) a;

	IF lTotaleIngresso IS NULL THEN
		lTotaleIngresso := 0;
	END IF;

	SELECT sum(quantita)
	  INTO lTotaleUscita
	  FROM (
		SELECT sum(quantita) AS quantita
		  FROM d_e_prodotti_fatture
		  JOIN d_e_fatture
		    ON d_e_prodotti_fatture.k_d_e_fatture = d_e_fatture.k_d_e_fatture
		 WHERE d_e_fatture.fl_deleted = 0
		   AND d_e_prodotti_fatture.k_d_e_prodotti = vIdProdotto
	           AND d_e_prodotti_fatture.fl_scarica = 1
	           AND (vIdMagazzino IS NULL OR d_e_fatture.k_d_e_magazzini IS NULL OR d_e_fatture.k_d_e_magazzini = vIdMagazzino)
	   UNION
		SELECT SUM(quantita) AS quantita
		  FROM d_e_movimenti_magazzino
		 WHERE d_e_movimenti_magazzino.fl_deleted = 0
		   AND d_e_movimenti_magazzino.tipo_movimento = 'U'
		   AND d_e_movimenti_magazzino.k_d_e_prodotti = vIdProdotto
	           AND (vIdMagazzino IS NULL OR d_e_movimenti_magazzino.k_d_e_magazzini IS NULL OR d_e_movimenti_magazzino.k_d_e_magazzini = vIdMagazzino)
          UNION
		SELECT SUM(quantita) AS quantita
		  FROM d_e_prodotti_ddt
		  JOIN d_e_ddt
		    ON d_e_prodotti_ddt.k_d_e_ddt = d_e_ddt.k_d_e_ddt
		 WHERE d_e_ddt.fl_deleted = 0
		   AND d_e_prodotti_ddt.k_d_e_prodotti = vIdProdotto
	           AND d_e_prodotti_ddt.fl_scarica = 1
	           AND (vIdMagazzino IS NULL OR d_e_ddt.k_d_e_magazzini IS NULL OR d_e_ddt.k_d_e_magazzini = vIdMagazzino)
	  UNION
		SELECT SUM(quantita) AS quantita
		  FROM d_e_prodotti_notecreditofornitore
		  JOIN d_e_notecreditofornitore
		    ON d_e_prodotti_notecreditofornitore.k_d_e_notecreditofornitore = d_e_notecreditofornitore.k_d_e_notecreditofornitore
		 WHERE d_e_notecreditofornitore.fl_deleted = 0
		   AND d_e_prodotti_notecreditofornitore.k_d_e_prodotti = vIdProdotto
	           AND d_e_prodotti_notecreditofornitore.fl_scarica = 1
	           AND (vIdMagazzino IS NULL OR d_e_notecreditofornitore.k_d_e_magazzini IS NULL OR d_e_notecreditofornitore.k_d_e_magazzini = vIdMagazzino)
	  UNION
		SELECT SUM(quantita) AS quantita
		  FROM d_e_prodotti_scontrini
		  JOIN d_e_scontrini
		    ON d_e_prodotti_scontrini.k_d_e_scontrini = d_e_scontrini.k_d_e_scontrini
		 WHERE d_e_scontrini.fl_deleted = 0
		   AND d_e_prodotti_scontrini.k_d_e_prodotti = vIdProdotto
	           AND d_e_prodotti_scontrini.fl_scarica = 1
	           AND (vIdMagazzino IS NULL OR d_e_scontrini.k_d_e_magazzini IS NULL OR d_e_scontrini.k_d_e_magazzini = vIdMagazzino)
	       ) a;

	IF lTotaleUscita IS NULL THEN
		lTotaleUscita := 0;
	END IF;

	lTotale := lTotaleIngresso - lTotaleUscita;

	IF lTotale IS NULL THEN
		lTotale := 0;
	END IF;

	RETURN lTotale;

END;$function$
