-- Aggiornamento calcolo totale fattura con aliquota IVA rivalsa specifica
CREATE OR REPLACE FUNCTION public.get_totale_fattura(bigint)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$DECLARE
	vIdFattura ALIAS FOR $1;
	lTotale numeric (10,2);
	lTotaleSpese numeric (10,2);
	lRivalsa numeric (10,2);
	lIvaRivalsa numeric (10,2);
	lIdAliquotaIvaRivalsa integer;
	lPercIvaRivalsa numeric(12,2);
BEGIN
	select get_totalespese_fattura(vIdFattura) into lTotaleSpese;

	select sum(get_prezzoscontato_prodottofattura(k_d_e_prodotti_fatture))
	  into lTotale
	from d_e_prodotti_fatture
	where k_d_e_fatture = vIdFattura;

	if lTotale is null then
		lTotale := 0;
	end if;

	-- Recupero Rivalsa INPS e eventuale aliquota IVA specifica
	select coalesce(importo_rivalsa_inps, 0), id_aliquota_iva_rivalsa 
	  into lRivalsa, lIdAliquotaIvaRivalsa 
	from d_e_fatture 
	where k_d_e_fatture = vIdFattura;

	-- Calcolo IVA su rivalsa
	IF lIdAliquotaIvaRivalsa IS NOT NULL AND lIdAliquotaIvaRivalsa > 0 THEN
		-- Usa aliquota specifica
		SELECT imposta INTO lPercIvaRivalsa FROM d_e_aliquoteiva WHERE k_d_e_aliquoteiva = lIdAliquotaIvaRivalsa;
	ELSE
		-- Fallback a aliquota della prima riga o 22%
		select coalesce(ai.imposta, 22) into lPercIvaRivalsa
		from d_e_fatture f
		left join d_e_prodotti_fatture pf on pf.k_d_e_fatture = f.k_d_e_fatture
		left join d_e_aliquoteiva ai on ai.k_d_e_aliquoteiva = pf.k_d_e_aliquoteiva
		where f.k_d_e_fatture = vIdFattura
		limit 1;
	END IF;

	lIvaRivalsa := (lRivalsa * coalesce(lPercIvaRivalsa, 22) / 100);

	if lIvaRivalsa is null then
		lIvaRivalsa := 0;
	end if;

	lTotale := lTotale + lTotaleSpese + lRivalsa + lIvaRivalsa;

	RETURN ROUND(lTotale, 2);

END;$function$;

-- Similmente per Note di Credito
CREATE OR REPLACE FUNCTION public.get_totale_notacredito(bigint)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$DECLARE
	vIdNotaCredito ALIAS FOR $1;
	lTotale numeric (10,2);
	lRivalsa numeric (10,2);
	lIvaRivalsa numeric (10,2);
	lIdAliquotaIvaRivalsa integer;
	lPercIvaRivalsa numeric(12,2);
BEGIN
	select sum(get_prezzoscontato_prodottonotacredito(k_d_e_prodotti_notecredito))
	  into lTotale
	from d_e_prodotti_notecredito
	where k_d_e_notecredito = vIdNotaCredito;

	if lTotale is null then
		lTotale := 0;
	end if;

	-- Recupero Rivalsa INPS e eventuale aliquota IVA specifica
	select coalesce(importo_rivalsa_inps, 0), id_aliquota_iva_rivalsa 
	  into lRivalsa, lIdAliquotaIvaRivalsa 
	from d_e_notecredito 
	where k_d_e_notecredito = vIdNotaCredito;

	-- Calcolo IVA su rivalsa
	IF lIdAliquotaIvaRivalsa IS NOT NULL AND lIdAliquotaIvaRivalsa > 0 THEN
		-- Usa aliquota specifica
		SELECT imposta INTO lPercIvaRivalsa FROM d_e_aliquoteiva WHERE k_d_e_aliquoteiva = lIdAliquotaIvaRivalsa;
	ELSE
		-- Fallback a aliquota della prima riga o 22%
		select coalesce(ai.imposta, 22) into lPercIvaRivalsa
		from d_e_notecredito f
		left join d_e_prodotti_notecredito pf on pf.k_d_e_notecredito = f.k_d_e_notecredito
		left join d_e_aliquoteiva ai on ai.k_d_e_aliquoteiva = pf.k_d_e_aliquoteiva
		where f.k_d_e_notecredito = vIdNotaCredito
		limit 1;
	END IF;

	lIvaRivalsa := (lRivalsa * coalesce(lPercIvaRivalsa, 22) / 100);

	if lIvaRivalsa is null then
		lIvaRivalsa := 0;
	end if;

	lTotale := lTotale + lRivalsa + lIvaRivalsa;

	RETURN ROUND(lTotale, 2);

END;$function$;

-- Similmente per Preventivi
CREATE OR REPLACE FUNCTION public.get_totale_preventivo(bigint)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$DECLARE
	vIdPreventivo ALIAS FOR $1;
	lTotale numeric (10,2);
	lRivalsa numeric (10,2);
	lIvaRivalsa numeric (10,2);
	lIdAliquotaIvaRivalsa integer;
	lPercIvaRivalsa numeric(12,2);
BEGIN
	select sum(get_prezzoscontato_prodottopreventivo(k_d_e_prodotti_preventivi))
	  into lTotale
	from d_e_prodotti_preventivi
	where k_d_e_preventivi = vIdPreventivo;

	if lTotale is null then
		lTotale := 0;
	end if;

	-- Recupero Rivalsa INPS e eventuale aliquota IVA specifica
	select coalesce(importo_rivalsa_inps, 0), id_aliquota_iva_rivalsa 
	  into lRivalsa, lIdAliquotaIvaRivalsa 
	from d_e_preventivi 
	where k_d_e_preventivi = vIdPreventivo;

	-- Calcolo IVA su rivalsa
	IF lIdAliquotaIvaRivalsa IS NOT NULL AND lIdAliquotaIvaRivalsa > 0 THEN
		-- Usa aliquota specifica
		SELECT imposta INTO lPercIvaRivalsa FROM d_e_aliquoteiva WHERE k_d_e_aliquoteiva = lIdAliquotaIvaRivalsa;
	ELSE
		-- Fallback a aliquota della prima riga o 22%
		select coalesce(ai.imposta, 22) into lPercIvaRivalsa
		from d_e_preventivi f
		left join d_e_prodotti_preventivi pf on pf.k_d_e_preventivi = f.k_d_e_preventivi
		left join d_e_aliquoteiva ai on ai.k_d_e_aliquoteiva = pf.k_d_e_aliquoteiva
		where f.k_d_e_preventivi = vIdPreventivo
		limit 1;
	END IF;

	lIvaRivalsa := (lRivalsa * coalesce(lPercIvaRivalsa, 22) / 100);

	if lIvaRivalsa is null then
		lIvaRivalsa := 0;
	end if;

	lTotale := lTotale + lRivalsa + lIvaRivalsa;

	RETURN ROUND(lTotale, 2);

END;$function$;
