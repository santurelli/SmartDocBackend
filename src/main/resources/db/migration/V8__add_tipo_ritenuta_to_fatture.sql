ALTER TABLE d_e_fatture ADD COLUMN tipo_ritenuta VARCHAR(10);

INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('FATTURAZIONE', 'EMETTI_RITENUTA', '0');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('FATTURAZIONE', 'TIPO_RITENUTA', 'RT01');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('FATTURAZIONE', 'PERC_RITENUTA', '20.00');
