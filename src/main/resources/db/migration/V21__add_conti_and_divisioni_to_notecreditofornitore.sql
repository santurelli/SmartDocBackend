-- V21__add_conti_and_divisioni_to_notecreditofornitore.sql
-- Add missing columns to d_e_prodotti_notecreditofornitore and their foreign keys

ALTER TABLE d_e_prodotti_notecreditofornitore ADD COLUMN IF NOT EXISTS k_d_e_conti integer;
ALTER TABLE d_e_prodotti_notecreditofornitore ADD COLUMN IF NOT EXISTS k_d_e_divisioni integer;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'd_e_prodotti_notecreditofornitore_divisioni_fk'
    ) THEN
        ALTER TABLE d_e_prodotti_notecreditofornitore 
        ADD CONSTRAINT d_e_prodotti_notecreditofornitore_divisioni_fk 
        FOREIGN KEY (k_d_e_divisioni) REFERENCES d_e_divisioni(k_d_e_divisioni) 
        ON UPDATE CASCADE ON DELETE SET NULL DEFERRABLE;
    END IF;
END $$;
