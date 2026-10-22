-- Migration V46: Performance Indexes for Stock/Giacenza Calculation and Article Search
-- Eliminates sequential table scans during get_totale_disponibile()

-- Product detail & stock movement tables: index on k_d_e_prodotti and document FKs
CREATE INDEX IF NOT EXISTS idx_prod_bollecarico_k_prod ON d_e_prodotti_bollecarico(k_d_e_prodotti);
CREATE INDEX IF NOT EXISTS idx_prod_bollecarico_k_doc ON d_e_prodotti_bollecarico(k_d_e_bollecarico);

CREATE INDEX IF NOT EXISTS idx_mov_magazzino_k_prod ON d_e_movimenti_magazzino(k_d_e_prodotti, tipo_movimento);
CREATE INDEX IF NOT EXISTS idx_mov_magazzino_tipo ON d_e_movimenti_magazzino(tipo_movimento, fl_deleted);

CREATE INDEX IF NOT EXISTS idx_prod_notecredito_k_prod ON d_e_prodotti_notecredito(k_d_e_prodotti);
CREATE INDEX IF NOT EXISTS idx_prod_notecredito_k_doc ON d_e_prodotti_notecredito(k_d_e_notecredito);

CREATE INDEX IF NOT EXISTS idx_prod_fatturefornitore_k_prod ON d_e_prodotti_fatturefornitore(k_d_e_prodotti);
CREATE INDEX IF NOT EXISTS idx_prod_fatturefornitore_k_doc ON d_e_prodotti_fatturefornitore(k_d_e_fatturefornitore);

CREATE INDEX IF NOT EXISTS idx_prod_fatture_k_prod ON d_e_prodotti_fatture(k_d_e_prodotti);
CREATE INDEX IF NOT EXISTS idx_prod_fatture_k_doc ON d_e_prodotti_fatture(k_d_e_fatture);

CREATE INDEX IF NOT EXISTS idx_prod_ddt_k_prod ON d_e_prodotti_ddt(k_d_e_prodotti);
CREATE INDEX IF NOT EXISTS idx_prod_ddt_k_doc ON d_e_prodotti_ddt(k_d_e_ddt);

CREATE INDEX IF NOT EXISTS idx_prod_notecreditofornitore_k_prod ON d_e_prodotti_notecreditofornitore(k_d_e_prodotti);
CREATE INDEX IF NOT EXISTS idx_prod_notecreditofornitore_k_doc ON d_e_prodotti_notecreditofornitore(k_d_e_notecreditofornitore);

CREATE INDEX IF NOT EXISTS idx_prod_scontrini_k_prod ON d_e_prodotti_scontrini(k_d_e_prodotti);
CREATE INDEX IF NOT EXISTS idx_prod_scontrini_k_doc ON d_e_prodotti_scontrini(k_d_e_scontrini);

CREATE INDEX IF NOT EXISTS idx_prod_confordine_k_prod ON d_e_prodotti_confordine(k_d_e_prodotti);
CREATE INDEX IF NOT EXISTS idx_prod_confordine_k_doc ON d_e_prodotti_confordine(k_d_e_confordine);

-- Product table optimization
CREATE INDEX IF NOT EXISTS idx_prodotti_tipologia_del ON d_e_prodotti(tipologia, fl_deleted);

-- Mark stock functions as STABLE PARALLEL SAFE so PostgreSQL caches results and allows parallel plans
DO $$
BEGIN
    ALTER FUNCTION get_totale_disponibile(integer, integer) STABLE PARALLEL SAFE;
    ALTER FUNCTION get_totale_disponibile(integer) STABLE PARALLEL SAFE;
    ALTER FUNCTION get_totale_disponibile(integer, integer, character varying) STABLE PARALLEL SAFE;
EXCEPTION WHEN OTHERS THEN
    -- In case of different function signatures across environments
    NULL;
END $$;
