-- Fase 2 (parziale): aggancio conti di default su Categoria Articolo, Cliente e Fornitore.
-- Nullable: se non impostato, la futura generazione automatica delle scritture contabili
-- usera' il conto generico di ruolo (ruolo_default in d_e_piano_conti).

ALTER TABLE d_e_categorie ADD COLUMN IF NOT EXISTS k_conto_ricavo INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);
ALTER TABLE d_e_categorie ADD COLUMN IF NOT EXISTS k_conto_costo  INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);

ALTER TABLE d_e_clienti ADD COLUMN IF NOT EXISTS k_conto_default INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);

ALTER TABLE d_e_fornitori ADD COLUMN IF NOT EXISTS k_conto_default INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);
