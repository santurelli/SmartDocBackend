package it.tinna.smartdoc.server.delegate.ai;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.tinna.smartdoc.server.integration.ai.OpenAiDocumentExtractionService;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.fornitori.FornitoriDelegate;
import it.tinna.smartdoc.server.delegate.prodotti.ProdottiDelegate;
import it.tinna.smartdoc.shared.dto.ai.EstrazioneFatturaFornitoreDto;
import it.tinna.smartdoc.shared.dto.ai.EstrazioneRigaDto;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiEstrazioneDelegate extends BaseDelegate {

    private static final String OPERAZIONE_ESTRAZIONE_FATTURA_FORNITORE = "ESTRAZIONE_FATTURA_FORNITORE";

    // Soglia minima di similarita' (Jaccard sulle parole) per proporre un abbinamento articolo.
    // Deliberatamente alta: un suggerimento sbagliato e' peggio di nessun suggerimento, dato che
    // l'utente potrebbe fidarsi ad occhio senza controllare bene ogni riga.
    private static final double SOGLIA_SIMILARITA = 0.5;

    @Autowired
    private OpenAiDocumentExtractionService openAiDocumentExtractionService;

    @Autowired
    private FornitoriDelegate fornitoriDelegate;

    @Autowired
    private ProdottiDelegate prodottiDelegate;

    public EstrazioneFatturaFornitoreDto estraiFatturaFornitore(MultipartFile file) throws IOException, SQLException {
        EstrazioneFatturaFornitoreDto dto = openAiDocumentExtractionService.estrai(file);

        if (StringUtils.isNotBlank(dto.getErroreEstrazione())) {
            registraUtilizzo(OPERAZIONE_ESTRAZIONE_FATTURA_FORNITORE, "ERRORE", dto.getErroreEstrazione());
            return dto;
        }

        try {
            FornitoreDto esistente = trovaFornitore(dto);
            if (esistente != null) {
                dto.setIdFornitoreTrovato((int) esistente.getId());
                dto.setDenominazioneFornitoreTrovato(esistente.getDenominazione());
            }
        } catch (Exception e) {
            log.warn("Errore nella ricerca del fornitore estratto dal documento", e);
        }

        suggerisciArticoli(dto);
        registraUtilizzo(OPERAZIONE_ESTRAZIONE_FATTURA_FORNITORE, "OK", null);

        return dto;
    }

    /**
     * Log di utilizzo per tenant (d_e_ai_log_utilizzo), per poter contare/monitorare le chiamate
     * AI in futuro (es. limiti per piano, costi). Non deve mai far fallire l'estrazione: un errore
     * nella scrittura del log viene solo loggato, non propagato.
     */
    private void registraUtilizzo(String tipoOperazione, String esito, String dettaglioErrore) {
        try {
            jdbcTemplate.update(
                "INSERT INTO d_e_ai_log_utilizzo (tipo_operazione, esito, dettaglio_errore) VALUES (?, ?, ?)",
                tipoOperazione, esito, dettaglioErrore != null ? org.apache.commons.lang3.StringUtils.left(dettaglioErrore, 2000) : null);
        } catch (Exception e) {
            log.warn("Impossibile registrare l'utilizzo AI nel log", e);
        }
    }

    /**
     * Cascata di ricerca del fornitore gia' in anagrafica, dal segnale piu' affidabile al meno
     * affidabile: Partita IVA esatta, Partita IVA "ripulita" (spazi/prefisso IT/maiuscole
     * ignorati, per tollerare le differenze di formattazione tipiche dell'OCR), denominazione
     * esatta come ultima spiaggia se la Partita IVA non e' stata estratta.
     */
    private FornitoreDto trovaFornitore(EstrazioneFatturaFornitoreDto dto) throws SQLException {
        String piva = dto.getPartitaIvaFornitore();
        if (StringUtils.isNotBlank(piva)) {
            FornitoreDto esistente = fornitoriDelegate.getByPartitaIva(piva.trim());
            if (esistente != null) {
                return esistente;
            }
            String pivaNormalizzata = piva.trim().toUpperCase().replaceAll("[^0-9A-Z]", "");
            if (StringUtils.isNotBlank(pivaNormalizzata)) {
                esistente = fornitoriDelegate.getByPartitaIvaNormalizzata(pivaNormalizzata);
                if (esistente != null) {
                    return esistente;
                }
            }
        }
        if (StringUtils.isNotBlank(dto.getDenominazioneFornitore())) {
            return fornitoriDelegate.getByDenominazioneEsatta(dto.getDenominazioneFornitore().trim());
        }
        return null;
    }

    private void suggerisciArticoli(EstrazioneFatturaFornitoreDto dto) {
        if (dto.getRighe() == null || dto.getRighe().isEmpty()) {
            return;
        }
        List<ProdottoDto> catalogo;
        try {
            catalogo = prodottiDelegate.getList(null, "", 5000, 0, 0, "asc", null, null, null, null, null, null, null);
        } catch (Exception e) {
            log.warn("Impossibile caricare il catalogo articoli per il suggerimento di abbinamento", e);
            return;
        }
        if (catalogo == null || catalogo.isEmpty()) {
            return;
        }

        for (EstrazioneRigaDto riga : dto.getRighe()) {
            if (StringUtils.isBlank(riga.getDescrizione())) {
                continue;
            }
            Set<String> paroleRiga = tokenizza(riga.getDescrizione());
            if (paroleRiga.isEmpty()) {
                continue;
            }

            ProdottoDto migliore = null;
            double punteggioMigliore = 0;
            for (ProdottoDto prodotto : catalogo) {
                if (StringUtils.isBlank(prodotto.getDescrizione())) {
                    continue;
                }
                double punteggio = similaritaJaccard(paroleRiga, tokenizza(prodotto.getDescrizione()));
                if (punteggio > punteggioMigliore) {
                    punteggioMigliore = punteggio;
                    migliore = prodotto;
                }
            }

            if (migliore != null && punteggioMigliore >= SOGLIA_SIMILARITA) {
                riga.setIdProdottoSuggerito((int) migliore.getId());
                riga.setCodiceProdottoSuggerito(migliore.getCodice());
                riga.setDescrizioneProdottoSuggerito(migliore.getDescrizione());
            }
        }
    }

    private Set<String> tokenizza(String testo) {
        String normalizzato = testo.toLowerCase()
                .replaceAll("[^a-z0-9àèéìòù\\s]", " ")
                .trim();
        if (normalizzato.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> parole = new HashSet<>(Arrays.asList(normalizzato.split("\\s+")));
        parole.removeIf(p -> p.length() < 3); // scarta articoli/preposizioni troppo corti
        return parole;
    }

    private double similaritaJaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }
        Set<String> intersezione = new HashSet<>(a);
        intersezione.retainAll(b);
        Set<String> unione = new HashSet<>(a);
        unione.addAll(b);
        return (double) intersezione.size() / unione.size();
    }
}
