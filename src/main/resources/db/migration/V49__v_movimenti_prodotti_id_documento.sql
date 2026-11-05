CREATE OR REPLACE VIEW v_movimenti_prodotti AS
 SELECT d_e_fatture.num_fattura || COALESCE('/'::text || d_e_fatture.particella::text, ''::text) AS numero_documento,
    d_e_fatture.data_fattura AS data_movimento,
    'U'::character varying AS tipo_movimento,
    d_e_prodotti_fatture.k_d_e_fatture AS id_documento,
    d_e_prodotti_fatture.k_d_e_prodotti AS id_prodotto,
    d_e_prodotti.descrizione AS descrizione_prodotto,
    d_e_prodotti.codice AS codice_prodotto,
    d_e_prodotti_fatture.quantita AS quantita_movimento,
    d_e_prodotti_fatture.k_d_e_unitamisura AS id_um_movimento,
    COALESCE(um.descrizione, ''::character varying) AS descrizione_um_movimento,
    get_quantita_in_um_base(d_e_prodotti.k_d_e_prodotti, d_e_prodotti_fatture.k_d_e_unitamisura, d_e_prodotti_fatture.quantita) AS quantita_um_base,
    d_e_prodotti.k_d_e_unitamisura1 AS id_um_base,
    COALESCE(um_base.descrizione, ''::character varying) AS descrizione_um_base,
        CASE
            WHEN d_e_fatture.tipo_fattura::text = 'FATTURA'::text THEN
            CASE d_e_fatture.fl_fatturaelettronica
                WHEN 0 THEN 'Fattura'::character varying
                ELSE 'Fattura elettronica'::character varying
            END
            WHEN d_e_fatture.tipo_fattura::text = 'FATTURA_ACCOMPAGNATORIA'::text THEN
            CASE d_e_fatture.fl_fatturaelettronica
                WHEN 0 THEN 'Fattura accomp.'::character varying
                ELSE 'Fattura accomp. elettronica'::character varying
            END
            WHEN d_e_fatture.tipo_fattura::text = 'FATTURA_PROFORMA'::text THEN 'Fattura pro-forma'::character varying
            WHEN d_e_fatture.tipo_fattura::text = 'NOTA_DEBITO'::text THEN
            CASE d_e_fatture.fl_fatturaelettronica
                WHEN 0 THEN 'Nota debito'::character varying
                ELSE 'Nota debito elettronica'::character varying
            END
            ELSE NULL::character varying
        END AS causale,
    d_e_clienti.denominazione AS cliente_fornitore,
    d_e_prodotti_fatture.prezzo,
    d_e_magazzini.k_d_e_magazzini AS idmagazzino,
    d_e_magazzini.descrizione AS descrizionemagazzino
   FROM d_e_prodotti_fatture
     JOIN d_e_fatture ON d_e_prodotti_fatture.k_d_e_fatture = d_e_fatture.k_d_e_fatture
     JOIN d_e_prodotti ON d_e_prodotti_fatture.k_d_e_prodotti = d_e_prodotti.k_d_e_prodotti
     LEFT JOIN d_e_unitamisura um ON d_e_prodotti_fatture.k_d_e_unitamisura = um.k_d_e_unitamisura
     LEFT JOIN d_e_unitamisura um_base ON d_e_prodotti.k_d_e_unitamisura1 = um_base.k_d_e_unitamisura
     JOIN d_e_clienti ON d_e_fatture.k_d_e_clienti = d_e_clienti.k_d_e_clienti
     LEFT JOIN d_e_magazzini ON d_e_fatture.k_d_e_magazzini = d_e_magazzini.k_d_e_magazzini
  WHERE d_e_fatture.fl_deleted = 0 AND d_e_prodotti_fatture.fl_scarica = 1
UNION
 SELECT d_e_ddt.num_ddt || COALESCE('/'::text || d_e_ddt.particella::text, ''::text) AS numero_documento,
    d_e_ddt.data_ddt AS data_movimento,
    'U'::character varying AS tipo_movimento,
    d_e_prodotti_ddt.k_d_e_ddt AS id_documento,
    d_e_prodotti_ddt.k_d_e_prodotti AS id_prodotto,
    d_e_prodotti.descrizione AS descrizione_prodotto,
    d_e_prodotti.codice AS codice_prodotto,
    d_e_prodotti_ddt.quantita AS quantita_movimento,
    d_e_prodotti_ddt.k_d_e_unitamisura AS id_um_movimento,
    COALESCE(um.descrizione, ''::character varying) AS descrizione_um_movimento,
    get_quantita_in_um_base(d_e_prodotti.k_d_e_prodotti, d_e_prodotti_ddt.k_d_e_unitamisura, d_e_prodotti_ddt.quantita) AS quantita_um_base,
    d_e_prodotti.k_d_e_unitamisura1 AS id_um_base,
    COALESCE(um_base.descrizione, ''::character varying) AS descrizione_um_base,
    'Doc. trasporto'::character varying AS causale,
    d_e_clienti.denominazione AS cliente_fornitore,
    d_e_prodotti_ddt.prezzo,
    d_e_magazzini.k_d_e_magazzini AS idmagazzino,
    d_e_magazzini.descrizione AS descrizionemagazzino
   FROM d_e_prodotti_ddt
     JOIN d_e_ddt ON d_e_prodotti_ddt.k_d_e_ddt = d_e_ddt.k_d_e_ddt
     JOIN d_e_prodotti ON d_e_prodotti_ddt.k_d_e_prodotti = d_e_prodotti.k_d_e_prodotti
     LEFT JOIN d_e_unitamisura um ON d_e_prodotti_ddt.k_d_e_unitamisura = um.k_d_e_unitamisura
     LEFT JOIN d_e_unitamisura um_base ON d_e_prodotti.k_d_e_unitamisura1 = um_base.k_d_e_unitamisura
     JOIN d_e_clienti ON d_e_ddt.k_d_e_clienti = d_e_clienti.k_d_e_clienti
     LEFT JOIN d_e_magazzini ON d_e_ddt.k_d_e_magazzini = d_e_magazzini.k_d_e_magazzini
  WHERE d_e_ddt.fl_deleted = 0 AND d_e_prodotti_ddt.fl_scarica = 1
UNION
 SELECT d_e_confordine.num_confordine || COALESCE('/'::text || d_e_confordine.particella::text, ''::text) AS numero_documento,
    d_e_confordine.data_confordine AS data_movimento,
    'C'::character varying AS tipo_movimento,
    d_e_prodotti_confordine.k_d_e_confordine AS id_documento,
    d_e_prodotti_confordine.k_d_e_prodotti AS id_prodotto,
    d_e_prodotti.descrizione AS descrizione_prodotto,
    d_e_prodotti.codice AS codice_prodotto,
    d_e_prodotti_confordine.quantita AS quantita_movimento,
    d_e_prodotti_confordine.k_d_e_unitamisura AS id_um_movimento,
    COALESCE(um.descrizione, ''::character varying) AS descrizione_um_movimento,
    get_quantita_in_um_base(d_e_prodotti.k_d_e_prodotti, d_e_prodotti_confordine.k_d_e_unitamisura, d_e_prodotti_confordine.quantita) AS quantita_um_base,
    d_e_prodotti.k_d_e_unitamisura1 AS id_um_base,
    COALESCE(um_base.descrizione, ''::character varying) AS descrizione_um_base,
    'Conf. ordine'::character varying AS causale,
    d_e_clienti.denominazione AS cliente_fornitore,
    d_e_prodotti_confordine.prezzo,
    d_e_magazzini.k_d_e_magazzini AS idmagazzino,
    d_e_magazzini.descrizione AS descrizionemagazzino
   FROM d_e_prodotti_confordine
     JOIN d_e_confordine ON d_e_prodotti_confordine.k_d_e_confordine = d_e_confordine.k_d_e_confordine
     JOIN d_e_prodotti ON d_e_prodotti_confordine.k_d_e_prodotti = d_e_prodotti.k_d_e_prodotti
     LEFT JOIN d_e_unitamisura um ON d_e_prodotti_confordine.k_d_e_unitamisura = um.k_d_e_unitamisura
     LEFT JOIN d_e_unitamisura um_base ON d_e_prodotti.k_d_e_unitamisura1 = um_base.k_d_e_unitamisura
     LEFT JOIN d_e_magazzini ON d_e_confordine.k_d_e_magazzini = d_e_magazzini.k_d_e_magazzini
     JOIN d_e_clienti ON d_e_confordine.k_d_e_clienti = d_e_clienti.k_d_e_clienti
  WHERE d_e_confordine.fl_deleted = 0 AND NOT (d_e_confordine.k_d_e_confordine IN ( SELECT d_r_doccollegati.id_docpadre
           FROM d_r_doccollegati
          WHERE d_r_doccollegati.tipo_docpadre::text = 'CONF_ORDINE'::text))
UNION
 SELECT d_e_notecredito.num_notacredito || COALESCE('/'::text || d_e_notecredito.particella::text, ''::text) AS numero_documento,
    d_e_notecredito.data_notacredito AS data_movimento,
    'I'::character varying AS tipo_movimento,
    d_e_prodotti_notecredito.k_d_e_notecredito AS id_documento,
    d_e_prodotti_notecredito.k_d_e_prodotti AS id_prodotto,
    d_e_prodotti.descrizione AS descrizione_prodotto,
    d_e_prodotti.codice AS codice_prodotto,
    d_e_prodotti_notecredito.quantita AS quantita_movimento,
    d_e_prodotti_notecredito.k_d_e_unitamisura AS id_um_movimento,
    COALESCE(um.descrizione, ''::character varying) AS descrizione_um_movimento,
    get_quantita_in_um_base(d_e_prodotti.k_d_e_prodotti, d_e_prodotti_notecredito.k_d_e_unitamisura, d_e_prodotti_notecredito.quantita) AS quantita_um_base,
    d_e_prodotti.k_d_e_unitamisura1 AS id_um_base,
    COALESCE(um_base.descrizione, ''::character varying) AS descrizione_um_base,
    'Nota credito'::character varying AS causale,
    d_e_clienti.denominazione AS cliente_fornitore,
    d_e_prodotti_notecredito.prezzo,
    d_e_magazzini.k_d_e_magazzini AS idmagazzino,
    d_e_magazzini.descrizione AS descrizionemagazzino
   FROM d_e_prodotti_notecredito
     JOIN d_e_notecredito ON d_e_prodotti_notecredito.k_d_e_notecredito = d_e_notecredito.k_d_e_notecredito
     JOIN d_e_prodotti ON d_e_prodotti_notecredito.k_d_e_prodotti = d_e_prodotti.k_d_e_prodotti
     LEFT JOIN d_e_unitamisura um ON d_e_prodotti_notecredito.k_d_e_unitamisura = um.k_d_e_unitamisura
     LEFT JOIN d_e_unitamisura um_base ON d_e_prodotti.k_d_e_unitamisura1 = um_base.k_d_e_unitamisura
     LEFT JOIN d_e_magazzini ON d_e_notecredito.k_d_e_magazzini = d_e_magazzini.k_d_e_magazzini
     JOIN d_e_clienti ON d_e_notecredito.k_d_e_clienti = d_e_clienti.k_d_e_clienti
  WHERE d_e_notecredito.fl_deleted = 0 AND d_e_prodotti_notecredito.fl_scarica = 1
UNION
 SELECT d_e_notecreditofornitore.num_notacredito || COALESCE('/'::text || d_e_notecreditofornitore.particella::text, ''::text) AS numero_documento,
    d_e_notecreditofornitore.data_notacredito AS data_movimento,
    'U'::character varying AS tipo_movimento,
    d_e_prodotti_notecreditofornitore.k_d_e_notecreditofornitore AS id_documento,
    d_e_prodotti_notecreditofornitore.k_d_e_prodotti AS id_prodotto,
    d_e_prodotti.descrizione AS descrizione_prodotto,
    d_e_prodotti.codice AS codice_prodotto,
    d_e_prodotti_notecreditofornitore.quantita AS quantita_movimento,
    d_e_prodotti_notecreditofornitore.k_d_e_unitamisura AS id_um_movimento,
    COALESCE(um.descrizione, ''::character varying) AS descrizione_um_movimento,
    get_quantita_in_um_base(d_e_prodotti.k_d_e_prodotti, d_e_prodotti_notecreditofornitore.k_d_e_unitamisura, d_e_prodotti_notecreditofornitore.quantita) AS quantita_um_base,
    d_e_prodotti.k_d_e_unitamisura1 AS id_um_base,
    COALESCE(um_base.descrizione, ''::character varying) AS descrizione_um_base,
    'Nota credito fornitore'::character varying AS causale,
    d_e_fornitori.denominazione AS cliente_fornitore,
    d_e_prodotti_notecreditofornitore.prezzo,
    d_e_magazzini.k_d_e_magazzini AS idmagazzino,
    d_e_magazzini.descrizione AS descrizionemagazzino
   FROM d_e_prodotti_notecreditofornitore
     JOIN d_e_notecreditofornitore ON d_e_prodotti_notecreditofornitore.k_d_e_notecreditofornitore = d_e_notecreditofornitore.k_d_e_notecreditofornitore
     JOIN d_e_prodotti ON d_e_prodotti_notecreditofornitore.k_d_e_prodotti = d_e_prodotti.k_d_e_prodotti
     LEFT JOIN d_e_unitamisura um ON d_e_prodotti_notecreditofornitore.k_d_e_unitamisura = um.k_d_e_unitamisura
     LEFT JOIN d_e_unitamisura um_base ON d_e_prodotti.k_d_e_unitamisura1 = um_base.k_d_e_unitamisura
     LEFT JOIN d_e_magazzini ON d_e_notecreditofornitore.k_d_e_magazzini = d_e_magazzini.k_d_e_magazzini
     JOIN d_e_fornitori ON d_e_notecreditofornitore.k_d_e_fornitori = d_e_fornitori.k_d_e_fornitori
  WHERE d_e_notecreditofornitore.fl_deleted = 0 AND d_e_prodotti_notecreditofornitore.fl_scarica = 1
UNION
 SELECT NULL::text AS numero_documento,
    d_e_movimenti_magazzino.data AS data_movimento,
    d_e_movimenti_magazzino.tipo_movimento,
    d_e_movimenti_magazzino.k_d_e_movimenti_magazzino AS id_documento,
    d_e_movimenti_magazzino.k_d_e_prodotti AS id_prodotto,
    d_e_prodotti.descrizione AS descrizione_prodotto,
    d_e_prodotti.codice AS codice_prodotto,
    d_e_movimenti_magazzino.quantita AS quantita_movimento,
    d_e_movimenti_magazzino.k_d_e_unitamisura AS id_um_movimento,
    COALESCE(um.descrizione, ''::character varying) AS descrizione_um_movimento,
    get_quantita_in_um_base(d_e_prodotti.k_d_e_prodotti, d_e_movimenti_magazzino.k_d_e_unitamisura, d_e_movimenti_magazzino.quantita) AS quantita_um_base,
    d_e_prodotti.k_d_e_unitamisura1 AS id_um_base,
    COALESCE(um_base.descrizione, ''::character varying) AS descrizione_um_base,
    COALESCE(d_e_movimenti_magazzino.desc_causale, ''::character varying) AS causale,
    ''::character varying AS cliente_fornitore,
    d_e_movimenti_magazzino.prezzo_unitario AS prezzo,
    d_e_magazzini.k_d_e_magazzini AS idmagazzino,
    d_e_magazzini.descrizione AS descrizionemagazzino
   FROM d_e_movimenti_magazzino
     JOIN d_e_prodotti ON d_e_movimenti_magazzino.k_d_e_prodotti = d_e_prodotti.k_d_e_prodotti
     LEFT JOIN d_e_unitamisura um ON d_e_movimenti_magazzino.k_d_e_unitamisura = um.k_d_e_unitamisura
     LEFT JOIN d_e_unitamisura um_base ON d_e_prodotti.k_d_e_unitamisura1 = um_base.k_d_e_unitamisura
     LEFT JOIN d_e_magazzini ON d_e_movimenti_magazzino.k_d_e_magazzini = d_e_magazzini.k_d_e_magazzini
  WHERE d_e_movimenti_magazzino.fl_deleted = 0
UNION
 SELECT d_e_ordini.num_ordine || COALESCE('/'::text || d_e_ordini.particella::text, ''::text) AS numero_documento,
    d_e_ordini.data_ordine AS data_movimento,
    'O'::character varying AS tipo_movimento,
    d_e_ordini.k_d_e_ordini AS id_documento,
    d_e_prodotti_ordini.k_d_e_prodotti AS id_prodotto,
    d_e_prodotti.descrizione AS descrizione_prodotto,
    d_e_prodotti.codice AS codice_prodotto,
    get_quantita_in_um_base(d_e_prodotti.k_d_e_prodotti, d_e_prodotti_ordini.k_d_e_unitamisura, d_e_prodotti_ordini.quantita - COALESCE(( SELECT sum(d_e_prodotti_bollecarico.quantita) AS sum
           FROM d_e_prodotti_bollecarico,
            d_e_bollecarico
          WHERE d_e_bollecarico.k_d_e_bollecarico = d_e_prodotti_bollecarico.k_d_e_bollecarico AND d_e_bollecarico.k_d_e_ordini = d_e_ordini.k_d_e_ordini AND d_e_bollecarico.fl_deleted = 0 AND d_e_prodotti_bollecarico.k_d_e_prodotti = d_e_prodotti_ordini.k_d_e_prodotti), 0::numeric)) AS quantita_movimento,
    d_e_prodotti_ordini.k_d_e_unitamisura AS id_um_movimento,
    COALESCE(um.descrizione, ''::character varying) AS descrizione_um_movimento,
    get_quantita_in_um_base(d_e_prodotti.k_d_e_prodotti, d_e_prodotti_ordini.k_d_e_unitamisura, d_e_prodotti_ordini.quantita) AS quantita_um_base,
    d_e_prodotti.k_d_e_unitamisura1 AS id_um_base,
    COALESCE(um_base.descrizione, ''::character varying) AS descrizione_um_base,
    'Ordine a fornitore'::character varying AS causale,
    d_e_fornitori.denominazione AS cliente_fornitore,
    d_e_prodotti_ordini.prezzo,
    d_e_magazzini.k_d_e_magazzini AS idmagazzino,
    d_e_magazzini.descrizione AS descrizionemagazzino
   FROM d_e_prodotti_ordini
     JOIN d_e_ordini ON d_e_prodotti_ordini.k_d_e_ordini = d_e_ordini.k_d_e_ordini
     JOIN d_e_prodotti ON d_e_prodotti_ordini.k_d_e_prodotti = d_e_prodotti.k_d_e_prodotti
     LEFT JOIN d_e_magazzini ON d_e_ordini.k_d_e_magazzini = d_e_magazzini.k_d_e_magazzini
     LEFT JOIN d_e_unitamisura um ON d_e_prodotti_ordini.k_d_e_unitamisura = um.k_d_e_unitamisura
     LEFT JOIN d_e_unitamisura um_base ON d_e_prodotti.k_d_e_unitamisura1 = um_base.k_d_e_unitamisura
     JOIN d_e_fornitori ON d_e_ordini.k_d_e_fornitori = d_e_fornitori.k_d_e_fornitori
  WHERE d_e_ordini.fl_deleted = 0 AND get_stato_ordine(d_e_ordini.k_d_e_ordini)::text <> 'Concluso'::text
UNION
 SELECT d_e_bollecarico.num_bolla || COALESCE('/'::text || d_e_bollecarico.particella::text, ''::text) AS numero_documento,
    d_e_bollecarico.data_bolla AS data_movimento,
    'I'::character varying AS tipo_movimento,
    d_e_bollecarico.k_d_e_bollecarico AS id_documento,
    d_e_prodotti_bollecarico.k_d_e_prodotti AS id_prodotto,
    d_e_prodotti.descrizione AS descrizione_prodotto,
    d_e_prodotti.codice AS codice_prodotto,
    d_e_prodotti_bollecarico.quantita AS quantita_movimento,
    d_e_prodotti_bollecarico.k_d_e_unitamisura AS id_um_movimento,
    COALESCE(um.descrizione, ''::character varying) AS descrizione_um_movimento,
    get_quantita_in_um_base(d_e_prodotti.k_d_e_prodotti, d_e_prodotti_bollecarico.k_d_e_unitamisura, d_e_prodotti_bollecarico.quantita) AS quantita_um_base,
    d_e_prodotti.k_d_e_unitamisura1 AS id_um_base,
    COALESCE(um_base.descrizione, ''::character varying) AS descrizione_um_base,
    'Bolla di carico'::character varying AS causale,
    d_e_fornitori.denominazione AS cliente_fornitore,
    d_e_prodotti_bollecarico.prezzo,
    d_e_magazzini.k_d_e_magazzini AS idmagazzino,
    d_e_magazzini.descrizione AS descrizionemagazzino
   FROM d_e_prodotti_bollecarico
     JOIN d_e_bollecarico ON d_e_prodotti_bollecarico.k_d_e_bollecarico = d_e_bollecarico.k_d_e_bollecarico
     JOIN d_e_prodotti ON d_e_prodotti_bollecarico.k_d_e_prodotti = d_e_prodotti.k_d_e_prodotti
     LEFT JOIN d_e_unitamisura um ON d_e_prodotti_bollecarico.k_d_e_unitamisura = um.k_d_e_unitamisura
     LEFT JOIN d_e_magazzini ON d_e_bollecarico.k_d_e_magazzini = d_e_magazzini.k_d_e_magazzini
     LEFT JOIN d_e_unitamisura um_base ON d_e_prodotti.k_d_e_unitamisura1 = um_base.k_d_e_unitamisura
     JOIN d_e_fornitori ON d_e_bollecarico.k_d_e_fornitori = d_e_fornitori.k_d_e_fornitori
  WHERE d_e_bollecarico.fl_deleted = 0
UNION
 SELECT d_e_scontrini.num_scontrino || ''::text AS numero_documento,
    d_e_scontrini.data_scontrino AS data_movimento,
    'U'::character varying AS tipo_movimento,
    d_e_prodotti_scontrini.k_d_e_scontrini AS id_documento,
    d_e_prodotti_scontrini.k_d_e_prodotti AS id_prodotto,
    d_e_prodotti.descrizione AS descrizione_prodotto,
    d_e_prodotti.codice AS codice_prodotto,
    d_e_prodotti_scontrini.quantita AS quantita_movimento,
    d_e_prodotti_scontrini.k_d_e_unitamisura AS id_um_movimento,
    COALESCE(um.descrizione, ''::character varying) AS descrizione_um_movimento,
    get_quantita_in_um_base(d_e_prodotti.k_d_e_prodotti, d_e_prodotti_scontrini.k_d_e_unitamisura, d_e_prodotti_scontrini.quantita) AS quantita_um_base,
    d_e_prodotti.k_d_e_unitamisura1 AS id_um_base,
    COALESCE(um_base.descrizione, ''::character varying) AS descrizione_um_base,
    'Scontrino'::character varying AS causale,
    ''::character varying AS cliente_fornitore,
    d_e_prodotti_scontrini.prezzo,
    d_e_scontrini.k_d_e_magazzini AS idmagazzino,
    ''::character varying AS descrizionemagazzino
   FROM d_e_prodotti_scontrini
     JOIN d_e_scontrini ON d_e_prodotti_scontrini.k_d_e_scontrini = d_e_scontrini.k_d_e_scontrini
     JOIN d_e_prodotti ON d_e_prodotti_scontrini.k_d_e_prodotti = d_e_prodotti.k_d_e_prodotti
     LEFT JOIN d_e_unitamisura um ON d_e_prodotti_scontrini.k_d_e_unitamisura = um.k_d_e_unitamisura
     LEFT JOIN d_e_unitamisura um_base ON d_e_prodotti.k_d_e_unitamisura1 = um_base.k_d_e_unitamisura
  WHERE d_e_prodotti_scontrini.fl_scarica = 1;

