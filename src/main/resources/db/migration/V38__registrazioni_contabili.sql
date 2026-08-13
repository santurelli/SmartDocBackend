-- Fase 3: generazione automatica delle scritture contabili in partita doppia.
-- Testata (registrazione) + righe (movimenti Dare/Avere), tenant-scoped con RLS.

CREATE TABLE IF NOT EXISTS d_e_registrazioni_contabili
(
    k_d_e_registrazioni_contabili SERIAL PRIMARY KEY,
    data_registrazione            DATE         NOT NULL,
    descrizione                   VARCHAR(255),
    tipo_documento                VARCHAR(30)  NOT NULL, -- FATTURA | FATTURA_FORNITORE | NOTA_CREDITO | NOTA_CREDITO_FORNITORE
    id_documento                  INTEGER      NOT NULL,
    numero_documento              VARCHAR(50),
    totale_dare                   NUMERIC(14,2) NOT NULL DEFAULT 0,
    totale_avere                  NUMERIC(14,2) NOT NULL DEFAULT 0,
    fl_deleted                    SMALLINT     NOT NULL DEFAULT 0,
    user_created                  INTEGER,
    dt_created                    TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tenant_id                     BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE INDEX IF NOT EXISTS idx_d_e_registrazioni_contabili_tenant_id ON d_e_registrazioni_contabili (tenant_id);
CREATE INDEX IF NOT EXISTS idx_d_e_registrazioni_contabili_doc ON d_e_registrazioni_contabili (tipo_documento, id_documento);

ALTER TABLE d_e_registrazioni_contabili ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_registrazioni_contabili FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_registrazioni_contabili
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);

CREATE TABLE IF NOT EXISTS d_e_movimenti_contabili
(
    k_d_e_movimenti_contabili     SERIAL PRIMARY KEY,
    k_registrazione               INTEGER NOT NULL REFERENCES d_e_registrazioni_contabili (k_d_e_registrazioni_contabili) ON DELETE CASCADE,
    k_conto                       INTEGER NOT NULL REFERENCES d_e_piano_conti (k_d_e_piano_conti),
    importo_dare                  NUMERIC(14,2) NOT NULL DEFAULT 0,
    importo_avere                 NUMERIC(14,2) NOT NULL DEFAULT 0,
    descrizione                   VARCHAR(255),
    n_progr                       INTEGER,
    tenant_id                     BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE INDEX IF NOT EXISTS idx_d_e_movimenti_contabili_tenant_id ON d_e_movimenti_contabili (tenant_id);
CREATE INDEX IF NOT EXISTS idx_d_e_movimenti_contabili_registrazione ON d_e_movimenti_contabili (k_registrazione);

ALTER TABLE d_e_movimenti_contabili ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_movimenti_contabili FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_movimenti_contabili
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);
