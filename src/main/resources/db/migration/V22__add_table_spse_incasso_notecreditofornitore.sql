CREATE TABLE public.d_e_speseincasso_notecreditofornitore (
    k_d_e_speseincasso_notecreditofornitore serial PRIMARY KEY,
    k_d_e_notecreditofornitore integer,
    k_d_e_speseincasso integer,
    k_d_e_aliquoteiva integer,
    importo numeric(10,2),
    tenant_id BIGINT DEFAULT NULLIF(current_setting('app.current_tenant', true), '')::bigint,
    CONSTRAINT d_e_speseincasso_notecreditofornitore_fk_d_e_aliquoteiva FOREIGN KEY (k_d_e_aliquoteiva) REFERENCES d_e_aliquoteiva(k_d_e_aliquoteiva),
    CONSTRAINT d_e_speseincasso_notecreditofornitore_fk_d_e_notecreditofornitore FOREIGN KEY (k_d_e_notecreditofornitore) REFERENCES d_e_notecreditofornitore(k_d_e_notecreditofornitore) ON DELETE CASCADE,
    CONSTRAINT d_e_speseincasso_notecreditofornitore_fk_d_e_speseincasso FOREIGN KEY (k_d_e_speseincasso) REFERENCES d_e_speseincasso(k_d_e_speseincasso)
);

CREATE INDEX IF NOT EXISTS idx_d_e_speseincasso_notecreditofornitore_tenant_id ON public.d_e_speseincasso_notecreditofornitore (tenant_id);

ALTER TABLE public.d_e_speseincasso_notecreditofornitore ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.d_e_speseincasso_notecreditofornitore FORCE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation_policy ON public.d_e_speseincasso_notecreditofornitore 
    USING (tenant_id = NULLIF(current_setting('app.current_tenant', true), '')::bigint);