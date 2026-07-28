ALTER TABLE d_e_fatturefornitore
    ADD COLUMN IF NOT EXISTS fl_ritenuta_acconto  INTEGER      DEFAULT 0,
    ADD COLUMN IF NOT EXISTS perc_ritenuta_acconto NUMERIC(5,2),
    ADD COLUMN IF NOT EXISTS tipo_ritenuta         VARCHAR(30),
    ADD COLUMN IF NOT EXISTS causale_pagamento      VARCHAR(5);
