-- Fase 2 (rifinitura): override puntuale conto ricavo/costo a livello di singolo articolo.
-- Priorita' di risoluzione (fase 3): conto articolo > conto sottocategoria > conto categoria > conto generico di ruolo.
-- Pensato per eccezioni puntuali, non come sostituto della cascata categoria/sottocategoria.

ALTER TABLE d_e_prodotti ADD COLUMN IF NOT EXISTS k_conto_ricavo INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);
ALTER TABLE d_e_prodotti ADD COLUMN IF NOT EXISTS k_conto_costo  INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);
