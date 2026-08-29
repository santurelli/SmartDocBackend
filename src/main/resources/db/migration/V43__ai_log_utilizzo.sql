-- Log delle chiamate alle funzionalita' AI (cattura documenti, ecc.), per monitorare/contare
-- l'utilizzo per tenant. Una riga per ogni chiamata, indipendentemente dall'esito.

CREATE TABLE IF NOT EXISTS d_e_ai_log_utilizzo
(
    k_d_e_ai_log_utilizzo SERIAL PRIMARY KEY,
    tipo_operazione        VARCHAR(50)   NOT NULL, -- es. ESTRAZIONE_FATTURA_FORNITORE
    esito                  VARCHAR(20)   NOT NULL, -- OK | ERRORE
    dettaglio_errore       VARCHAR(2000),
    dt_utilizzo            TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    tenant_id              BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint
);

CREATE INDEX IF NOT EXISTS idx_d_e_ai_log_utilizzo_tenant_data ON d_e_ai_log_utilizzo (tenant_id, dt_utilizzo);

ALTER TABLE d_e_ai_log_utilizzo ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_ai_log_utilizzo FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_policy ON d_e_ai_log_utilizzo
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);
