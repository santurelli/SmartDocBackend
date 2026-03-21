-- V11__modern_listini.sql
-- New columns for hierarchical and derivation pricing

ALTER TABLE d_e_listini ADD COLUMN id_parent BIGINT REFERENCES d_e_listini(k_d_e_listini);
ALTER TABLE d_e_listini ADD COLUMN derivation_source VARCHAR(50) DEFAULT 'LISTINO'; -- LISTINO, ULTIMO_ACQUISTO, MEDIO_ACQUISTO
ALTER TABLE d_e_listini ADD COLUMN derivation_type VARCHAR(50) DEFAULT 'NONE';    -- NONE, PERCENTAGE, FIXED_MARKUP
ALTER TABLE d_e_listini ADD COLUMN derivation_value DECIMAL(12,4) DEFAULT 0;
ALTER TABLE d_e_listini ADD COLUMN rounding_rule DECIMAL(12,4) DEFAULT 0;

-- Optional: Comments for documentation
COMMENT ON COLUMN d_e_listini.derivation_source IS 'Source for price calculation: LISTINO, ULTIMO_ACQUISTO, or MEDIO_ACQUISTO';
COMMENT ON COLUMN d_e_listini.derivation_type IS 'Rule type: NONE (manual), PERCENTAGE (+X%), FIXED_MARKUP (+X Euro)';
COMMENT ON COLUMN d_e_listini.rounding_rule IS 'Rounding increment (e.g. 0.05)';
