UPDATE d_e_tipipagamento SET modalita = 'MP01' WHERE (modalita = 'CO' OR modalita = 'CONTANTI');
UPDATE d_e_tipipagamento SET modalita = 'MP05' WHERE (modalita = 'BO' OR modalita = 'BONIFICO');
UPDATE d_e_tipipagamento SET modalita = 'MP08' WHERE (modalita = 'CC' OR modalita = 'CARTA_CREDITO' OR modalita = 'CARTA_PAGAMENTO');
UPDATE d_e_tipipagamento SET modalita = 'MP12' WHERE (modalita = 'RI' OR modalita = 'RIBA');
UPDATE d_e_tipipagamento SET modalita = 'MP02' WHERE (modalita = 'AS' OR modalita = 'ASSEGNO');
UPDATE d_e_tipipagamento SET modalita = 'MP06' WHERE (modalita = 'VA' OR modalita = 'VAGLIA');
-- 2. Uniformazione d_e_scadenzepagamentifatture (Dati Documenti)
UPDATE d_e_scadenzepagamentifatture SET modalita = 'MP01' WHERE (modalita = 'CO' OR modalita = 'CONTANTI');
UPDATE d_e_scadenzepagamentifatture SET modalita = 'MP05' WHERE (modalita = 'BO' OR modalita = 'BONIFICO');
UPDATE d_e_scadenzepagamentifatture SET modalita = 'MP08' WHERE (modalita = 'CC' OR modalita = 'CARTA_CREDITO' OR modalita = 'CARTA_PAGAMENTO');
UPDATE d_e_scadenzepagamentifatture SET modalita = 'MP12' WHERE (modalita = 'RI' OR modalita = 'RIBA');
UPDATE d_e_scadenzepagamentifatture SET modalita = 'MP02' WHERE (modalita = 'AS' OR modalita = 'ASSEGNO');
UPDATE d_e_scadenzepagamentifatture SET modalita = 'MP06' WHERE (modalita = 'VA' OR modalita = 'VAGLIA');