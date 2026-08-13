-- Fase 2 (rifinitura): conto ricavo/costo anche a livello di sottocategoria.
-- Priorita' di risoluzione (fase 3): conto sottocategoria > conto categoria > conto generico di ruolo.

ALTER TABLE d_e_sottocategorie ADD COLUMN IF NOT EXISTS k_conto_ricavo INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);
ALTER TABLE d_e_sottocategorie ADD COLUMN IF NOT EXISTS k_conto_costo  INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);
