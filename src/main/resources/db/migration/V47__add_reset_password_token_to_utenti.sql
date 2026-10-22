DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='d_e_utenti' AND column_name='reset_password_token') THEN
        ALTER TABLE d_e_utenti ADD COLUMN reset_password_token VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='d_e_utenti' AND column_name='reset_password_expiry') THEN
        ALTER TABLE d_e_utenti ADD COLUMN reset_password_expiry TIMESTAMP;
    END IF;
END $$;
