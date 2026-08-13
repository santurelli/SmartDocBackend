-- Piano dei Conti (Fase 1 verso la contabilita' in partita doppia)
-- Struttura gerarchica (mastro/conto/sottoconto) tenant-scoped, con un set di conti
-- standard importabile come punto di partenza (vedi PianoDeiContiDelegate.importaStandard).

CREATE TABLE IF NOT EXISTS d_e_piano_conti
(
    k_d_e_piano_conti SERIAL PRIMARY KEY,
    codice            VARCHAR(20)  NOT NULL,
    descrizione       VARCHAR(255) NOT NULL,
    k_padre           INTEGER REFERENCES d_e_piano_conti (k_d_e_piano_conti) ON DELETE CASCADE,
    tipo              VARCHAR(20)  NOT NULL, -- ATTIVITA | PASSIVITA | PATRIMONIO_NETTO | COSTO | RICAVO | IVA
    ruolo_default     VARCHAR(30),           -- usato in fase 2 per il mapping automatico documento -> conto
    fl_predefinito    SMALLINT     NOT NULL DEFAULT 0,
    fl_bloccato       SMALLINT     NOT NULL DEFAULT 0, -- conti del set standard, non rinominabili/cancellabili
    note              VARCHAR(500),
    fl_deleted        SMALLINT     NOT NULL DEFAULT 0,
    user_created      INTEGER,
    dt_created        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_last_update  INTEGER,
    dt_last_update    TIMESTAMP WITHOUT TIME ZONE,
    tenant_id         BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE INDEX IF NOT EXISTS idx_d_e_piano_conti_tenant_id ON d_e_piano_conti (tenant_id);
CREATE INDEX IF NOT EXISTS idx_d_e_piano_conti_padre ON d_e_piano_conti (k_padre);
CREATE UNIQUE INDEX IF NOT EXISTS idx_d_e_piano_conti_codice_tenant ON d_e_piano_conti (codice, tenant_id) WHERE fl_deleted = 0;

ALTER TABLE d_e_piano_conti ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_piano_conti FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_piano_conti
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);
