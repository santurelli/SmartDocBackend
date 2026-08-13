-- Settore merceologico azienda: usato per proporre il template di piano dei conti piu' adatto
-- (COMMERCIO | SERVIZI | PRODUZIONE | EDILIZIA | GENERICO).

ALTER TABLE d_e_datiazienda ADD COLUMN IF NOT EXISTS settore_merceologico VARCHAR(30);
