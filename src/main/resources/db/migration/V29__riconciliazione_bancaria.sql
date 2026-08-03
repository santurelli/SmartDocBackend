-- Riconciliazione bancaria (feature 6)
-- Import estratti conto (MT940/CSV) e matching automatico con le scadenze aperte in Prima Nota.

CREATE TABLE IF NOT EXISTS d_e_riconciliazione_import
(
    k_d_e_riconciliazione_import SERIAL PRIMARY KEY,
    nome_file                    VARCHAR(255) NOT NULL,
    formato                      VARCHAR(10)  NOT NULL, -- 'MT940' | 'CSV'
    k_d_e_risorse                INTEGER,               -- conto/banca di riferimento
    stato                        VARCHAR(15)  NOT NULL DEFAULT 'COMPLETATO', -- COMPLETATO | ERRORE
    dettaglio_errore             VARCHAR(500),
    num_movimenti                INTEGER      DEFAULT 0,
    num_abbinati_auto            INTEGER      DEFAULT 0,
    user_import                  INTEGER,
    dt_import                    TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tenant_id                    BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE INDEX IF NOT EXISTS idx_d_e_riconciliazione_import_tenant_id ON d_e_riconciliazione_import (tenant_id);

ALTER TABLE d_e_riconciliazione_import ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_riconciliazione_import FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_riconciliazione_import
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);

CREATE TABLE IF NOT EXISTS d_e_riconciliazione_movimento
(
    k_d_e_riconciliazione_movimento SERIAL PRIMARY KEY,
    k_d_e_riconciliazione_import    INTEGER NOT NULL REFERENCES d_e_riconciliazione_import (k_d_e_riconciliazione_import) ON DELETE CASCADE,
    data_valuta                     DATE,
    data_contabile                  DATE,
    importo                         NUMERIC(14,2) NOT NULL, -- segno: positivo = entrata, negativo = uscita
    causale_banca                   VARCHAR(500),
    controparte                     VARCHAR(255),
    iban_controparte                VARCHAR(34),
    stato                           VARCHAR(20) NOT NULL DEFAULT 'NON_ABBINATO',
        -- NON_ABBINATO | ABBINATO_AUTO | ABBINATO_MANUALE | IGNORATO
    tipo_scadenza_abbinata          VARCHAR(20), -- INCASSO | PAGAMENTO
    k_scadenza_abbinata             INTEGER,     -- FK logica a d_e_scadenzepagamentifatture / fatturefornitore
    score_matching                  NUMERIC(5,2),
    dt_abbinamento                  TIMESTAMP WITHOUT TIME ZONE,
    user_abbinamento                INTEGER,
    tenant_id                       BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE INDEX IF NOT EXISTS idx_d_e_riconciliazione_movimento_tenant_id ON d_e_riconciliazione_movimento (tenant_id);
CREATE INDEX IF NOT EXISTS idx_d_e_riconciliazione_movimento_import ON d_e_riconciliazione_movimento (k_d_e_riconciliazione_import);
CREATE INDEX IF NOT EXISTS idx_d_e_riconciliazione_movimento_stato ON d_e_riconciliazione_movimento (stato);

ALTER TABLE d_e_riconciliazione_movimento ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_riconciliazione_movimento FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_riconciliazione_movimento
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);

-- Mapping colonne CSV salvato per risorsa/banca, cosi' l'utente lo configura una sola volta per conto
CREATE TABLE IF NOT EXISTS d_e_riconciliazione_csv_mapping
(
    k_d_e_riconciliazione_csv_mapping SERIAL PRIMARY KEY,
    k_d_e_risorse                     INTEGER NOT NULL,
    delimitatore                      VARCHAR(1)  NOT NULL DEFAULT ';',
    fl_ha_intestazione                SMALLINT    NOT NULL DEFAULT 1,
    formato_data                      VARCHAR(20) NOT NULL DEFAULT 'dd/MM/yyyy',
    col_data                          INTEGER     NOT NULL,
    col_importo                       INTEGER     NOT NULL,
    col_causale                       INTEGER,
    col_controparte                   INTEGER,
    col_iban_controparte              INTEGER,
    fl_importo_unico_con_segno        SMALLINT    NOT NULL DEFAULT 1, -- 1 = una colonna con segno, 0 = due colonne separate
    col_importo_entrata               INTEGER, -- usata solo se fl_importo_unico_con_segno = 0
    col_importo_uscita                INTEGER, -- usata solo se fl_importo_unico_con_segno = 0
    user_created                      INTEGER,
    dt_created                        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tenant_id                         BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_d_e_riconciliazione_csv_mapping_risorsa ON d_e_riconciliazione_csv_mapping (k_d_e_risorse, tenant_id);

ALTER TABLE d_e_riconciliazione_csv_mapping ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_riconciliazione_csv_mapping FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_riconciliazione_csv_mapping
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);
