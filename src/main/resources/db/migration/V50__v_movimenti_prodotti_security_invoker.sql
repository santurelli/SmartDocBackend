-- Imposta security_invoker = on sulla vista v_movimenti_prodotti
-- per fare in modo che le query sulla vista applichino le policy RLS
-- (Row-Level Security) e i permessi dell'utente/tenant chiamante.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_views WHERE viewname = 'v_movimenti_prodotti' AND schemaname = 'public') THEN
        EXECUTE 'ALTER VIEW public.v_movimenti_prodotti SET (security_invoker = on)';
    END IF;
END $$;
