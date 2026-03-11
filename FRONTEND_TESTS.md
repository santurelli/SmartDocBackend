# Esecuzione Test Frontend (SmartDoc)

Questo documento contiene le istruzioni base per eseguire la suite di test E2E (Playwright) del progetto frontend `smartdoc-frontend`.

## Prerequisiti
Aprire una finestra del terminale e posizionarsi nella cartella del frontend:
```bash
cd C:\PROGETTI_REACT\smartdoc-frontend
```

## Comandi Utili

### 1. Eseguire TUTTI i test (Modalità Veloce / Headless)
Esegue tutte le suite di test presenti nella cartella `e2e` in background.
```bash
npx playwright test --project=chromium
```

### 2. Eseguire TUTTI i test con Interfaccia Grafica (UI Mode)
Apre l'interfaccia interattiva di Playwright, utilissima per fare debug, vedere l'esecuzione step-by-step e navigare nel DOM al momento del fallimento.
```bash
npx playwright test --ui
```

### 3. Eseguire un SOLO file di test
Se stai lavorando a una specifica feature e vuoi lanciare solo i relativi test, puoi specificare il percorso del file.
Esempio per la fatturazione standard:
```bash
npx playwright test e2e/fattura-standard.spec.js --project=chromium
```

### 4. Visualizzare il Report Grafico
Al termine di un'esecuzione (specie se ci sono stati errori), puoi visualizzare il report HTML dettagliato tramite:
```bash
npx playwright show-report
```
