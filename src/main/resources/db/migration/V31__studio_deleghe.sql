-- Migration V31: Tabella per la gestione delle deleghe tra Studio Contabile ed Azienda Cliente
CREATE TABLE IF NOT EXISTS d_r_deleghe_studio (
    k_d_r_deleghe_studio BIGSERIAL PRIMARY KEY,
    k_studio_enti BIGINT NOT NULL,          -- Tenant ID dello Studio Contabile
    k_cliente_enti BIGINT NOT NULL,         -- Tenant ID dell'Azienda Cliente
    stato VARCHAR(20) DEFAULT 'PENDING',    -- PENDING, ACTIVE, REVOKED
    dt_richiesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dt_approvazione TIMESTAMP,
    dt_revoca TIMESTAMP,
    user_created VARCHAR(100),
    fl_deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE d_r_deleghe_studio IS 'Relazione e deleghe di accesso tra Studio Contabile e Tenant Aziendali';
COMMENT ON COLUMN d_r_deleghe_studio.k_studio_enti IS 'k_d_e_enti dell''ente Studio';
COMMENT ON COLUMN d_r_deleghe_studio.k_cliente_enti IS 'k_d_e_enti dell''ente Cliente';
COMMENT ON COLUMN d_r_deleghe_studio.stato IS 'Stato della delega: PENDING (in attesa), ACTIVE (accettata), REVOKED (revocata)';
