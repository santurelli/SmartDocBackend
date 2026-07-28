# Guida di Migrazione a Shared Database (Multi-Tenancy con RLS)

Questa guida documenta i passaggi esatti per esportare un database cliente singolo (es. `sd_publipeas`) e importarlo all'interno del database condiviso (`smartdoc_shared_db`) associandogli il rispettivo `tenant_id` numerico, allineando le sequenze e pulendo gli schemi temporanei.

---

## Spegnimento di Sicurezza e Controllo del Traffico

> [!WARNING]
> Prima di avviare qualsiasi attività di migrazione dei dati, è **fondamentale fermare SmartDoc** e i relativi servizi batch in background. Questo evita che utenti o sistemi integrati (come FastOrder) tentino di salvare nuovi dati nel vecchio database o in quello condiviso durante il processo di ETL, scongiurando inconsistenze e perdita di dati.

### 1. Arresto dei Servizi tramite Docker Compose

SmartDoc è distribuito tramite Docker Compose nella cartella `C:\PROGETTI\deploy\smartdoc`. Per spegnere l'applicazione mantenendo attivo il database per eseguire l'ETL:

1. **Apri una console Powershell** come Amministratore e posizionati nella cartella di deploy:
   ```powershell
   cd C:\PROGETTI\deploy\smartdoc
   ```
2. **Ferma i container `backend` e `frontend`** (questo fermerà le API web di SmartDoc, Caddy e tutti i batch di background, lasciando attivi `smartdoc-db`, `smartdoc-pgbouncer` e `smartdoc-redis` necessari per la migrazione):
   ```powershell
   docker-compose stop backend frontend
   ```

---

## Esempio Pratico: Migrazione di `sd_publipeas` (Tenant ID: 15)

Sostituisci `sd_publipeas` con il nome del database sorgente, `temp_publipeas` con il nome dello schema temporaneo desiderato, e `15` con il rispettivo `k_d_e_enti` estratto da `smartdoc_service_db.d_e_enti`.

### Fase 1: Esportazione e Rinomina dello Schema (Tramite DB di appoggio)

Esegui questi comandi all'interno del terminale Powershell (dalla cartella di deploy `C:\PROGETTI\deploy\smartdoc` dove il database `smartdoc-db` è attivo):

```powershell
# 1. Effettua il dump del database del cliente originale (es. sd_publipeas)
docker exec -i smartdoc-db pg_dump -U smartdoc -F c -b -v -d sd_publipeas > C:\Users\PIETRO\sd_publipeas.backup

# 2. Ripristina il database di appoggio 'temp_appoggio' a uno stato pulito
docker exec -it smartdoc-db psql -U smartdoc -d postgres -c "DROP DATABASE IF EXISTS temp_appoggio WITH (FORCE);"
docker exec -it smartdoc-db psql -U smartdoc -d postgres -c "CREATE DATABASE temp_appoggio;"

# 3. Ripristina il dump dentro 'temp_appoggio'
docker exec -i smartdoc-db pg_restore -U smartdoc -d temp_appoggio -v < C:\Users\PIETRO\sd_publipeas.backup

# 4. Rinomina lo schema 'public' con il nome temporaneo del cliente
docker exec -it smartdoc-db psql -U smartdoc -d temp_appoggio -c "ALTER SCHEMA public RENAME TO temp_publipeas;"

# 5. Esporta lo schema appena rinominato da 'temp_appoggio'
docker exec -i smartdoc-db pg_dump -U smartdoc -d temp_appoggio -F c -n temp_publipeas -v > C:\Users\PIETRO\temp_publipeas.backup
```

---

### Fase 2: Importazione e Unione dei Dati (ETL)

```powershell
# 6. Ripristina lo schema rinominato all'interno del database condiviso (smartdoc_shared_db)
docker exec -i smartdoc-db pg_restore -U smartdoc -d smartdoc_shared_db -v < C:\Users\PIETRO\temp_publipeas.backup

# 7. Esegui la funzione ETL per migrare i dati nello schema 'public' col tenant corretto
docker exec -it smartdoc-db psql -U smartdoc -d smartdoc_shared_db -c "SELECT merge_tenant_data('temp_publipeas', 15);"

# 8. Allinea tutte le sequenze del database per evitare conflitti di chiavi duplicate
Get-Content -Raw C:\WKS-OLD\smartdoc\.agent\migration\03_align_sequences.sql | docker exec -i smartdoc-db psql -U smartdoc -d smartdoc_shared_db

# 9. Elimina lo schema temporaneo per mantenere il database condiviso pulito
docker exec -it smartdoc-db psql -U smartdoc -d smartdoc_shared_db -c "DROP SCHEMA temp_publipeas CASCADE;"

# 10. Riavvia l'applicazione SmartDoc al termine di tutte le migrazioni
docker-compose start backend frontend
```

---

## Script di Supporto: Allineamento Sequenze (`03_align_sequences.sql`)

Qualora avessi bisogno di rieseguire lo script di allineamento delle sequenze indipendentemente, il file è salvato in `c:\WKS-OLD\smartdoc\.agent\migration\03_align_sequences.sql` ed ha il seguente contenuto:

```sql
DO $$
DECLARE
    r RECORD;
    max_val BIGINT;
BEGIN
    FOR r IN 
        SELECT 
            t.table_name, 
            c.column_name, 
            pg_get_serial_sequence('public.' || t.table_name, c.column_name) AS seq_name
        FROM information_schema.tables t
        JOIN information_schema.columns c ON t.table_name = c.table_name AND t.table_schema = c.table_schema
        WHERE t.table_schema = 'public' 
          AND t.table_type = 'BASE TABLE'
    LOOP
        IF r.seq_name IS NOT NULL THEN
            -- Calcola il valore massimo effettivo presente nella tabella
            EXECUTE format('SELECT COALESCE(MAX(%I), 0) FROM public.%I', r.column_name, r.table_name) INTO max_val;
            
            -- Imposta la sequenza al MAX(id) corrente
            IF max_val > 0 THEN
                EXECUTE format('SELECT setval(%L, %s)', r.seq_name, max_val);
            ELSE
                EXECUTE format('SELECT setval(%L, 1)', r.seq_name);
            END IF;
        END IF;
    END LOOP;
END $$;
```
