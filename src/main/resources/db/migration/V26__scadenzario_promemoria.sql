-- Scadenzario con promemoria automatici (feature 5)
-- Da applicare manualmente su smartdoc_shared_db (Flyway non wired in produzione)

CREATE TABLE IF NOT EXISTS d_e_scadenzario_regole
(
    k_d_e_scadenzario_regole SERIAL PRIMARY KEY,
    tipo                     VARCHAR(20)  NOT NULL, -- 'INCASSO' | 'PAGAMENTO'
    giorni_offset            INTEGER      NOT NULL, -- negativo = giorni prima della scadenza, positivo = giorni di ritardo
    oggetto                  VARCHAR(255) NOT NULL,
    corpo                    TEXT         NOT NULL, -- testo con placeholder {{cliente}}, {{importo}}, ecc.
    attivo                   SMALLINT     DEFAULT 1,
    ordine                   INTEGER      DEFAULT 0,
    user_created             INTEGER,
    dt_created               TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_last_update         INTEGER,
    dt_last_update           TIMESTAMP WITHOUT TIME ZONE,
    tenant_id                BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE INDEX IF NOT EXISTS idx_d_e_scadenzario_regole_tenant_id ON d_e_scadenzario_regole (tenant_id);

ALTER TABLE d_e_scadenzario_regole ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_scadenzario_regole FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_scadenzario_regole
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);

CREATE TABLE IF NOT EXISTS d_e_scadenzario_invii
(
    k_d_e_scadenzario_invii  SERIAL PRIMARY KEY,
    k_d_e_scadenzario_regole INTEGER     NOT NULL REFERENCES d_e_scadenzario_regole (k_d_e_scadenzario_regole) ON DELETE CASCADE,
    tipo                     VARCHAR(20) NOT NULL, -- 'INCASSO' | 'PAGAMENTO'
    k_d_e_scadenza           INTEGER     NOT NULL, -- FK logica a k_d_e_scadenzepagamentifatture o k_d_e_scadenzepagamentifatturefornitore
    destinatario             VARCHAR(255),
    esito                    VARCHAR(10), -- 'OK' | 'KO'
    dettaglio_errore         VARCHAR(500),
    dt_invio                 TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tenant_id                BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE INDEX IF NOT EXISTS idx_d_e_scadenzario_invii_tenant_id ON d_e_scadenzario_invii (tenant_id);
-- Univoco solo sugli invii riusciti: un fallimento non blocca il retry al run successivo
CREATE UNIQUE INDEX IF NOT EXISTS idx_d_e_scadenzario_invii_dedup
    ON d_e_scadenzario_invii (k_d_e_scadenzario_regole, tipo, k_d_e_scadenza) WHERE esito = 'OK';

ALTER TABLE d_e_scadenzario_invii ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_scadenzario_invii FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_scadenzario_invii
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);
