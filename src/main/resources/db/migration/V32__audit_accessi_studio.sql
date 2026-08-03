-- Migration V32: Tabella Audit Log per accessi ed impersonificazioni degli Studi Contabili (GDPR)
CREATE TABLE IF NOT EXISTS d_t_audit_accessi_studio (
    k_d_t_audit_accessi_studio BIGSERIAL PRIMARY KEY,
    k_studio_enti BIGINT NOT NULL,
    k_cliente_enti BIGINT NOT NULL,
    username_operatore VARCHAR(100) NOT NULL,
    azione VARCHAR(50) NOT NULL, -- LOGIN_IMPERSONATE, EXIT_IMPERSONATE, BATCH_DOWNLOAD
    ip_address VARCHAR(45),
    dt_accesso TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE d_t_audit_accessi_studio IS 'Registro delle operazioni e delle sessioni di accesso effettuate dallo Studio sui tenant clienti (GDPR Compliance)';
COMMENT ON COLUMN d_t_audit_accessi_studio.k_studio_enti IS 'k_d_e_enti dell''ente Studio';
COMMENT ON COLUMN d_t_audit_accessi_studio.k_cliente_enti IS 'k_d_e_enti dell''ente Cliente consultata';
COMMENT ON COLUMN d_t_audit_accessi_studio.username_operatore IS 'Username dell''operatore che ha effettuato l''accesso';
COMMENT ON COLUMN d_t_audit_accessi_studio.azione IS 'Tipo di operazione (es. LOGIN_IMPERSONATE)';
