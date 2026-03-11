package it.tinna.smartdoc.server.delegate.external;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        // 2. Mappatura Fattura
        FatturaDto feDto = mapToInternalFattura(externalFattura, internalCliente, nomeStore);

        // 3. Inserimento Fattura
        long idFattura = fattureDelegate.insert(feDto);
        feDto.setId(idFattura);
        log.info("Fattura inserita correttamente con ID: {}", idFattura);

        // 4. Generazione XML Fattura Elettronica
        try {
            fatturaElettronicaDelegate.getFatturaElettronica(feDto, nomeStore);
            if (StringUtils.isNotEmpty(feDto.getErroreValidazioneXml())) {
                log.warn("Fattura generata con errori di validazione XML: {}", feDto.getErroreValidazioneXml());
            }

            // 5. Memorizzazione per invio SdI
            FatturaElettronicaWrapperDto wrapper = new FatturaElettronicaWrapperDto();
            wrapper.setFattura(feDto);
            if (StringUtils.isEmpty(feDto.getErroreValidazioneXml())) {
                wrapper.setFlussoFatturaElettronica(feDto.getXmlFattura().getBytes());
            }

            fatturaElettronicaDelegate.memorizzaFatturaElettronica(dbKey, wrapper);
            log.info("Fattura elettronica memorizzata per l'invio.");
        } catch (Exception e) {
            log.error("Errore durante la generazione/memorizzazione della fattura elettronica", e);
            // Non facciamo rollback della fattura "normale", ma segnaliamo l'errore
        }

        return ExternalResponseDto.builder()
                .success(true)
                .message("Fattura creata con successo. ID: " + idFattura)
                .id(idFattura)
                .build();
    }

    public ExternalResponseDto getEsitoInvio(Long idFattura, String nomeStore) throws SQLException {
        var esito = fatturaElettronicaDelegate.getEsitoInvioSdi(idFattura, nomeStore);
        if (esito != null) {
            return ExternalResponseDto.builder()
                    .success(true)
                    .message("Esito recuperato: " + esito.getEsito())
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
        feDto.setStatoFatturaElettronica(StatoFatturaElettronica.DI);
        
        feDto.setClienteDto(internalCliente);
        feDto.setIdCliente((int)internalCliente.getId());
        
        it.tinna.smartdoc.shared.dto.external.fastorder.ClienteDto extCli = extFattura.getCliente();
        feDto.setIndirizzoIntestazione(extCli.getIndirizzo());
        feDto.setCittaIntestazione(extCli.getCitta());
        feDto.setCapIntestazione(extCli.getCap());
        feDto.setProvinciaIntestazione(extCli.getProvincia());
        feDto.setCodiceUfficioDestinazione(extCli.getCodiceDestinatario());
        feDto.setPec(extCli.getPec());
        feDto.setPartitaIva(extCli.getPartitaIva());
        feDto.setCodiceFiscale(extCli.getCodiceFiscale());

        feDto.setDataDocumento(extFattura.getData());
        feDto.setNumDocumento(extFattura.getNumero());
        
        // Calcolo totali
        feDto.setTotale(extFattura.getPagamentoComandaDto().getTotale());
        if (extFattura.getPagamentoComandaDto().getSconto() != null && extFattura.getPagamentoComandaDto().getSconto() != 0d) {
             feDto.setTotale(BigDecimal.valueOf(feDto.getTotale()).add(BigDecimal.valueOf(extFattura.getPagamentoComandaDto().getSconto())).doubleValue());
        }

        // Mappatura Righe (Prodotti)
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        if (StringUtils.isBlank(extFattura.getTestoModelloStampa())) {
            for (var extPiatto : extFattura.getPiatti()) {
                ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
                pdDto.setFuoriMagazzino(true);
                pdDto.setFmDescrizione(extPiatto.getDescrizionePiatto());
                pdDto.setQuantita(Double.valueOf(extPiatto.getQuantita()));
                
                // Mappatura IVA (semplificata come da legacy reader)
                if (extPiatto.getIva().equals(4d)) pdDto.setIdAliquotaIva(11);
                else if (extPiatto.getIva().equals(22d)) pdDto.setIdAliquotaIva(6);
                else pdDto.setIdAliquotaIva(1); // Default 10%
                
                // Prezzo ivato -> Netto
                BigDecimal prezzoConIva = BigDecimal.valueOf(extPiatto.getPrezzo());
                BigDecimal ivaPerc = BigDecimal.valueOf(extPiatto.getIva()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal prezzoSenzaIva = prezzoConIva.divide(BigDecimal.ONE.add(ivaPerc), 2, RoundingMode.HALF_UP);
                pdDto.setPrezzo(prezzoSenzaIva.doubleValue());
                pdDto.setTotaleSenzaIva(prezzoSenzaIva.multiply(BigDecimal.valueOf(extPiatto.getQuantita())).doubleValue());
                
                prodotti.add(pdDto);
            }
        } else {
            // Caso testo modello stampa (riga unica)
            ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
            pdDto.setFuoriMagazzino(true);
            pdDto.setFmDescrizione(StringUtils.left(extFattura.getTestoModelloStampa().replaceAll("\\n", " "), 1000));
            pdDto.setQuantita(1d);
            pdDto.setIdAliquotaIva(1);
            
            BigDecimal prezzoConIva = BigDecimal.valueOf(feDto.getTotale());
            BigDecimal prezzoSenzaIva = prezzoConIva.divide(BigDecimal.valueOf(1.1), 2, RoundingMode.HALF_UP); // Default 10%
            pdDto.setPrezzo(prezzoSenzaIva.doubleValue());
            pdDto.setTotaleSenzaIva(pdDto.getPrezzo());
            prodotti.add(pdDto);
        }
        feDto.setProdotti(prodotti);

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
