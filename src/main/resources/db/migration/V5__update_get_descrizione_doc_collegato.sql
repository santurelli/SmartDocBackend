CREATE OR REPLACE FUNCTION public.get_descrizione_doccollegato(
	integer,
	character varying)
    RETURNS character varying
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE
	vIdDocPadre ALIAS FOR $1;
	vTipoDocPadre ALIAS FOR $2;
	lCur refcursor;
	lIdDocFiglio d_r_doccollegati.id_docfiglio%TYPE;
	lTipoDocFiglio d_r_doccollegati.tipo_docfiglio%TYPE;
	lNumDoc integer;
	lParticella character varying;
	lData character varying;
	lDescrizione character varying;
BEGIN
	lDescrizione := '';
	OPEN lcur FOR EXECUTE 'SELECT id_docfiglio, tipo_docfiglio FROM d_r_doccollegati WHERE id_docpadre = ' || vIdDocPadre || ' AND tipo_docpadre = ''' || vTipoDocPadre || '''';
	FETCH lcur INTO lIdDocFiglio, lTipoDocFiglio;
	WHILE FOUND LOOP
		IF lTipoDocFiglio = 'FATTURA' THEN
			SELECT num_fattura,
			       particella,
			       TO_CHAR(data_fattura, 'DD/MM/YYYY')
			  INTO lNumDoc, lParticella, lData
			  FROM d_e_fatture
			 WHERE k_d_e_fatture = lIdDocFiglio
			   AND d_e_fatture.fl_deleted = 0;
			lDescrizione := 'Fattura num. '::text || lNumDoc || COALESCE('/'::text || lParticella::text, ''::text) || ' del ' || lData || '---' || lIdDocFiglio::text || '---' || lTipoDocFiglio || '&&&';
		ELSIF lTipoDocFiglio = 'DDT' THEN
			SELECT num_ddt,
			       particella,
			       TO_CHAR(data_ddt, 'DD/MM/YYYY')
			  INTO lNumDoc, lParticella, lData
			  FROM d_e_ddt
			 WHERE k_d_e_ddt = lIdDocFiglio
			   AND d_e_ddt.fl_deleted = 0;
			lDescrizione := 'Ddt num. '::text || lNumDoc || COALESCE('/'::text || lParticella::text, ''::text) || ' del ' || lData || '---' || lIdDocFiglio::text || '---' || lTipoDocFiglio || '&&&';
		ELSIF lTipoDocFiglio = 'BOLLA_CARICO' THEN
			SELECT num_bolla,
			       particella,
			       TO_CHAR(data_bolla, 'DD/MM/YYYY')
			  INTO lNumDoc, lParticella, lData
			  FROM d_e_ddt
			 WHERE k_d_e_ddt = lIdDocFiglio
			   AND d_e_ddt.fl_deleted = 0;
			lDescrizione := 'Bolla carico '::text || lNumDoc || COALESCE('/'::text || lParticella::text, ''::text) || ' del ' || lData || '---' || lIdDocFiglio::text || '---' || lTipoDocFiglio || '&&&';
		ELSIF lTipoDocFiglio = 'CONF_ORDINE' THEN
			SELECT num_confordine,
			       particella,
			       TO_CHAR(data_confordine, 'DD/MM/YYYY')
			  INTO lNumDoc, lParticella, lData
			  FROM d_e_confordine
			 WHERE k_d_e_confordine = lIdDocFiglio
			   AND d_e_confordine.fl_deleted = 0;
			lDescrizione := 'Conferma ordine '::text || lNumDoc || COALESCE('/'::text || lParticella::text, ''::text) || ' del ' || lData || '---' || lIdDocFiglio::text || '---' || lTipoDocFiglio || '&&&';
		END IF;
		FETCH lcur INTO lIdDocFiglio, lTipoDocFiglio;
	END LOOP;
	CLOSE lcur;
	RETURN lDescrizione;
END;
$BODY$;

ALTER FUNCTION public.get_descrizione_doccollegato(integer, character varying)
    OWNER TO smartdoc;