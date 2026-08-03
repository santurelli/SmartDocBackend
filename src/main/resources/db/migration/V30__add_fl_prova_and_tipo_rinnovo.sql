-- Migration V30: Aggiunta colonna fl_prova e tipo_rinnovo alla tabella d_e_enti (Service DB)
ALTER TABLE d_e_enti ADD COLUMN IF NOT EXISTS fl_prova SMALLINT DEFAULT 1;
COMMENT ON COLUMN d_e_enti.fl_prova IS '1 = In Prova Gratuita (90gg), 0 = Abbonamento Pagato (365gg / 35gg)';

ALTER TABLE d_e_enti ADD COLUMN IF NOT EXISTS tipo_rinnovo VARCHAR(10) DEFAULT 'ANNUAL';
COMMENT ON COLUMN d_e_enti.tipo_rinnovo IS 'MONTHLY = Abbonamento Mensile (35gg tolleranza), ANNUAL = Abbonamento Annuale (365gg)';

-- Imposta tipo_rinnovo = 'ANNUAL' per i record esistenti ove nullo
UPDATE d_e_enti SET tipo_rinnovo = 'ANNUAL' WHERE tipo_rinnovo IS NULL;
