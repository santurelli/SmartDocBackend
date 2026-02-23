-- Aggiunta colonna per la riga articoli per la gestione della Ritenuta d'Acconto su singola riga
ALTER TABLE d_e_prodotti_fatture ADD COLUMN fl_ritenuta INTEGER DEFAULT 0;
