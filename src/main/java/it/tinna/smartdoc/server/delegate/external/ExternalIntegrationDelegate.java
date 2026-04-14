package it.tinna.smartdoc.server.delegate.external;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import it.tinna.smartdoc.server.dao.documenti.FattureDao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.clienti.ClientiDelegate;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.server.delegate.documenti.FattureDelegate;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.clienti.TipologiaClienteFornitore;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.StatoFatturaElettronica;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;
import it.tinna.smartdoc.shared.dto.response.ExternalResponseDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalIntegrationDelegate extends BaseDelegate {

    private final FattureDelegate fattureDelegate;
    private final FatturaElettronicaDelegate fatturaElettronicaDelegate;
    private final ClientiDelegate clientiDelegate;

    @Transactional(rollbackFor = Throwable.class)
    public ExternalResponseDto processaFatturaFastOrder(String nomeStore, it.tinna.smartdoc.shared.dto.external.fastorder.FatturaDto externalFattura) throws Exception {
        String dbKey = DatabaseContextHolder.getClientDatabase();
        log.info("Processamento fattura FastOrder per lo store {} su DB {}", nomeStore, dbKey);

        // 1. Mappatura Cliente
        ClienteDto internalCliente = findOrCreateCliente(externalFattura.getCliente());

        // 1.1 Idempotenza: Verifica esistenza fattura
        Long existingId = null;
        try {
            FattureDao fattureDao = new FattureDao(jdbcTemplate);
            Map<String, Object> existing = jdbcTemplate.queryForList(
                "SELECT k_d_e_fatture, stato_fattura_elettronica FROM d_e_fatture WHERE num_fattura = ? AND data_fattura = TO_DATE(?, 'DD/MM/YYYY') AND k_d_e_clienti = ? AND fl_deleted = 0",
                externalFattura.getNumero(), externalFattura.getData(), internalCliente.getId()
            ).stream().findFirst().orElse(null);

            if (existing != null) {
                existingId = ((Number) existing.get("k_d_e_fatture")).longValue();
                String stato = (String) existing.get("stato_fattura_elettronica");

                // Stati considerati modificabili per un re-invio da esterno
                boolean modificabile = stato == null || 
                                     StatoFatturaElettronica.BO.name().equals(stato) || 
                                     StatoFatturaElettronica.NS.name().equals(stato) || 
                                     StatoFatturaElettronica.DI.name().equals(stato); // DI = Da Inviare / IN_CODA

                if (modificabile) {
                    log.info("Rilevata fattura esistente (ID: {}) in stato {}. Procedo con l'aggiornamento.", existingId, stato);
                    // Pulizia preventiva della tabella centrale per forzare rigenerazione XML
                    fatturaElettronicaDelegate.cancellaFatturaElettronicaCentrale(dbKey, existingId);
                } else {
                    log.warn("Fattura {} già presente in stato {}. Blocca invio.", externalFattura.getNumero(), stato);
                    return ExternalResponseDto.builder()
                            .success(false)
                            .message("Fattura numero " + externalFattura.getNumero() + " già presente ed elaborata (Stato: " + stato + ")")
                            .errorCode("ALREADY_PROCESSED")
                            .build();
                }
            }
        } catch (Exception e) {
            log.error("Errore durante il controllo di idempotenza", e);
        }

        // 2. Mappatura Fattura
        FatturaDto feDto = mapToInternalFattura(externalFattura, internalCliente, nomeStore);

        // 3. Salvataggio Fattura (Insert o Update)
        long idFattura;
        if (existingId != null) {
            feDto.setId(existingId);
            // SYSTEM USER dummy per l'update automatico
            it.tinna.smartdoc.shared.dto.login.UtenteDto systemUser = new it.tinna.smartdoc.shared.dto.login.UtenteDto();
            systemUser.setFatturaElettronica(1);
            
            fattureDelegate.update(systemUser, feDto);
            idFattura = existingId;
            log.info("Fattura ID {} aggiornata correttamente.", idFattura);
        } else {
            idFattura = fattureDelegate.insert(feDto);
            log.info("Nuova fattura inserita con ID: {}", idFattura);
        }
        feDto.setId(idFattura);

        // Ricarico l'oggetto completo dal delegate per assicurarmi che i totali calcolati 
        // dal DAO (tramite get_totale_fattura) siano aggiornati e coerenti
        feDto = fattureDelegate.getById(idFattura);

        // 4. Generazione XML Fattura Elettronica
        try {
            try {
                fatturaElettronicaDelegate.getFatturaElettronica(feDto, nomeStore);
            } catch (Exception e) {
                log.error("Errore critico durante la generazione XML: ", e);
                String msg = "Errore imprevisto durante la generazione XML: " + e.getMessage();
                feDto.setErroreValidazioneXml(StringUtils.defaultString(feDto.getErroreValidazioneXml()) + msg);
            }

            if (StringUtils.isNotEmpty(feDto.getErroreValidazioneXml())) {
                log.warn("Fattura generata con errori di validazione XML: {}", feDto.getErroreValidazioneXml());
            }

            // 5. Memorizzazione per invio SdI
            FatturaElettronicaWrapperDto wrapper = new FatturaElettronicaWrapperDto();
            wrapper.setFattura(feDto);
            
            // Se non ci sono errori, allegato l'XML
            if (StringUtils.isEmpty(feDto.getErroreValidazioneXml()) && feDto.getXmlFattura() != null) {
                wrapper.setFlussoFatturaElettronica(feDto.getXmlFattura().getBytes());
                feDto.setStatoFatturaElettronica(StatoFatturaElettronica.DI);
            }

            // Memorizziamo SEMPRE (anche con errore) per permettere la sincro sul frontend
            fatturaElettronicaDelegate.memorizzaFatturaElettronica(dbKey, wrapper);
            
            // Aggiornamento stato sul DB Tenant
            if (StringUtils.isEmpty(feDto.getErroreValidazioneXml())) {
                fatturaElettronicaDelegate.aggiornaStatoFattura(idFattura, StatoFatturaElettronica.DI);
                log.info("Fattura elettronica memorizzata e stato aggiornato a DI (Da Inviare) per il tenant.");
            } else {
                // Se c'è un errore, assicuriamoci che lo stato rimanga BO (Bozza) per permettere la correzione
                fatturaElettronicaDelegate.aggiornaStatoFattura(idFattura, StatoFatturaElettronica.BO);
                log.warn("Fattura memorizzata in stato BO causa errori di validazione.");
            }
        } catch (Exception e) {
            log.error("Errore fatale durante il salvataggio degli esiti di generazione", e);
        }

        return ExternalResponseDto.builder()
                .success(true)
                .message("Fattura creata con successo. ID: " + idFattura)
                .id(idFattura)
                .build();
    }

    public ExternalResponseDto getEsitoInvio(Long idFattura, String nomeStore) throws SQLException {
        String dbKey = DatabaseContextHolder.getClientDatabase();
        log.info("Richiesta esito invio per fattura ID: {} (Store: {}, DB: {})", idFattura, nomeStore, dbKey);
        
        it.tinna.smartdoc.shared.dto.documenti.EsitoSdiDto esito = fatturaElettronicaDelegate.getEsitoInvioSdi(idFattura, dbKey);
        if (esito != null && esito.getEsito() != null) {
            return ExternalResponseDto.builder()
                    .success(true)
                    .message(esito.getEsito())
                    .build();
        } else {
            return ExternalResponseDto.builder()
                    .success(false)
                    .message("Esito non ancora disponibile")
                    .build();
        }
    }

    private ClienteDto findOrCreateCliente(it.tinna.smartdoc.shared.dto.external.fastorder.ClienteDto extCliente) throws SQLException {
        if (extCliente == null) return null;

        ClienteDto internal = null;
        if (StringUtils.isNotBlank(extCliente.getPartitaIva())) {
            internal = clientiDelegate.getClienteByPartitaIva(extCliente.getPartitaIva());
        }
        if (internal == null && StringUtils.isNotBlank(extCliente.getCodiceFiscale())) {
            internal = clientiDelegate.getClienteByCodiceFiscale(extCliente.getCodiceFiscale());
        }

        if (internal == null) {
            log.info("Cliente non trovato, creazione nuovo cliente: {}", extCliente.getDenominazione());
            internal = new ClienteDto();
            internal.setDenominazione(extCliente.getDenominazione());
            internal.setPartitaIva(extCliente.getPartitaIva());
            internal.setCodiceFiscale(extCliente.getCodiceFiscale());
            internal.setCitta(extCliente.getCitta());
            internal.setPecPrincipale(extCliente.getPec());
            internal.setTipologia(TipologiaClienteFornitore.PRIVATO);
            internal.setCodice(clientiDelegate.generaCodice());
            
            Integer id = clientiDelegate.insert(internal, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            internal.setId(id.longValue());
        }
        return internal;
    }

    private FatturaDto mapToInternalFattura(it.tinna.smartdoc.shared.dto.external.fastorder.FatturaDto extFattura, ClienteDto internalCliente, String nomeStore) {
        FatturaDto feDto = new FatturaDto();
        feDto.setTipoFattura(TipoFattura.FATTURA);
        feDto.setFlFatturaElettronica(1);
        feDto.setStatoFatturaElettronica(StatoFatturaElettronica.BO);
        
        feDto.setClienteDto(internalCliente);
        feDto.setIdCliente((int)internalCliente.getId());
        
        it.tinna.smartdoc.shared.dto.external.fastorder.ClienteDto extCli = extFattura.getCliente();
        feDto.setIndirizzoIntestazione(StringUtils.defaultIfBlank(extCli.getIndirizzo(), "NON SPECIFICATO"));
        feDto.setCittaIntestazione(StringUtils.defaultIfBlank(extCli.getCitta(), "NON SPECIFICATO"));
        feDto.setCapIntestazione(StringUtils.defaultIfBlank(extCli.getCap(), "00000"));
        feDto.setProvinciaIntestazione(StringUtils.defaultIfBlank(extCli.getProvincia(), "EE"));
        feDto.setCodiceUfficioDestinazione(extCli.getCodiceDestinatario());
        feDto.setPec(extCli.getPec());
        feDto.setPartitaIva(extCli.getPartitaIva());
        feDto.setCodiceFiscale(extCli.getCodiceFiscale());

        feDto.setDataDocumento(extFattura.getData());
        feDto.setNumDocumento(extFattura.getNumero());
        feDto.setSplitPayment(0);
        feDto.setFlRitenutaAcconto(0);
        feDto.setEsigibilitaDifferita(0);

        // Recupero ID Tipo Pagamento "Pagato"
        try {
            Integer idTipoPagamento = jdbcTemplate.queryForObject(
                "SELECT k_d_e_tipipagamento FROM d_e_tipipagamento WHERE descrizione = 'Pagato' AND fl_deleted = 0 LIMIT 1", 
                Integer.class);
            feDto.setIdTipoPagamento(idTipoPagamento);
        } catch (Exception e) {
            log.warn("Impossibile trovare il tipo pagamento 'Pagato'. Le scadenze potrebbero non essere calcolate correttamente.");
        }
        
        // Calcolo totali
        Double totale = extFattura.getPagamentoComandaDto().getTotale();
        feDto.setTotale((totale != null && Double.isFinite(totale)) ? totale : 0.0);
        
        Double scontoPerc = extFattura.getPagamentoComandaDto().getSconto();
        if (scontoPerc != null && Double.isFinite(scontoPerc) && scontoPerc > 0) {
            feDto.setSconto(String.valueOf(scontoPerc));
        }



        // Mappatura Righe (Prodotti)
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        BigDecimal calcoloTotaleIva = BigDecimal.ZERO;

        if (StringUtils.isBlank(extFattura.getTestoModelloStampa())) {
            for (it.tinna.smartdoc.shared.dto.external.fastorder.PiattoComandaDto extPiatto : extFattura.getPiatti()) {
                ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
                pdDto.setProdotto(true);
                pdDto.setFuoriMagazzino(true);
                String desc = StringUtils.defaultIfBlank(extPiatto.getDescrizionePiatto(), "Articolo").replaceAll("\\n", " ").replaceAll("\\r", " ").trim();
                pdDto.setFmDescrizione(desc);
                pdDto.setDescProdotto(desc);
                pdDto.setNota(desc); 
                pdDto.setQuantita(Double.valueOf(extPiatto.getQuantita()));
                
                // Mappatura IVA (semplificata come da legacy reader)
                if (extPiatto.getIva().equals(4d)) pdDto.setIdAliquotaIva(11);
                else if (extPiatto.getIva().equals(22d)) pdDto.setIdAliquotaIva(6);
                else pdDto.setIdAliquotaIva(1); // Default 10%
                
                // Prezzo ivato (pieno, lo sconto è gestito a livello di testata)
                BigDecimal prezzoConIva = BigDecimal.valueOf(extPiatto.getPrezzo());
                
                BigDecimal ivaPerc = BigDecimal.valueOf(extPiatto.getIva()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal prezzoSenzaIva = prezzoConIva.divide(BigDecimal.ONE.add(ivaPerc), 2, RoundingMode.HALF_UP);
                
                pdDto.setPrezzo(prezzoSenzaIva.doubleValue());
                BigDecimal rigaTotaleSenzaIva = prezzoSenzaIva.multiply(BigDecimal.valueOf(extPiatto.getQuantita()));
                pdDto.setTotaleSenzaIva(rigaTotaleSenzaIva.doubleValue());
                pdDto.setPrezzoImponibile(rigaTotaleSenzaIva.doubleValue());
                
                // Calcolo IVA riga
                BigDecimal rigaTotaleConIva = prezzoConIva.multiply(BigDecimal.valueOf(extPiatto.getQuantita()));
                calcoloTotaleIva = calcoloTotaleIva.add(rigaTotaleConIva.subtract(rigaTotaleSenzaIva));
                
                prodotti.add(pdDto);
            }
        } else {
            // Caso testo modello stampa (riga unica)
            ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
            pdDto.setProdotto(true);
            pdDto.setFuoriMagazzino(true);
            pdDto.setFmDescrizione(StringUtils.defaultIfBlank(extFattura.getTestoModelloStampa(), "Dettaglio Fattura").replaceAll("\\n", " "));
            pdDto.setFmDescrizione(StringUtils.left(pdDto.getFmDescrizione(), 1000));
            pdDto.setQuantita(1d);
            pdDto.setIdAliquotaIva(1);
            
            BigDecimal prezzoConIva = BigDecimal.valueOf(feDto.getTotale());
            BigDecimal prezzoSenzaIva = prezzoConIva.divide(BigDecimal.valueOf(1.1), 2, RoundingMode.HALF_UP); // Default 10%
            pdDto.setPrezzo(prezzoSenzaIva.doubleValue());
            pdDto.setTotaleSenzaIva(pdDto.getPrezzo());
            pdDto.setPrezzoImponibile(pdDto.getPrezzo());
            
            calcoloTotaleIva = prezzoConIva.subtract(prezzoSenzaIva);
            prodotti.add(pdDto);
        }
        feDto.setProdotti(prodotti);
        feDto.setTotaleIva(calcoloTotaleIva.setScale(2, RoundingMode.HALF_UP).doubleValue());
        feDto.setTotaleDaPagare(feDto.getTotale());

        // Scadenze
        ScadenzaPagamentoDocumentoDto scadenzaDto = new ScadenzaPagamentoDocumentoDto();
        scadenzaDto.setModalitaPagamento(ModalitaPagamentoEnum.CONTANTI.name());
        scadenzaDto.setDtScadenza(extFattura.getData());
        scadenzaDto.setImporto(extFattura.getPagamentoComandaDto().getTotale());
        scadenzaDto.setSaldato(1);
        feDto.setListaScadenzePagamentiDocumento(List.of(scadenzaDto));

        return feDto;
    }
}
