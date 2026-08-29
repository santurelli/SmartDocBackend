-- Integrazione e-commerce (WooCommerce): configurazione per tenant + log ordini importati (idempotenza).

CREATE TABLE IF NOT EXISTS d_e_ecommerce_config
(
    k_d_e_ecommerce_config SERIAL PRIMARY KEY,
    piattaforma            VARCHAR(20)   NOT NULL DEFAULT 'WOOCOMMERCE',
    store_url              VARCHAR(500),
    consumer_key           VARCHAR(200),
    consumer_secret        VARCHAR(200),
    stato_ordine_woo       VARCHAR(30)   NOT NULL DEFAULT 'processing',
    fl_abilitato           INTEGER       NOT NULL DEFAULT 0,
    fl_crea_ddt            INTEGER       NOT NULL DEFAULT 1,
    fl_crea_fattura        INTEGER       NOT NULL DEFAULT 1,
    intervallo_minuti      INTEGER       NOT NULL DEFAULT 15,
    dt_ultimo_sync         TIMESTAMP WITHOUT TIME ZONE,
    ultimo_esito           VARCHAR(2000),
    tenant_id              BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_d_e_ecommerce_config_tenant ON d_e_ecommerce_config (tenant_id);

ALTER TABLE d_e_ecommerce_config ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_ecommerce_config FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_ecommerce_config
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);

CREATE TABLE IF NOT EXISTS d_e_ecommerce_ordini_importati
(
    k_d_e_ecommerce_ordini SERIAL PRIMARY KEY,
    id_ordine_esterno      VARCHAR(50)   NOT NULL,
    numero_ordine_esterno  VARCHAR(50),
    k_d_e_ddt              INTEGER,
    k_d_e_fatture          INTEGER,
    dt_importazione        TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    esito                  VARCHAR(20)   NOT NULL DEFAULT 'OK', -- OK | ERRORE
    dettaglio_esito        VARCHAR(2000),
    tenant_id              BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_d_e_ecommerce_ordini_ext_tenant ON d_e_ecommerce_ordini_importati (id_ordine_esterno, tenant_id);
CREATE INDEX IF NOT EXISTS idx_d_e_ecommerce_ordini_dt ON d_e_ecommerce_ordini_importati (dt_importazione);

ALTER TABLE d_e_ecommerce_ordini_importati ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_ecommerce_ordini_importati FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_ecommerce_ordini_importati
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);
