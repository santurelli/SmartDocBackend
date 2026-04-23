-- Aggiunta campi per personalizzazione rivalsa previdenziale
ALTER TABLE d_e_fatture ADD COLUMN perc_imponibile_rivalsa NUMERIC(12,2) DEFAULT 100.00;
ALTER TABLE d_e_fatture ADD COLUMN id_aliquota_iva_rivalsa INTEGER;

ALTER TABLE d_e_notecredito ADD COLUMN perc_imponibile_rivalsa NUMERIC(12,2) DEFAULT 100.00;
ALTER TABLE d_e_notecredito ADD COLUMN id_aliquota_iva_rivalsa INTEGER;

ALTER TABLE d_e_preventivi ADD COLUMN perc_imponibile_rivalsa NUMERIC(12,2) DEFAULT 100.00;
ALTER TABLE d_e_preventivi ADD COLUMN id_aliquota_iva_rivalsa INTEGER;

ALTER TABLE d_e_confordine ADD COLUMN perc_imponibile_rivalsa NUMERIC(12,2) DEFAULT 100.00;
ALTER TABLE d_e_confordine ADD COLUMN id_aliquota_iva_rivalsa INTEGER;

ALTER TABLE d_e_ddt ADD COLUMN perc_imponibile_rivalsa NUMERIC(12,2) DEFAULT 100.00;
ALTER TABLE d_e_ddt ADD COLUMN id_aliquota_iva_rivalsa INTEGER;

-- Configurazione di default
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('FATTURAZIONE', 'PERC_IMPONIBILE_RIVALSA', '100.00');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('FATTURAZIONE', 'ID_ALIQUOTA_IVA_RIVALSA', '0');
