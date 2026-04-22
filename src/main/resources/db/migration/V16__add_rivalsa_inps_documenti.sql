-- 1. FATTURE E NOTE DEBITO (condividono la tabella d_e_fatture)
ALTER TABLE d_e_fatture ADD COLUMN fl_rivalsa_inps SMALLINT DEFAULT 0;
ALTER TABLE d_e_fatture ADD COLUMN perc_rivalsa_inps NUMERIC(12,2) DEFAULT 0;
ALTER TABLE d_e_fatture ADD COLUMN importo_rivalsa_inps NUMERIC(12,2) DEFAULT 0;
ALTER TABLE d_e_fatture ADD COLUMN tipo_cassa_inps VARCHAR(10);
-- 2. NOTE CREDITO
ALTER TABLE d_e_notecredito ADD COLUMN fl_rivalsa_inps SMALLINT DEFAULT 0;
ALTER TABLE d_e_notecredito ADD COLUMN perc_rivalsa_inps NUMERIC(12,2) DEFAULT 0;
ALTER TABLE d_e_notecredito ADD COLUMN importo_rivalsa_inps NUMERIC(12,2) DEFAULT 0;
ALTER TABLE d_e_notecredito ADD COLUMN tipo_cassa_inps VARCHAR(10);
-- 3. PREVENTIVI
ALTER TABLE d_e_preventivi ADD COLUMN fl_rivalsa_inps SMALLINT DEFAULT 0;
ALTER TABLE d_e_preventivi ADD COLUMN perc_rivalsa_inps NUMERIC(12,2) DEFAULT 0;
ALTER TABLE d_e_preventivi ADD COLUMN importo_rivalsa_inps NUMERIC(12,2) DEFAULT 0;
ALTER TABLE d_e_preventivi ADD COLUMN tipo_cassa_inps VARCHAR(10);
-- 4. CONFERME ORDINE
ALTER TABLE d_e_confordine ADD COLUMN fl_rivalsa_inps SMALLINT DEFAULT 0;
ALTER TABLE d_e_confordine ADD COLUMN perc_rivalsa_inps NUMERIC(12,2) DEFAULT 0;
ALTER TABLE d_e_confordine ADD COLUMN importo_rivalsa_inps NUMERIC(12,2) DEFAULT 0;
ALTER TABLE d_e_confordine ADD COLUMN tipo_cassa_inps VARCHAR(10);
-- 5. DDT
ALTER TABLE d_e_ddt ADD COLUMN fl_rivalsa_inps SMALLINT DEFAULT 0;
ALTER TABLE d_e_ddt ADD COLUMN perc_rivalsa_inps NUMERIC(12,2) DEFAULT 0;
ALTER TABLE d_e_ddt ADD COLUMN importo_rivalsa_inps NUMERIC(12,2) DEFAULT 0;
ALTER TABLE d_e_ddt ADD COLUMN tipo_cassa_inps VARCHAR(10);
-- 6. CONFIGURAZIONI DI DEFAULT
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('FATTURAZIONE', 'EMETTI_RIVALSA_INPS', '0');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('FATTURAZIONE', 'TIPO_CASSA_RIVALSA_INPS', 'TC22');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('FATTURAZIONE', 'PERC_RIVALSA_INPS', '4.00');