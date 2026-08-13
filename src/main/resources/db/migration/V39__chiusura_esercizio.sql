-- Chiusura/apertura esercizio contabile: stato per anno + saldi di apertura riportati (tenant-scoped, RLS).

-- Retrofit: i tenant che avevano gia' importato il piano dei conti standard prima di questa migration
-- hanno il conto "Utile (perdita) d'esercizio" senza ruolo_default (serve alla chiusura per trovarlo).
UPDATE d_e_piano_conti SET ruolo_default = 'UTILE_ESERCIZIO' WHERE codice = '50.04' AND ruolo_default IS NULL;

CREATE TABLE IF NOT EXISTS d_e_esercizi
(
    k_d_e_esercizi   SERIAL PRIMARY KEY,
    anno             INTEGER      NOT NULL,
    stato            VARCHAR(10)  NOT NULL DEFAULT 'APERTO', -- APERTO | CHIUSO
    dt_chiusura      TIMESTAMP WITHOUT TIME ZONE,
    user_chiusura    INTEGER,
    tenant_id        BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_d_e_esercizi_anno_tenant ON d_e_esercizi (anno, tenant_id);

ALTER TABLE d_e_esercizi ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_esercizi FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_esercizi
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);

-- Saldo di apertura di un conto per un dato anno: per i conti patrimoniali (ATTIVITA/PASSIVITA/PATRIMONIO_NETTO/IVA)
-- e' il saldo di chiusura dell'anno precedente riportato; per i conti economici (COSTO/RICAVO) non viene mai
-- valorizzato (partono sempre da zero, si azzerano con la scrittura di chiusura, non si riportano).
CREATE TABLE IF NOT EXISTS d_e_saldi_apertura
(
    k_d_e_saldi_apertura SERIAL PRIMARY KEY,
    k_conto              INTEGER NOT NULL REFERENCES d_e_piano_conti (k_d_e_piano_conti),
    anno                 INTEGER NOT NULL,
    saldo                NUMERIC(14,2) NOT NULL DEFAULT 0, -- convenzione Dare positivo, Avere negativo
    tenant_id            BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_d_e_saldi_apertura_conto_anno_tenant ON d_e_saldi_apertura (k_conto, anno, tenant_id);

ALTER TABLE d_e_saldi_apertura ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_saldi_apertura FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_saldi_apertura
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);
