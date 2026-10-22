-- Migration: Creazione gestione Marche e collegamento ai Prodotti
CREATE TABLE IF NOT EXISTS d_e_marche (
    k_d_e_marche SERIAL PRIMARY KEY,
    tenant_id BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint,
    codice VARCHAR(50),
    descrizione VARCHAR(255) NOT NULL,
    note TEXT,
    fl_deleted SMALLINT DEFAULT 0,
    user_created INTEGER,
    dt_created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_last_update INTEGER,
    dt_last_update TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_d_e_marche_descrizione ON d_e_marche(descrizione);
CREATE INDEX IF NOT EXISTS idx_d_e_marche_tenant_id ON d_e_marche(tenant_id);

ALTER TABLE d_e_marche ENABLE ROW LEVEL SECURITY;
ALTER TABLE d_e_marche FORCE ROW LEVEL SECURITY;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_policies WHERE tablename = 'd_e_marche' AND policyname = 'tenant_isolation_policy'
    ) THEN
        CREATE POLICY tenant_isolation_policy ON d_e_marche 
            USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);
    END IF;
END $$;

-- Aggiunta colonna marca a d_e_prodotti
ALTER TABLE d_e_prodotti ADD COLUMN IF NOT EXISTS k_d_e_marche INTEGER;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'd_e_prodotti_marche_fk'
    ) THEN
        ALTER TABLE d_e_prodotti 
        ADD CONSTRAINT d_e_prodotti_marche_fk 
        FOREIGN KEY (k_d_e_marche) REFERENCES d_e_marche(k_d_e_marche) 
        ON UPDATE CASCADE ON DELETE SET NULL DEFERRABLE;
    END IF;
END $$;

-- Abilitazione parametro configurazione MARCHE
INSERT INTO d_e_configurazione (dominio, chiave, valore)
VALUES ('GLOBAL', 'MARCHE', '1')
ON CONFLICT (dominio, chiave) DO UPDATE SET valore = '1';

-- Marche predefinite (opzionali / di base)
INSERT INTO d_e_marche (codice, descrizione) VALUES 
('MIC', 'Michelin'),
('PIR', 'Pirelli'),
('CON', 'Continental'),
('BRI', 'Bridgestone'),
('GOO', 'Goodyear'),
('DUN', 'Dunlop'),
('HAN', 'Hankook'),
('YOK', 'Yokohama'),
('NOK', 'Nokian'),
('KUM', 'Kumho'),
('TOY', 'Toyo'),
('NEX', 'Nexen');
