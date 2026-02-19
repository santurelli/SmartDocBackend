-- Aggiunta colonne per la gestione della Ritenuta d'Acconto
ALTER TABLE d_e_fatture ADD COLUMN fl_ritenuta_acconto INTEGER DEFAULT 0;
ALTER TABLE d_e_fatture ADD COLUMN perc_ritenuta_acconto NUMERIC(15,2) DEFAULT 0;
ALTER TABLE d_e_fatture ADD COLUMN importo_ritenuta_acconto NUMERIC(15,2) DEFAULT 0;