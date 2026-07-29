ALTER TABLE d_e_contatti_clienti
    ADD COLUMN IF NOT EXISTS fl_usa_per_solleciti SMALLINT DEFAULT 0;

ALTER TABLE d_e_contatti_fornitori
    ADD COLUMN IF NOT EXISTS fl_usa_per_solleciti SMALLINT DEFAULT 0;
