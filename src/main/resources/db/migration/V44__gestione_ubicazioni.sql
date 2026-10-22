-- Migration: Gestione Ubicazioni e Ubicazioni-Prodotti (Pneumatici)
CREATE TABLE IF NOT EXISTS d_e_ubicazioni (
    k_d_e_ubicazioni BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint,
    cod_ubicazione VARCHAR(50) NOT NULL,
    descrizione VARCHAR(255),
    corsia VARCHAR(50),
    ripiano VARCHAR(50),
    livello VARCHAR(50),
    note TEXT,
    fl_deleted SMALLINT DEFAULT 0 NOT NULL,
    user_created VARCHAR(100),
    dt_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_last_update VARCHAR(100),
    dt_last_update TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ubicazioni_cod ON d_e_ubicazioni(cod_ubicazione);
CREATE INDEX IF NOT EXISTS idx_ubicazioni_deleted ON d_e_ubicazioni(fl_deleted);
CREATE INDEX IF NOT EXISTS idx_d_e_ubicazioni_tenant_id ON d_e_ubicazioni(tenant_id);

ALTER TABLE d_e_ubicazioni ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_ubicazioni FORCE ROW LEVEL SECURITY;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_policies WHERE tablename = 'd_e_ubicazioni' AND policyname = 'tenant_isolation_policy'
    ) THEN
        CREATE POLICY tenant_isolation_policy ON d_e_ubicazioni 
            USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS d_r_ubicazioni_prodotti (
    k_d_r_ubicazioni_prodotti BIGSERIAL PRIMARY KEY,
    k_d_e_prodotti BIGINT NOT NULL,
    k_d_e_ubicazioni BIGINT NOT NULL,
    dot VARCHAR(20),
    quantita NUMERIC(12,2) DEFAULT 0 NOT NULL,
    note TEXT,
    fl_deleted SMALLINT DEFAULT 0 NOT NULL,
    user_created VARCHAR(100),
    dt_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_last_update VARCHAR(100),
    dt_last_update TIMESTAMP,
    tenant_id BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint,
    CONSTRAINT fk_ubiprod_prod FOREIGN KEY (k_d_e_prodotti) REFERENCES d_e_prodotti(k_d_e_prodotti) ON DELETE CASCADE,
    CONSTRAINT fk_ubiprod_ubi FOREIGN KEY (k_d_e_ubicazioni) REFERENCES d_e_ubicazioni(k_d_e_ubicazioni) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ubiprod_prod ON d_r_ubicazioni_prodotti(k_d_e_prodotti);
CREATE INDEX IF NOT EXISTS idx_ubiprod_ubi ON d_r_ubicazioni_prodotti(k_d_e_ubicazioni);
CREATE INDEX IF NOT EXISTS idx_d_r_ubicazioni_prodotti_tenant_id ON d_r_ubicazioni_prodotti(tenant_id);

ALTER TABLE d_r_ubicazioni_prodotti ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_r_ubicazioni_prodotti FORCE ROW LEVEL SECURITY;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_policies WHERE tablename = 'd_r_ubicazioni_prodotti' AND policyname = 'tenant_isolation_policy'
    ) THEN
        CREATE POLICY tenant_isolation_policy ON d_r_ubicazioni_prodotti 
            USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);
    END IF;
END $$;

-- Inserimento parametri di configurazione se non presenti
INSERT INTO d_e_configurazione (dominio, chiave, valore)
VALUES ('GLOBAL', 'GOMMISTA', '1')
ON CONFLICT (dominio, chiave) DO NOTHING;

INSERT INTO d_e_configurazione (dominio, chiave, valore)
VALUES ('GLOBAL', 'UBICAZIONI', '1')
ON CONFLICT (dominio, chiave) DO NOTHING;

INSERT INTO d_e_configurazione (dominio, chiave, valore)
VALUES ('GLOBAL', 'STAMPANTE_PRELIEVO_IP', '192.168.1.200')
ON CONFLICT (dominio, chiave) DO NOTHING;

INSERT INTO d_e_configurazione (dominio, chiave, valore)
VALUES ('GLOBAL', 'PRINTER_BRIDGE_URL', 'http://localhost:3001')
ON CONFLICT (dominio, chiave) DO NOTHING;
