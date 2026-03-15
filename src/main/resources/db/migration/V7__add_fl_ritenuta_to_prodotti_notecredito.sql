-- Aggiunta colonna per la riga articoli per la gestione della Ritenuta d'Acconto su singola riga
ALTER TABLE d_e_prodotti_notecredito ADD COLUMN fl_ritenuta INTEGER DEFAULT 0;
