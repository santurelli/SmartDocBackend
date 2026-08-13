-- Override manuale del conto contabile su una singola riga documento (Fase 3+): quando valorizzato,
-- il motore di generazione automatica delle scritture lo usa al posto della cascata di risoluzione
-- (articolo -> sottocategoria -> categoria -> ruolo generico). Solo sui documenti che generano
-- scritture contabili: Fatture, Fatture Fornitore, Note di Credito, Note di Credito Fornitore.

ALTER TABLE d_e_prodotti_fatture ADD COLUMN IF NOT EXISTS k_conto_override INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);
ALTER TABLE d_e_prodotti_fatturefornitore ADD COLUMN IF NOT EXISTS k_conto_override INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);
ALTER TABLE d_e_prodotti_notecredito ADD COLUMN IF NOT EXISTS k_conto_override INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);
ALTER TABLE d_e_prodotti_notecreditofornitore ADD COLUMN IF NOT EXISTS k_conto_override INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti);
