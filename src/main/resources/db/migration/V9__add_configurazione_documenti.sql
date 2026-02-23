-- V9__add_configurazione_documenti.sql
-- Inserimento delle configurazioni per l'abilitazione dei documenti (Ciclo Attivo / Passivo)

-- CICLO ATTIVO
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('DOCUMENTI', 'ABILITA_PREVENTIVI', '1');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('DOCUMENTI', 'ABILITA_CONF_ORDINE', '1');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('DOCUMENTI', 'ABILITA_DDT', '1');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('DOCUMENTI', 'ABILITA_FATTURE_PROFORMA', '1');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('DOCUMENTI', 'ABILITA_FATTURE_ACCOMPAGNATORIE', '1');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('DOCUMENTI', 'ABILITA_FATTURE', '1');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('DOCUMENTI', 'ABILITA_NOTE_DEBITO', '1');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('DOCUMENTI', 'ABILITA_NOTE_CREDITO', '1');

-- CICLO PASSIVO
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('DOCUMENTI', 'ABILITA_FATTURE_FORNITORE', '1');
INSERT INTO d_e_configurazione (dominio, chiave, valore) VALUES ('DOCUMENTI', 'ABILITA_NOTE_CREDITO_FORNITORE', '1');
