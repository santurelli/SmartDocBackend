-- Inserimento del ruolo WAREHOUSE_OPERATOR per ciascun tenant (rispettando il partizionamento multi-tenant per tenant_id)
DO $$
DECLARE
    t RECORD;
    next_id INT;
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'd_e_gruppi' AND column_name = 'tenant_id'
    ) THEN
        -- Trova il prossimo id disponibile per la chiave primaria
        SELECT COALESCE(MAX(k_d_e_gruppi), 0) INTO next_id FROM d_e_gruppi;

        -- Inserisce il ruolo per ogni tenant censito in d_e_gruppi
        FOR t IN (
            SELECT DISTINCT tenant_id 
            FROM d_e_gruppi 
            WHERE tenant_id IS NOT NULL 
            ORDER BY tenant_id
        ) LOOP
            IF NOT EXISTS (
                SELECT 1 FROM d_e_gruppi 
                WHERE tenant_id = t.tenant_id AND descrizione = 'WAREHOUSE_OPERATOR'
            ) THEN
                next_id := next_id + 1;
                INSERT INTO d_e_gruppi (k_d_e_gruppi, descrizione, tenant_id)
                VALUES (next_id, 'WAREHOUSE_OPERATOR', t.tenant_id);
            END IF;
        END LOOP;
    ELSE
        -- Per database single-tenant (senza colonna tenant_id)
        IF NOT EXISTS (SELECT 1 FROM d_e_gruppi WHERE descrizione = 'WAREHOUSE_OPERATOR') THEN
            SELECT COALESCE(MAX(k_d_e_gruppi), 0) + 1 INTO next_id FROM d_e_gruppi;
            INSERT INTO d_e_gruppi (k_d_e_gruppi, descrizione)
            VALUES (next_id, 'WAREHOUSE_OPERATOR');
        END IF;
    END IF;
END $$;
