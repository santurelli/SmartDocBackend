-- Flag per fornitore: fatture soggette a ritenuta d'acconto. Quando impostato, selezionando questo
-- fornitore in una Fattura Fornitore il flag di ritenuta viene proposto automaticamente (resta comunque
-- modificabile manualmente sul singolo documento).

ALTER TABLE d_e_fornitori ADD COLUMN IF NOT EXISTS fl_ritenuta_acconto INTEGER NOT NULL DEFAULT 0;
ALTER TABLE d_e_fornitori ADD COLUMN IF NOT EXISTS tipo_ritenuta VARCHAR(30) NOT NULL DEFAULT 'PERSONE_FISICHE';
ALTER TABLE d_e_fornitori ADD COLUMN IF NOT EXISTS perc_ritenuta NUMERIC(5,2) NOT NULL DEFAULT 20.00;
