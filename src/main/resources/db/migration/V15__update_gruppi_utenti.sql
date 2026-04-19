DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='d_e_utenti' AND column_name='email') THEN
        ALTER TABLE d_e_utenti ADD COLUMN email VARCHAR;
    END IF;
END $$;
-- 2. Pulizia e popolamento iniziale ruoli (Gruppi)
-- (Opzionale/Per sicurezza) Assicuriamoci che l'admin di base venga associato a NULL o aggiornato
-- prima di cancellare la tabella se ci sono constraint. Usa con cautela se ci sono utenti.
-- ALTER TABLE public.d_e_utenti DROP CONSTRAINT d_e_utenti_gruppi_fk;
-- TRUNCATE TABLE d_e_gruppi RESTART IDENTITY CASCADE;
-- In alternativa, semplicemente procediamo con gli inserimenti avendo la primary key stabilita:
-- NOTA: Se si vuole fare TRUNCATE svuoterebbe i gruppi per tutti. Farò un inserimento condizionato o delete mirate.
-- Per evitare problemi con Foreign Keys, usiamo INSERT ON CONFLICT (su PostgreSQL)
-- oppure controlliamo se esiste prima di inserire. Poiché 'k_d_e_gruppi' è la PK:
DELETE FROM d_e_gruppi;
INSERT INTO d_e_gruppi (k_d_e_gruppi, descrizione) VALUES (1, 'ADMIN');
INSERT INTO d_e_gruppi (k_d_e_gruppi, descrizione) VALUES (2, 'ACCOUNTING');
INSERT INTO d_e_gruppi (k_d_e_gruppi, descrizione) VALUES (3, 'SALES');
INSERT INTO d_e_gruppi (k_d_e_gruppi, descrizione) VALUES (4, 'WAREHOUSE');
-- Se ci fosse un utente 'admin' preesistente, assicuriamoci sia un ADMIN (Gruppo 1)
UPDATE d_e_utenti
   SET k_d_e_gruppi = 1
 WHERE username = 'admin';