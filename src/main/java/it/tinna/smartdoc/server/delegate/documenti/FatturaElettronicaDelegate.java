package it.tinna.smartdoc.server.delegate.documenti;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.springframework.web.multipart.MultipartFile;
import jakarta.xml.bind.Unmarshaller;
import java.io.InputStream;

import it.tinna.smartdoc.batch.dto.NotificaFatturaDto;
import it.tinna.smartdoc.batch.enums.ConfigurazioneDomain;
import it.tinna.smartdoc.batch.enums.ConfigurazioneKey;
import it.tinna.smartdoc.server.constants.EsigibilitaIvaEnum;
import it.tinna.smartdoc.server.constants.FormatoTrasmissioneEnum;
import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.server.constants.NaturaEsenzioneEnum;
import it.tinna.smartdoc.server.constants.RegimeFiscaleEnum;
import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;
import it.tinna.smartdoc.server.constants.TipoPagamentoEnum;
import it.tinna.smartdoc.server.constants.TipoRitenutaEnum;
import it.tinna.smartdoc.server.constants.CausalePagamentoEnum;
import it.tinna.smartdoc.server.constants.TipoScontoDocumentoEnum;
import it.tinna.smartdoc.server.constants.TipoCassaEnum;
import it.tinna.smartdoc.server.xml.sdi.SdiFormatter;
import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.dao.configurazione.ConfigurazioneDao;
import it.tinna.smartdoc.server.dao.datiazienda.DatiAziendaDao;
import it.tinna.smartdoc.server.dao.documenti.FatturaElettronicaDao;
import it.tinna.smartdoc.server.dao.documenti.FattureDao;
import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass.ErroreType;
import it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass.NotificaMancataConsegnaType;
import it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass.NotificaScartoType;
import it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass.RicevutaConsegnaType;
import it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass.EsitoFTPType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.AnagraficaType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.CedentePrestatoreType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.CessionarioCommittenteType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiAnagraficiCedenteType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiAnagraficiCessionarioType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiBeniServiziType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiDocumentiCorrelatiType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiGeneraliDocumentoType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiGeneraliType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiPagamentoType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiRiepilogoType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiRitenutaType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiTrasmissioneType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiCassaPrevidenzialeType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DettaglioLineeType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DettaglioPagamentoType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.FatturaElettronicaBodyType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.FatturaElettronicaHeaderType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.FatturaElettronicaType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.IdFiscaleType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.IndirizzoType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.ObjectFactory;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.ScontoMaggiorazioneType;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import it.tinna.smartdoc.shared.dto.clienti.TipologiaClienteFornitore;
import it.tinna.smartdoc.shared.dto.configurazione.ConfigurazioneDto;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.EsitoSdiDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.StatoFatturaElettronica;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;
import it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;
import it.tinna.smartdoc.server.xml.sdi.SdiFormatter;

@Service(value = "fatturaelettronicaDelegate")
public class FatturaElettronicaDelegate extends BaseDelegate
{
    @Autowired
    @Qualifier("serviceJdbcTemplate")
    private JdbcTemplate serviceJdbcTemplate;

    @Autowired
    private NumerazioneFatturaElettronicaDelegate numerazioneFatturaElettronicaDelegate;

    @Autowired
    private it.tinna.smartdoc.server.dao.nazioni.NazioniDao nazioniDao;

    public void aggiornaDatiEsitoSdi(File fileEsitoSdi,
                                     NotificaMancataConsegnaType notificaMancataConsegna) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        dao.aggiornaDatiNotificaMancataConsegna(notificaMancataConsegna.getIdentificativoSdI().toString(), notificaMancataConsegna.getMessageId(), notificaMancataConsegna.getDataOraRicezione(), notificaMancataConsegna.getDescrizione(), fileEsitoSdi.getAbsolutePath(), FilenameUtils.getBaseName(notificaMancataConsegna.getNomeFile()).split("_")[1]);
        dao.memorizzaEsitoSdi(FilenameUtils.getBaseName(notificaMancataConsegna.getNomeFile()).split("_")[1]);
    }

    public void aggiornaDatiEsitoSdi(File fileEsitoSdi,
                                     NotificaScartoType notificaScarto) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        StringBuilder strErrori = new StringBuilder("");
        if ( notificaScarto.getListaErrori().getErrore() != null && !notificaScarto.getListaErrori().getErrore().isEmpty() )
        {
            for ( int i = 0; i < notificaScarto.getListaErrori().getErrore().size(); i++ )
            {
                ErroreType errore = notificaScarto.getListaErrori().getErrore().get(i);
                if ( i > 0 )
                {
                    strErrori.append("|");
                }
                strErrori.append("Codice: ").append(errore.getCodice()).append(" - Descrizione: ").append(errore.getDescrizione());
            }
        }
        dao.aggiornaDatiNotificaScarto(notificaScarto.getIdentificativoSdI().toString(), notificaScarto.getMessageId(), notificaScarto.getDataOraRicezione(), strErrori.toString(), fileEsitoSdi.getAbsolutePath(), FilenameUtils.getBaseName(notificaScarto.getNomeFile()).split("_")[1]);
        dao.memorizzaEsitoSdi(FilenameUtils.getBaseName(notificaScarto.getNomeFile()).split("_")[1]);
    }

    public void aggiornaDatiEsitoSdi(File fileEsitoSdi,
                                     RicevutaConsegnaType ricevutaConsegna) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        dao.aggiornaDatiRicevutaConsegna(ricevutaConsegna.getIdentificativoSdI().toString(), ricevutaConsegna.getMessageId(), ricevutaConsegna.getDataOraConsegna(), ricevutaConsegna.getDestinatario().getDescrizione(), fileEsitoSdi.getAbsolutePath(), FilenameUtils.getBaseName(ricevutaConsegna.getNomeFile()).split("_")[1]);
        dao.memorizzaEsitoSdi(FilenameUtils.getBaseName(ricevutaConsegna.getNomeFile()).split("_")[1]);
    }

    public void aggiornaDatiInvioSupporto(EsitoFTPType esitoInvio) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        dao.aggiornaDatiInvioSupporto(esitoInvio);
    }

    public void aggiornaStatoFattura(long idFattura,
                                     StatoFatturaElettronica statoFattura) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(jdbcTemplate);
        dao.aggiornaStatoFattura(idFattura, statoFattura);
    }

    public List<EsitoSdiDto> getEsitiInvioSdi(List<Long> idFatture,
                                              String dbKey) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        return dao.getEsitiInvioSdi(idFatture, dbKey);
    }

    public EsitoSdiDto getEsitoInvioSdi(long idFattura,
                                        String dbKey) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        return dao.getEsitoInvioSdi(idFattura, dbKey);
    }

    /**
     * Genera la fattura elettronica per una data fattura/nota credito Il nome store viene usato soltanto per just Design
     * 
     * @param dto
     * @param nomeStore
     * @throws SQLException
     * @throws SAXException
     * @throws IOException
     * @throws JAXBException
     * @throws ParseException
     */
    public void getFatturaElettronica(FatturaElettronicaDto dto,
                                      String nomeStore) throws SQLException, SAXException, IOException, JAXBException, ParseException
    {
        AliquoteIvaDao aiDao = new AliquoteIvaDao(jdbcTemplate);
        TipiPagamentoDao tpDao = new TipiPagamentoDao(jdbcTemplate);
        DatiAziendaDao datiAziendaDao = new DatiAziendaDao(jdbcTemplate);
        DatiAziendaDto datiAziendaDto = datiAziendaDao.getDatiAzienda();
        
        if (dto instanceof FatturaDto && TipoFattura.FATTURA_SEMPLIFICATA.equals(((FatturaDto)dto).getTipoFattura())) {
            getFatturaElettronicaSemplificata(dto, nomeStore);
            return;
        }

        FatturaElettronicaType fatturaElettronica = new FatturaElettronicaType();
        fatturaElettronica.setVersione(dto.getClienteDto().getTipologia() == TipologiaClienteFornitore.PUBBLICA_AMMINISTRAZIONE ? FormatoTrasmissioneEnum.FATTURA_PA : FormatoTrasmissioneEnum.FATTURA_PRIVATI);
        // FatturaElettronicaHeader
        FatturaElettronicaHeaderType header = new FatturaElettronicaHeaderType();
        fatturaElettronica.setFatturaElettronicaHeader(header);
        // DatiTrasmissione
        DatiTrasmissioneType datiTrasmissione = new DatiTrasmissioneType();
        header.setDatiTrasmissione(datiTrasmissione);
        // idTrasmittente
        IdFiscaleType idTrasmittente = new IdFiscaleType();
        datiTrasmissione.setIdTrasmittente(idTrasmittente);
        idTrasmittente.setIdPaese("IT");
        idTrasmittente.setIdCodice(datiAziendaDto.getPartitaIva());
        try
        {
            Date dateFattura = null;
            try {
                dateFattura = FastDateFormat.getInstance("dd/MM/yyyy").parse(dto.getDataDocumento());
            } catch (Exception e) {
                String currentErr = StringUtils.defaultString(dto.getErroreValidazioneXml());
                dto.setErroreValidazioneXml(currentErr + "Data documento non valida o mancante; ");
                dateFattura = new Date();
                dto.setDataDocumento(FastDateFormat.getInstance("dd/MM/yyyy").format(dateFattura));
            }
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFattura);
            String progressivo = numerazioneFatturaElettronicaDelegate.getNumero(cal.get(Calendar.YEAR));
            // ProgressivoInvio
            datiTrasmissione.setProgressivoInvio(progressivo);
            // FormatoTrasmissione
            datiTrasmissione.setFormatoTrasmissione(dto.getClienteDto().getTipologia() == TipologiaClienteFornitore.PUBBLICA_AMMINISTRAZIONE ? FormatoTrasmissioneEnum.FATTURA_PA : FormatoTrasmissioneEnum.FATTURA_PRIVATI);
            // CodiceDestinatario
            if ( StringUtils.isNotBlank(dto.getCodiceUfficioDestinazione()) )
            {
                datiTrasmissione.setCodiceDestinatario(dto.getCodiceUfficioDestinazione().toUpperCase());
            }
            else
            {
                datiTrasmissione.setCodiceDestinatario("0000000");
            }
            // PEC Destinatario
            if ( StringUtils.isNotBlank(dto.getPec()) )
            {
                datiTrasmissione.setPECDestinatario(dto.getPec());
            }
            // CedentePrestatore
            CedentePrestatoreType cedentePrestatore = new CedentePrestatoreType();
            header.setCedentePrestatore(cedentePrestatore);
            // DatiAnagrafici
            DatiAnagraficiCedenteType datiAnagrafici = new DatiAnagraficiCedenteType();
            cedentePrestatore.setDatiAnagrafici(datiAnagrafici);
            // IdFiscaleIVA
            IdFiscaleType idFiscaleIVA = new IdFiscaleType();
            datiAnagrafici.setIdFiscaleIVA(idFiscaleIVA);
            idFiscaleIVA.setIdPaese("IT");
            idFiscaleIVA.setIdCodice(datiAziendaDto.getPartitaIva());
            // CodiceFiscale non obbligatorio. Non viene valorizzato
            // Anagrafica
            AnagraficaType anagrafica = new AnagraficaType();
            datiAnagrafici.setAnagrafica(anagrafica);
            anagrafica.setDenominazione(StringUtils.defaultIfBlank(datiAziendaDto.getDenominazione(), "AZIENDA NON SPECIFICATA"));
            if ( StringUtils.isNotBlank(nomeStore) )
            {
                anagrafica.setDenominazione(new StringBuilder(anagrafica.getDenominazione()).append(" - Negozio ").append(nomeStore).toString());
            }
            // AlboProfessionale, ProvinciaAlbo, NumeroIscrizioneAlbo, DataIscrizioneAlbo non sono valorizzati
            datiAnagrafici.setRegimeFiscale(RegimeFiscaleEnum.valueOf(datiAziendaDto.getValoreRegimeFiscale()));
            // Sede
            IndirizzoType sede = new IndirizzoType();
            cedentePrestatore.setSede(sede);
            sede.setIndirizzo(StringUtils.substring(StringUtils.defaultIfBlank(datiAziendaDto.getIndirizzo(), "NON SPECIFICATO"), 0, 60));
            sede.setCAP(StringUtils.defaultIfBlank(datiAziendaDto.getCap(), "00000"));
            sede.setComune(StringUtils.substring(StringUtils.defaultIfBlank(datiAziendaDto.getCitta(), "NON SPECIFICATO"), 0, 60));
            sede.setProvincia(StringUtils.defaultIfBlank(datiAziendaDto.getProvincia(), "EE").toUpperCase());
            sede.setNazione("IT"); // TODO salvare la nazione azienda tramite la pagina jsp di configurazione dati azienda
            // StabileOrganizzazione non obbligatorio. Non viene valorizzato
            // IscrizioneREA non obbligatorio. Non viene valorizzato
            // Contatti non obbligatorio. Non viene valorizzato
            // RiferimentoAmministrazione non obbligatorio. Non viene valorizzato
            // RappresentanteFiscale non obbligatorio. Non viene valorizzato
            // CessionarioCommittente
            CessionarioCommittenteType cessionarioCommittente = new CessionarioCommittenteType();
            header.setCessionarioCommittente(cessionarioCommittente);
            DatiAnagraficiCessionarioType datiAnagraficiCessionario = new DatiAnagraficiCessionarioType();
            cessionarioCommittente.setDatiAnagrafici(datiAnagraficiCessionario);
            // IdFiscaleIVA oppure CodiceFiscale
            // Prioritize tax data from the document header, fallback to customer card
            String pIva = StringUtils.defaultIfBlank(dto.getPartitaIva(), (dto.getClienteDto() != null ? dto.getClienteDto().getPartitaIva() : ""));
            String cf = StringUtils.defaultIfBlank(dto.getCodiceFiscale(), (dto.getClienteDto() != null ? dto.getClienteDto().getCodiceFiscale() : ""));
            String nazione = StringUtils.defaultIfBlank(dto.getNazioneIntestazione(), "Italia");
            String isoCliente = nazioniDao.getCodiceIsoByNome(nazione);
            if (StringUtils.isBlank(isoCliente)) {
                isoCliente = "IT";
            }

            // IdFiscaleIVA
            if ( StringUtils.isNotBlank(pIva) )
            {
                IdFiscaleType idFiscaleIVACessionario = new IdFiscaleType();
                datiAnagraficiCessionario.setIdFiscaleIVA(idFiscaleIVACessionario);
                idFiscaleIVACessionario.setIdPaese(isoCliente);
                idFiscaleIVACessionario.setIdCodice(pIva);
            }
            // Always set CodiceFiscale if present (SDI allows both)
            if ( StringUtils.isNotBlank(cf) )
            {
                datiAnagraficiCessionario.setCodiceFiscale(cf.toUpperCase());
            }
            // Anagrafica
            AnagraficaType anagraficaCessionario = new AnagraficaType();
            datiAnagraficiCessionario.setAnagrafica(anagraficaCessionario);
            anagraficaCessionario.setDenominazione(dto.getClienteDto().getDenominazione());
            // Sede
            IndirizzoType sedeCessionario = new IndirizzoType();
            cessionarioCommittente.setSede(sedeCessionario);
            
            StringBuilder validationErrors = new StringBuilder(StringUtils.defaultString(dto.getErroreValidazioneXml()));
            
            if (StringUtils.isBlank(dto.getIndirizzoIntestazione())) {
                validationErrors.append("Indirizzo Sede cliente mancante; ");
            }
            sedeCessionario.setIndirizzo(StringUtils.substring(StringUtils.defaultString(dto.getIndirizzoIntestazione(), "NON SPECIFICATO"), 0, 60));
            
            if (StringUtils.isBlank(dto.getCittaIntestazione())) {
                validationErrors.append("Comune Sede cliente mancante; ");
            }
            sedeCessionario.setComune(StringUtils.substring(StringUtils.defaultString(dto.getCittaIntestazione(), "NON SPECIFICATO"), 0, 60));
            
            if (StringUtils.isBlank(dto.getCapIntestazione())) {
                validationErrors.append("CAP Sede cliente mancante; ");
            }
            sedeCessionario.setCAP(StringUtils.defaultString(dto.getCapIntestazione(), "00000"));
            
            if (StringUtils.isBlank(dto.getProvinciaIntestazione())) {
                validationErrors.append("Provincia Sede cliente mancante; ");
                sedeCessionario.setProvincia("EE");
            } else {
                sedeCessionario.setProvincia(dto.getProvinciaIntestazione().toUpperCase());
            }
            
            sedeCessionario.setNazione(isoCliente);
            
            if (validationErrors.length() > 0) {
                dto.setErroreValidazioneXml(validationErrors.toString());
            }
            // FatturaElettronicaBody
            FatturaElettronicaBodyType body = new FatturaElettronicaBodyType();
            fatturaElettronica.getFatturaElettronicaBody().add(body);
            // DatiGenerali
            DatiGeneraliType datiGenerali = new DatiGeneraliType();
            body.setDatiGenerali(datiGenerali);
            // 2.1.1 DatiGeneraliDocumento
            DatiGeneraliDocumentoType datiGeneraliDocumento = new DatiGeneraliDocumentoType();
            datiGenerali.setDatiGeneraliDocumento(datiGeneraliDocumento);
            if ( dto instanceof FatturaDto )
            {
                FatturaDto fDto = (FatturaDto) dto;
                if ( fDto.getNumeroScontrino() != null && StringUtils.isNotEmpty(fDto.getDataScontrino()) )
                {
                    // Se c'è lo scontrino, è una fattura differita (TD24)
                    datiGeneraliDocumento.setTipoDocumento(TipoDocumentoEnum.FATTURA_DIFFERITA);
                }
                else if ( fDto.getTipoFattura() == TipoFattura.FATTURA || fDto.getTipoFattura() == TipoFattura.FATTURA_ACCOMPAGNATORIA )
                {
                    datiGeneraliDocumento.setTipoDocumento(TipoDocumentoEnum.FATTURA);
                }
                else
                {
                    datiGeneraliDocumento.setTipoDocumento(TipoDocumentoEnum.NOTA_DEBITO);
                }
            }
            else
            {
                datiGeneraliDocumento.setTipoDocumento(TipoDocumentoEnum.NOTA_CREDITO);
            }
            // datiGeneraliDocumento.setTipoDocumento(dto instanceof FatturaDto ? TipoDocumentoEnum.FATTURA : TipoDocumentoEnum.NOTA_CREDITO); // TODO gestire le altre casistiche previste
            datiGeneraliDocumento.setDivisa("EUR");
            datiGeneraliDocumento.setData(FastDateFormat.getInstance("dd/MM/yyyy").parse(dto.getDataDocumento()));
            String numDocumento = dto.getNumDocumento().toString();
            if ( StringUtils.isNotEmpty(dto.getParticella()) )
            {
                if ( dto.getParticella().startsWith(" ") || dto.getParticella().startsWith("/") || dto.getParticella().startsWith("\\") )
                {
                    numDocumento = numDocumento + dto.getParticella();
                }
                else
                {
                    numDocumento = numDocumento + " " + dto.getParticella();
                }
            }
            datiGeneraliDocumento.setNumero(numDocumento);
            if ( dto.getSplitPayment() == null || dto.getSplitPayment() == 0 )
            {
                datiGeneraliDocumento.setImportoTotaleDocumento(dto.getTotale());
            }
            if ( dto.getSplitPayment() != null && dto.getSplitPayment() == 1 )
            {
                // In caso di split payment, il totale dovuto caricato è solo l'imponibile, aggiungiamo l'IVA per il totale documento XML
                datiGeneraliDocumento.setImportoTotaleDocumento(dto.getTotale() + dto.getTotaleIva());
            }
            else
            {
                // Altrimenti il totale caricato è già il lordo completo
                datiGeneraliDocumento.setImportoTotaleDocumento(dto.getTotale());
            }
            if ( StringUtils.isNotBlank(dto.getSconto()) && !dto.getSconto().equalsIgnoreCase("NaN"))
            {
                ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
                scontoMaggiorazione.setTipo(TipoScontoDocumentoEnum.SCONTO);
                if ( dto.getSconto().endsWith("%") )
                {
                    String val = dto.getSconto().replaceAll("%", "").trim();
                    if (NumberUtils.isCreatable(val)) {
                       scontoMaggiorazione.setPercentuale(Double.parseDouble(val));
                    }
                }
                else
                {
                    if (NumberUtils.isCreatable(dto.getSconto())) {
                        scontoMaggiorazione.setImporto(Double.parseDouble(dto.getSconto()));
                        datiGeneraliDocumento.setImportoTotaleDocumento(datiGeneraliDocumento.getImportoTotaleDocumento() - scontoMaggiorazione.getImporto());
                    }
                }

                if (scontoMaggiorazione.getPercentuale() != null || scontoMaggiorazione.getImporto() != null) {
                    datiGeneraliDocumento.getScontoMaggiorazione().add(scontoMaggiorazione);
                }
            }
            // 2.1.2 DatiOrdineAcquisto
            if ( StringUtils.isNotEmpty(dto.getNumeroOrdineAcquisto()) )
            {
                DatiDocumentiCorrelatiType datiOrdineAcquisto = new DatiDocumentiCorrelatiType();
                datiGenerali.getDatiOrdineAcquisto().add(datiOrdineAcquisto);
                datiOrdineAcquisto.setIdDocumento(dto.getNumeroOrdineAcquisto());
                if ( StringUtils.isNotBlank(dto.getDataOrdineAcquisto()) )
                {
                    datiOrdineAcquisto.setData(FastDateFormat.getInstance("dd/MM/yyyy").parse(dto.getDataOrdineAcquisto()));
                }
                datiOrdineAcquisto.setCodiceCommessaConvenzione(StringUtils.defaultIfBlank(dto.getDatiCommessa(), null));
                datiOrdineAcquisto.setCodiceCIG(StringUtils.defaultIfBlank(dto.getCig(), null));
                datiOrdineAcquisto.setCodiceCUP(StringUtils.defaultIfBlank(dto.getCup(), null));
            }
            if ( dto.getFlRitenutaAcconto() != null && dto.getFlRitenutaAcconto().intValue() == 1 )
            {
                DatiRitenutaType datiRitenuta = new DatiRitenutaType();
                datiRitenuta.setTipoRitenuta(TipoRitenutaEnum.valueOf(dto.getTipoRitenuta()));
                datiRitenuta.setImportoRitenuta(dto.getImportoRitenutaAcconto() != null ? dto.getImportoRitenutaAcconto().doubleValue() : 0.0);
                datiRitenuta.setAliquotaRitenuta(dto.getPercRitenutaAcconto());
                datiRitenuta.setCausalePagamento(CausalePagamentoEnum.A); // Default value
                datiGeneraliDocumento.setDatiRitenuta(datiRitenuta);
            }
            if ( StringUtils.isNotBlank(dto.getCausale()) )
            {
                datiGeneraliDocumento.getCausale().add(dto.getCausale());
            }
            // Gestione Scontrino
            if ( dto instanceof FatturaDto )
            {
                FatturaDto fDto = (FatturaDto) dto;
                if ( fDto.getNumeroScontrino() != null && StringUtils.isNotEmpty(fDto.getDataScontrino()) )
                {
                    datiGeneraliDocumento.getCausale().add(new StringBuilder("RIFERIMENTO SCONTRINO N. ").append(fDto.getNumeroScontrino()).append(" DEL ").append(fDto.getDataScontrino()).toString());
                }
            }
            // 2.1.2 DatiFattureCollegate
            if ( datiGeneraliDocumento.getTipoDocumento() == TipoDocumentoEnum.NOTA_DEBITO && ((FatturaDto) dto).getIdFatturaCollegata() != 0L )
            {
                FattureDao fattureDao = new FattureDao(jdbcTemplate);
                FatturaDto fatturaCollegataDto = fattureDao.getById(((FatturaDto) dto).getIdFatturaCollegata());
                if ( fatturaCollegataDto != null && StringUtils.isNotEmpty(fatturaCollegataDto.getNumeroOrdineAcquisto()) )
                {
                    DatiDocumentiCorrelatiType datiFatturaCollegata = new DatiDocumentiCorrelatiType();
                    datiGenerali.getDatiFattureCollegate().add(datiFatturaCollegata);
                    datiFatturaCollegata.setIdDocumento(fatturaCollegataDto.getNumeroOrdineAcquisto());
                    if ( StringUtils.isNotBlank(fatturaCollegataDto.getDataOrdineAcquisto()) )
                    {
                        datiFatturaCollegata.setData(FastDateFormat.getInstance("dd/MM/yyyy").parse(fatturaCollegataDto.getDataOrdineAcquisto()));
                    }
                    datiFatturaCollegata.setCodiceCommessaConvenzione(StringUtils.defaultIfBlank(fatturaCollegataDto.getDatiCommessa(), null));
                    datiFatturaCollegata.setCodiceCIG(StringUtils.defaultIfBlank(fatturaCollegataDto.getCig(), null));
                    datiFatturaCollegata.setCodiceCUP(StringUtils.defaultIfBlank(fatturaCollegataDto.getCup(), null));
                }
            }
            else if ( datiGeneraliDocumento.getTipoDocumento() == TipoDocumentoEnum.NOTA_CREDITO && ((NotaCreditoDto) dto).getIdFattura() != null )
            {
                FattureDao fattureDao = new FattureDao(jdbcTemplate);
                FatturaDto fatturaCollegataDto = fattureDao.getById(((NotaCreditoDto) dto).getIdFattura());
                if ( fatturaCollegataDto != null && StringUtils.isNotEmpty(fatturaCollegataDto.getNumeroOrdineAcquisto()) )
                {
                    DatiDocumentiCorrelatiType datiFatturaCollegata = new DatiDocumentiCorrelatiType();
                    datiGenerali.getDatiFattureCollegate().add(datiFatturaCollegata);
                    datiFatturaCollegata.setIdDocumento(fatturaCollegataDto.getNumeroOrdineAcquisto());
                    if ( StringUtils.isNotBlank(fatturaCollegataDto.getDataOrdineAcquisto()) )
                    {
                        datiFatturaCollegata.setData(FastDateFormat.getInstance("dd/MM/yyyy").parse(fatturaCollegataDto.getDataOrdineAcquisto()));
                    }
                    datiFatturaCollegata.setCodiceCommessaConvenzione(StringUtils.defaultIfBlank(fatturaCollegataDto.getDatiCommessa(), null));
                    datiFatturaCollegata.setCodiceCIG(StringUtils.defaultIfBlank(fatturaCollegataDto.getCig(), null));
                    datiFatturaCollegata.setCodiceCUP(StringUtils.defaultIfBlank(fatturaCollegataDto.getCup(), null));
                }

            }
            // 2.2 DatiBeniServizi
            DatiBeniServiziType datiBeniServizi = new DatiBeniServiziType();
            body.setDatiBeniServizi(datiBeniServizi);
            // 2.2.1 DettaglioLinee
            int numLinea = 1;
            List<RiepilogoIvaDto> riepilogoIva = new ArrayList<RiepilogoIvaDto>();
            for ( int i = 0; i < dto.getProdotti().size(); i++ )
            {
                ProdottoDocumentoDto pdDto = dto.getProdotti().get(i);

                // Se fmDescrizione è valorizzato, forziamo fuoriMagazzino a true per garantire il corretto branch XML
                if (StringUtils.isNotBlank(pdDto.getFmDescrizione())) {
                    pdDto.setFuoriMagazzino(true);
                    pdDto.setProdotto(true);
                }

                RiepilogoIvaDto riDto = new RiepilogoIvaDto();
                riDto.setIdAliquotaIva(pdDto.getIdAliquotaIva());
                riDto.setFlRitenuta(pdDto.getFlRitenuta());
                DettaglioLineeType dettaglioLinea = new DettaglioLineeType();
                datiBeniServizi.getDettaglioLinee().add(dettaglioLinea);
                dettaglioLinea.setNumeroLinea(numLinea++);
                
                if ( pdDto.isProdotto() )
                {
                    if ( !pdDto.isFuoriMagazzino() )
                    {
                        dettaglioLinea.setDescrizione(StringUtils.defaultIfBlank(pdDto.getCodiceProdotto() + " - " + pdDto.getDescProdotto(), "Articolo"));
                    }
                    else
                    {
                        dettaglioLinea.setDescrizione(StringUtils.defaultIfBlank(pdDto.getFmDescrizione(), "Articolo"));
                    }
                    dettaglioLinea.setQuantita(pdDto.getQuantita());
                    dettaglioLinea.setUnitaMisura(pdDto.getDescUnitaMisura());
                    dettaglioLinea.setPrezzoUnitario(pdDto.getPrezzo());
                    if ( StringUtils.isNotBlank(pdDto.getSconto()) )
                    {
                        if ( pdDto.getSconto().contains("+") )
                        {
                            String[] sconti = pdDto.getSconto().split("\\+");
                            // sicuramente sconto percentuale
                            for ( int j = 0; j < sconti.length; j++ )
                            {
                                ScontoMaggiorazioneType sconto = new ScontoMaggiorazioneType();
                                sconto.setTipo(TipoScontoDocumentoEnum.SCONTO);
                                sconto.setPercentuale(Double.parseDouble(sconti[j].trim().replaceAll("%", "")));
                                dettaglioLinea.getScontoMaggiorazione().add(sconto);
                            }
                        }
                        else
                        {
                            ScontoMaggiorazioneType sconto = new ScontoMaggiorazioneType();
                            sconto.setTipo(TipoScontoDocumentoEnum.SCONTO);
                            if ( pdDto.getSconto().contains("%") )
                            {
                                sconto.setPercentuale(Double.parseDouble(pdDto.getSconto().trim().replaceAll("%", "")));
                            }
                            else
                            {
                                sconto.setImporto(Double.parseDouble(pdDto.getSconto().trim()));
                            }
                            dettaglioLinea.getScontoMaggiorazione().add(sconto);
                        }
                    }
                    // Ricalcoliamo il totale se quello caricato dal DB dovesse essere 0 (caso prodotti fuori magazzino FastOrder)
                    Double totaleSenzaIva = pdDto.getTotaleSenzaIva();
                    if ((totaleSenzaIva == null || totaleSenzaIva == 0.0) && pdDto.getPrezzo() != null && pdDto.getQuantita() != null && pdDto.getPrezzo() > 0) {
                        totaleSenzaIva = pdDto.getPrezzo() * pdDto.getQuantita();
                        // Se c'è uno sconto, andrebbe sottratto qui per SDI 2.2.1.11, ma per ora manteniamo la compatibilità 
                        // con il prezzo unitario caricato. In caso di FastOrder lo sconto riga è solitamente assente.
                    }
                    dettaglioLinea.setPrezzoTotale(totaleSenzaIva);
                    AliquotaIvaDto aiDto = aiDao.getById(pdDto.getIdAliquotaIva());
                    riDto.setAliquotaIva(aiDto.getImposta());
                    riDto.setTipologiaIva(aiDto.getClasse());
                    dettaglioLinea.setAliquotaIVA(aiDto.getImposta());
                    if ( aiDto.getImposta().equals(0d) )
                    {
                        dettaglioLinea.setNatura(NaturaEsenzioneEnum.valueOf(aiDto.getClasse()));
                    }
                    
                    if ( dto.getFlRitenutaAcconto() != null && dto.getFlRitenutaAcconto().intValue() == 1 )
                    {
                        if ( riDto.getFlRitenuta() == null || riDto.getFlRitenuta().intValue() == 1 )
                        {
                            dettaglioLinea.setRitenuta(true);
                        }
                    }
                    
                    if ( dto instanceof FatturaDto )
                    {
                        FatturaDto fDto = (FatturaDto) dto;
                        if ( fDto.getNumeroScontrino() != null && StringUtils.isNotEmpty(fDto.getDataScontrino()) )
                        {
                            it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.AltriDatiGestionaliType altriDatiGestionali = new it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.AltriDatiGestionaliType();
                            altriDatiGestionali.setTipoDato("SCONTRINO");
                            altriDatiGestionali.setRiferimentoTesto("" + fDto.getNumeroScontrino());
                            altriDatiGestionali.setRiferimentoData(FastDateFormat.getInstance("dd/MM/yyyy").parse(fDto.getDataScontrino()));
                            dettaglioLinea.getAltriDatiGestionali().add(altriDatiGestionali);
                        }
                    }

                    if ( riepilogoIva.contains(riDto) )
                    {
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setTotaleImponibile(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getTotaleImponibile() + totaleSenzaIva);
                    }
                    else
                    {
                        riepilogoIva.add(riDto);
                        riDto.setAliquotaIva(aiDto.getImposta());
                        riDto.setTotaleImponibile(totaleSenzaIva);
                    }
                }
                else
                {
                    dettaglioLinea.setDescrizione(StringUtils.defaultIfBlank(pdDto.getNota(), "Articolo"));
                    dettaglioLinea.setPrezzoUnitario(0d);
                    dettaglioLinea.setPrezzoTotale(0d);
                    dettaglioLinea.setAliquotaIVA(0d);
                }
            }
                // 2.1.1.9 DatiCassaPrevidenziale (Rivalsa INPS)
                if ( dto.getFlRivalsaInps() != null && dto.getFlRivalsaInps().intValue() == 1 )
                {
                    DatiCassaPrevidenzialeType datiCassa = new DatiCassaPrevidenzialeType();
                    datiCassa.setTipoCassa(SdiFormatter.parseTipoCassa(dto.getTipoCassaInps()));
                    datiCassa.setAlCassa(dto.getPercRivalsaInps());
                    datiCassa.setImportoContributoCassa(dto.getImportoRivalsaInps() != null ? dto.getImportoRivalsaInps().doubleValue() : 0.0);
                    
                    // L'imponibile cassa è calcolato in base alla percentuale definita (default 100%)
                    double percImponibile = dto.getPercImponibileRivalsa() != null ? dto.getPercImponibileRivalsa() : 100.0;
                    double imponibileCassaBase = 0;
                    for (ProdottoDocumentoDto pd : dto.getProdotti()) {
                        if (pd.isProdotto()) {
                            imponibileCassaBase += (pd.getTotaleSenzaIva() != null ? pd.getTotaleSenzaIva() : 0.0);
                        }
                    }
                    double imponibileCassa = BigDecimal.valueOf(imponibileCassaBase).multiply(BigDecimal.valueOf(percImponibile).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).doubleValue();
                    datiCassa.setImponibileCassa(imponibileCassa);
                    
                    // Aliquota IVA: usiamo quella specifica se impostata, altrimenti fallback su prima riga o 22%
                    double aliquotaIvaCassa = 22.0;
                    String naturaCassa = null;
                    Integer idIvaRivalsa = dto.getIdAliquotaIvaRivalsa();
                    
                    if (idIvaRivalsa != null && idIvaRivalsa > 0) {
                        AliquotaIvaDto aiDto = aiDao.getById(idIvaRivalsa);
                        if (aiDto != null) {
                            aliquotaIvaCassa = aiDto.getImposta();
                            naturaCassa = aiDto.getClasse();
                        }
                    } else if (!dto.getProdotti().isEmpty()) {
                        AliquotaIvaDto aiDto = aiDao.getById(dto.getProdotti().get(0).getIdAliquotaIva());
                        if (aiDto != null) {
                            aliquotaIvaCassa = aiDto.getImposta();
                            naturaCassa = aiDto.getClasse();
                        }
                    }
                    
                    datiCassa.setAliquotaIVA(aliquotaIvaCassa);
                    if (aliquotaIvaCassa == 0 && StringUtils.isNotBlank(naturaCassa)) {
                        datiCassa.setNatura(SdiFormatter.parseNaturaEsenzione(naturaCassa));
                    }
                    
                    if (dto.getFlRitenutaAcconto() != null && dto.getFlRitenutaAcconto().intValue() == 1) {
                        datiCassa.setRitenuta(true);
                    }
                    
                    datiGeneraliDocumento.getDatiCassaPrevidenziale().add(datiCassa);
                    
                    // Aggiorniamo il riepilogo IVA: la rivalsa INPS fa cumulo sull'imponibile IVA
                    RiepilogoIvaDto riDtoCassa = new RiepilogoIvaDto();
                    riDtoCassa.setAliquotaIva(aliquotaIvaCassa);
                    riDtoCassa.setTipologiaIva(naturaCassa);
                    
                    int index = riepilogoIva.indexOf(riDtoCassa);
                    if (index >= 0) {
                        riepilogoIva.get(index).setTotaleImponibile(riepilogoIva.get(index).getTotaleImponibile() + datiCassa.getImportoContributoCassa());
                    } else {
                        riDtoCassa.setTotaleImponibile(datiCassa.getImportoContributoCassa());
                        riepilogoIva.add(riDtoCassa);
                    }
                    
                    // Aggiorniamo il totale documento per includere la cassa (che non era nel totale items caricato dal DB)
                    double importoIvatoCassa = datiCassa.getImportoContributoCassa() * (1 + aliquotaIvaCassa / 100.0);
                    datiGeneraliDocumento.setImportoTotaleDocumento(datiGeneraliDocumento.getImportoTotaleDocumento() + importoIvatoCassa);
                }
            // 2.2.2 DatiRiepilogo
            for ( int i = 0; i < riepilogoIva.size(); i++ )
            {
                RiepilogoIvaDto riepilogoDto = riepilogoIva.get(i);
                DatiRiepilogoType datiRiepilogo = new DatiRiepilogoType();
                datiBeniServizi.getDatiRiepilogo().add(datiRiepilogo);
                datiRiepilogo.setAliquotaIVA(riepilogoDto.getAliquotaIva());
                if ( riepilogoDto.getAliquotaIva().equals(0d) )
                {
                    datiRiepilogo.setNatura(NaturaEsenzioneEnum.valueOf(riepilogoDto.getTipologiaIva()));
                }
                datiRiepilogo.setImponibileImporto(riepilogoDto.getTotaleImponibile());
                datiRiepilogo.setImposta(BigDecimal.valueOf(riepilogoDto.getAliquotaIva()).multiply(BigDecimal.valueOf(riepilogoDto.getTotaleImponibile())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                // datiRiepilogo.setImposta(riepilogoDto.getImportoIva());
                if ( dto.getEsigibilitaDifferita() != null && dto.getEsigibilitaDifferita() == 1 )
                {
                    datiRiepilogo.setEsigibilitaIVA(EsigibilitaIvaEnum.DIFFERITA);
                }
                else if ( dto.getSplitPayment() != null && dto.getSplitPayment() == 1 )
                {
                    datiRiepilogo.setEsigibilitaIVA(EsigibilitaIvaEnum.SCISSIONE);

                }
                else
                {
                    datiRiepilogo.setEsigibilitaIVA(EsigibilitaIvaEnum.IMMEDIATA);
                }
            }
            // 2.4 DatiPagamento

            DatiPagamentoType datiPagamento = new DatiPagamentoType();
            body.getDatiPagamento().add(datiPagamento);
            if ( dto.getListaScadenzePagamentiDocumento().size() == 1 )
            {
                datiPagamento.setCondizioniPagamento(TipoPagamentoEnum.COMPLETO);
            }
            else
            {
                datiPagamento.setCondizioniPagamento(TipoPagamentoEnum.RATE);
            }
            BigDecimal totScadenze = BigDecimal.ZERO;
            for ( int i = 0; i < dto.getListaScadenzePagamentiDocumento().size(); i++ )
            {
                ScadenzaPagamentoDocumentoDto scadenzaDto = dto.getListaScadenzePagamentiDocumento().get(i);
                DettaglioPagamentoType dettaglioPagamento = new DettaglioPagamentoType();
                datiPagamento.getDettaglioPagamento().add(dettaglioPagamento);
                TipoPagamentoDto tpDto = tpDao.getById(dto.getIdTipoPagamento());
                ModalitaPagamentoEnum modalita = ModalitaPagamentoEnum.fromCodice(StringUtils.isNotBlank(scadenzaDto.getModalitaPagamento()) ? scadenzaDto.getModalitaPagamento() : tpDto.getModalita());
                dettaglioPagamento.setModalitaPagamento(modalita != null ? modalita : ModalitaPagamentoEnum.CONTANTI);
                dettaglioPagamento.setDataScadenzaPagamento(FastDateFormat.getInstance("dd/MM/yyyy").parse(scadenzaDto.getDtScadenza()));
                dettaglioPagamento.setImportoPagamento(scadenzaDto.getImporto());
                if ( tpDto != null && ModalitaPagamentoEnum.BONIFICO.equals(ModalitaPagamentoEnum.fromCodice(tpDto.getModalita())) && StringUtils.isNotBlank(dto.getIbanNsBanca()) )
                {
                    dettaglioPagamento.setIBAN(dto.getIbanNsBanca());
                }
                totScadenze = totScadenze.add(BigDecimal.valueOf(scadenzaDto.getImporto()));
            }
            if ( totScadenze.compareTo(BigDecimal.valueOf(dto.getTotale())) < 0 )
            {
                ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
                scontoMaggiorazione.setTipo(TipoScontoDocumentoEnum.SCONTO);

                scontoMaggiorazione.setImporto(BigDecimal.valueOf(datiGeneraliDocumento.getImportoTotaleDocumento()).subtract(totScadenze).doubleValue());
                datiGeneraliDocumento.setImportoTotaleDocumento(datiGeneraliDocumento.getImportoTotaleDocumento() - scontoMaggiorazione.getImporto());

                datiGeneraliDocumento.getScontoMaggiorazione().add(scontoMaggiorazione);
            }
            else if ( totScadenze.compareTo(BigDecimal.valueOf(datiGeneraliDocumento.getImportoTotaleDocumento())) > 0 )
            {
                ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
                scontoMaggiorazione.setTipo(TipoScontoDocumentoEnum.MAGGIORAZIONE);

                scontoMaggiorazione.setImporto(totScadenze.subtract(BigDecimal.valueOf(datiGeneraliDocumento.getImportoTotaleDocumento())).doubleValue());
                datiGeneraliDocumento.setImportoTotaleDocumento(datiGeneraliDocumento.getImportoTotaleDocumento() + scontoMaggiorazione.getImporto());

                datiGeneraliDocumento.getScontoMaggiorazione().add(scontoMaggiorazione);
            }
            BigDecimal totRiepilogo = BigDecimal.ZERO;
            for ( int i = 0; i < datiBeniServizi.getDatiRiepilogo().size(); i++ )
            {
                totRiepilogo = totRiepilogo.add(BigDecimal.valueOf(datiBeniServizi.getDatiRiepilogo().get(i).getImponibileImporto()).add(BigDecimal.valueOf(datiBeniServizi.getDatiRiepilogo().get(i).getImposta())));
            }
            BigDecimal totSconti = BigDecimal.ZERO;
            if ( datiGeneraliDocumento.getScontoMaggiorazione() != null )
            {
                for ( ScontoMaggiorazioneType scontoMaggiorazione : datiGeneraliDocumento.getScontoMaggiorazione() )
                {
                    BigDecimal importoDaAggiungere = BigDecimal.ZERO;
                    if ( scontoMaggiorazione.getImporto() != null )
                    {
                        importoDaAggiungere = BigDecimal.valueOf(scontoMaggiorazione.getImporto());
                    }
                    else if ( scontoMaggiorazione.getPercentuale() != null )
                    {
                        BigDecimal scontoPercentuale = totRiepilogo.multiply(BigDecimal.valueOf(scontoMaggiorazione.getPercentuale())).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                        importoDaAggiungere = scontoPercentuale;
                    }

                    if (TipoScontoDocumentoEnum.SCONTO.equals(scontoMaggiorazione.getTipo())) {
                        totSconti = totSconti.add(importoDaAggiungere);
                    } else {
                        totSconti = totSconti.subtract(importoDaAggiungere);
                    }
                }
            }
            BigDecimal splitPaymentAmount = (dto.getSplitPayment() == null || dto.getSplitPayment() == 0) ? BigDecimal.ZERO : BigDecimal.valueOf(dto.getTotaleIva());
            BigDecimal totDocumentoCalcolato = totRiepilogo.subtract(totSconti).subtract(splitPaymentAmount);
            
            if ( totScadenze.compareTo(totDocumentoCalcolato) != 0 )
            {
                BigDecimal arrotondamento = totScadenze.subtract(totDocumentoCalcolato);
                arrotondamento = arrotondamento.setScale(2, BigDecimal.ROUND_HALF_UP);
                
                if ( arrotondamento.abs().compareTo(BigDecimal.valueOf(0.05)) > 0 )
                {
                    _log.error("ERRORE CRITICO: Discrepanza totale documento superiore alla soglia tecnica (0.05€).");
                    _log.error("Fattura ID: {}", dto.getId());
                    _log.error("Totale Riepilogo (Lordo Linee): {}", totRiepilogo);
                    _log.error("Totale Sconti/Maggiorazioni: {}", totSconti);
                    _log.error("Split Payment: {}", splitPaymentAmount);
                    _log.error("Totale Scadenze (Rate): {}", totScadenze);
                    _log.error("Differenza (Arrotondamento richiesto): {}", arrotondamento);
                    throw new RuntimeException("Impossibile generare XML: discrepanza totale/rate troppo elevata (" + arrotondamento + "€). Verificare le rate e il totale documento.");
                }

                if ( arrotondamento.compareTo(BigDecimal.ZERO) != 0 )
                {
                    _log.info("Applico arrotondamento tecnico per quadratura centesimi.");
                    _log.info("Valore arrotondamento: {}", arrotondamento.toString());
                    datiGeneraliDocumento.setArrotondamento(arrotondamento.doubleValue());
                }
            }
            try
            {
                JAXBContext jaxbContext = JAXBContext.newInstance(FatturaElettronicaType.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                ObjectFactory objectFactory = new ObjectFactory();
                JAXBElement<FatturaElettronicaType> je = objectFactory.createFatturaElettronica(fatturaElettronica);
                JAXBSource source = new JAXBSource(jaxbContext, je);
                SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//                Resource resource = new ClassPathResource("xsd/sdi/v1_2/Schema_del_file_xml_FatturaPA_versione_1.2.xsd");
                Resource resource = new ClassPathResource("xsd/sdi/v1_2_1/Schema_del_file_xml_FatturaPA_versione_1.2.1.xsd");
                StringWriter sw = new StringWriter();
                try
                {
                    Source schemaSource = new StreamSource(resource.getInputStream());
                    Schema schema = sf.newSchema(schemaSource);
                    Validator validator = schema.newValidator();
                    validator.validate(source);
                    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    // jaxbMarshaller.marshal(je, System.out);
                    jaxbMarshaller.marshal(je, sw);
                    DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
                    String formattedDate = df.format(cal.getTime());
                    dto.setNomeFileFattura(new StringBuilder("IT").append(datiAziendaDto.getPartitaIva()).append("_").append(formattedDate + StringUtils.leftPad("" + dto.getNumDocumento(), 3, "0")).toString());
                    dto.setXmlFattura(sw.toString());
                    dto.setErroreValidazioneXml(null);
                    dto.setXmlNonValido(null);
                    // return dto;
                }
                catch ( SAXException | IOException e )
                {
                    jaxbMarshaller.marshal(je, sw);
                    dto.setErroreValidazioneXml(ExceptionUtils.getRootCause(e).getMessage());
                    dto.setXmlNonValido(sw.toString());
                    // _log.error("Validazione file fattura elettronica fallita", e);
                    // throw e;
                }
            }
            catch ( JAXBException e )
            {
                _log.error("Errore durante la preparazione del file xml della fattura elettronica", e);
                throw e;
            }
        }
        catch ( ParseException e )
        {
            _log.error("Errore nel parsing della data fattura", e);
            throw e;
        }
    }

    public long getIdByProgressivoFile(String progressivoFile) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        return dao.getIdByProgressivoFile(progressivoFile);
    }

    public List<NotificaFatturaDto> getNotificheJustDesign() throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        return dao.getNotificheJustDesign();
    }

    public List<NotificaFatturaDto> getNotificheGenericheSdi() throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        return dao.getNotificheGenericheSdi();
    }

    public List<NotificaFatturaDto> getNotificheFastOrder() throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        return dao.getNotificheFastOrder();
    }

    public synchronized String getProgressivoInvio() throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        String ultimoProgressivioInvio = dao.getProgressivoInvio();
        if ( StringUtils.isEmpty(ultimoProgressivioInvio) )
        {
            ultimoProgressivioInvio = "00001";
        }
        long l = Long.valueOf(ultimoProgressivioInvio, 36);
        l++;
        return StringUtils.leftPad(Long.toString(l, 36), 10, "0");
    }

    public synchronized String getProgressivoUnivocoFile() throws SQLException
    {
        ConfigurazioneDao dao = new ConfigurazioneDao(jdbcTemplate);
        String ultimoProgressivioUnivoco = dao.getByKey(ConfigurazioneDomain.FATTURA_ELETTRONICA.name(), ConfigurazioneKey.PROGRESSIVO_UNIVOCO_FILE.name());
        if ( StringUtils.isEmpty(ultimoProgressivioUnivoco) )
        {
            ultimoProgressivioUnivoco = "00001";
        }
        long l = Long.valueOf(ultimoProgressivioUnivoco, 36);
        l++;
        String str = StringUtils.leftPad(Long.toString(l, 36), 5, "0");
        if ( str.length() > 5 )
        {
            str = "00001";
        }
        ConfigurazioneDto dto = new ConfigurazioneDto();
        dto.setDominio(ConfigurazioneDomain.FATTURA_ELETTRONICA.name());
        dto.setChiave(ConfigurazioneKey.PROGRESSIVO_UNIVOCO_FILE.name());
        dto.setValore(str);
        dao.update(dto);
        return str;
    }

    public long getSupportiInviati() throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(jdbcTemplate);
        return dao.getSupportiInviati();
    }

    public void impostaFattureNotificate(List<Object[]> params) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        dao.impostaFattureNotificate(params);
    }

    public void impostaFattureNonNotificate(List<Object[]> params) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        dao.impostaFattureNonNotificate(params);
    }

    public void impostaInviataSdi(String dbKey,
                                  long idFattura) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        dao.impostaInviataSdi(dbKey, idFattura);
    }

    public void impostaNotaCreditoInviataSdi(String dbKey,
                                             long idFattura) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        dao.impostaNotaCreditoInviataSdi(dbKey, idFattura);
    }

    /**
     * Verifica l'inviabilità di una fattura allo SdI Una fattura è inviabile se non si trova nello stato IN oppure RC oppure se lo stato è null ma esiste un supporto inviato per cui non è stato ancora ricevuto l'esito di invio oppure è in stato OK
     * 
     * @param dbKey
     * @param idFattura
     * @return
     * @throws SQLException
     */
    public boolean isFatturaInviabile(String dbKey,
                                      long idFattura) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        return dao.isFatturaInviabile(dbKey, idFattura);
    }

    public void memorizzaEsitoSdi(String progressivoFile) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        dao.memorizzaEsitoSdi(progressivoFile);
    }

    public long memorizzaFatturaElettronica(String dbKey,
                                            FatturaElettronicaWrapperDto dto) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        return dao.memorizzaFatturaElettronica(dbKey, dto);
    }

    public void memorizzaInvioSdi(long idFatturaElettronica,
                                  String progressivoFile,
                                  long idSupporto) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        dao.memorizzaInvioSdi(idFatturaElettronica, progressivoFile, idSupporto);
    }

    public long memorizzaSupporto(File fileSupporto,
                                  int numeroFatture) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        return dao.memorizzaSupporto(fileSupporto, numeroFatture);
    }

    public void cancellaFatturaElettronicaCentrale(String dbKey, long idFattura) throws SQLException
    {
        FatturaElettronicaDao dao = new FatturaElettronicaDao(serviceJdbcTemplate);
        dao.cancellaFatturaElettronicaCentrale(dbKey, idFattura);
    }

    /**
     * Genera l'XML della Fattura Semplificata (TD07) manualmente, 
     * dato che non disponiamo delle classi JAXB per lo schema FSM10.
     */
    private void getFatturaElettronicaSemplificata(FatturaElettronicaDto dto, String nomeStore) throws SQLException, ParseException {
        AliquoteIvaDao aiDao = new AliquoteIvaDao(jdbcTemplate);
        DatiAziendaDao datiAziendaDao = new DatiAziendaDao(jdbcTemplate);
        DatiAziendaDto datiAziendaDto = datiAziendaDao.getDatiAzienda();

        // 1. Calcolo Progressivo Invio
        Date dateFattura = FastDateFormat.getInstance("dd/MM/yyyy").parse(dto.getDataDocumento());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFattura);
        String progressivo = numerazioneFatturaElettronicaDelegate.getNumero(cal.get(Calendar.YEAR));

        // 2. Costruzione manuale XML FSM10
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<p:FatturaElettronicaSemplificata versione=\"FSM10\" ");
        xml.append("xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" ");
        xml.append("xmlns:p=\"http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0\" ");
        xml.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");

        // Header
        xml.append("  <FatturaElettronicaHeader>\n");
        xml.append("    <DatiTrasmissione>\n");
        xml.append("      <IdTrasmittente>\n");
        xml.append("        <IdPaese>IT</IdPaese>\n");
        xml.append("        <IdCodice>").append(datiAziendaDto.getPartitaIva()).append("</IdCodice>\n");
        xml.append("      </IdTrasmittente>\n");
        xml.append("      <ProgressivoInvio>").append(progressivo).append("</ProgressivoInvio>\n");
        xml.append("      <FormatoTrasmissione>FSM10</FormatoTrasmissione>\n");
        String codDest = StringUtils.defaultIfBlank(dto.getCodiceUfficioDestinazione(), "0000000").toUpperCase();
        xml.append("      <CodiceDestinatario>").append(codDest).append("</CodiceDestinatario>\n");
        if (StringUtils.isNotBlank(dto.getPec())) {
            xml.append("      <PECDestinatario>").append(dto.getPec()).append("</PECDestinatario>\n");
        }
        xml.append("    </DatiTrasmissione>\n");

        xml.append("    <CedentePrestatore>\n");
        xml.append("      <IdFiscaleIVA>\n");
        xml.append("        <IdPaese>IT</IdPaese>\n");
        xml.append("        <IdCodice>").append(datiAziendaDto.getPartitaIva()).append("</IdCodice>\n");
        xml.append("      </IdFiscaleIVA>\n");
        xml.append("      <Denominazione>").append(escapeXml(datiAziendaDto.getDenominazione())).append("</Denominazione>\n");
        xml.append("      <Sede>\n");
        xml.append("        <Indirizzo>").append(escapeXml(datiAziendaDto.getIndirizzo())).append("</Indirizzo>\n");
        xml.append("        <CAP>").append(datiAziendaDto.getCap()).append("</CAP>\n");
        xml.append("        <Comune>").append(escapeXml(datiAziendaDto.getCitta())).append("</Comune>\n");
        xml.append("        <Provincia>").append(datiAziendaDto.getProvincia()).append("</Provincia>\n");
        xml.append("        <Nazione>IT</Nazione>\n");
        xml.append("      </Sede>\n");
        xml.append("      <RegimeFiscale>RF01</RegimeFiscale>\n");
        xml.append("    </CedentePrestatore>\n");

        xml.append("    <CessionarioCommittente>\n");
        String pIva = StringUtils.defaultIfBlank(dto.getPartitaIva(), (dto.getClienteDto() != null ? dto.getClienteDto().getPartitaIva() : ""));
        String cf = StringUtils.defaultIfBlank(dto.getCodiceFiscale(), (dto.getClienteDto() != null ? dto.getClienteDto().getCodiceFiscale() : ""));
        String nazione = StringUtils.defaultIfBlank(dto.getNazioneIntestazione(), "Italia");
        String isoCliente = nazioniDao.getCodiceIsoByNome(nazione);
        if (StringUtils.isBlank(isoCliente)) {
            isoCliente = "IT";
        }

        xml.append("      <IdentificativiFiscali>\n");
        if (StringUtils.isNotBlank(pIva)) {
            xml.append("        <IdFiscaleIVA>\n");
            xml.append("          <IdPaese>").append(isoCliente).append("</IdPaese>\n");
            xml.append("          <IdCodice>").append(pIva).append("</IdCodice>\n");
            xml.append("        </IdFiscaleIVA>\n");
        }
        if (StringUtils.isNotBlank(cf)) {
            xml.append("        <CodiceFiscale>").append(cf.toUpperCase()).append("</CodiceFiscale>\n");
        }
        xml.append("      </IdentificativiFiscali>\n");
        xml.append("      <AltriDatiIdentificativi>\n");
        xml.append("        <Denominazione>").append(escapeXml(dto.getClienteDto().getDenominazione())).append("</Denominazione>\n");
        xml.append("        <Sede>\n");
        xml.append("          <Indirizzo>").append(escapeXml(StringUtils.defaultIfBlank(dto.getIndirizzoIntestazione(), "NON SPECIFICATO"))).append("</Indirizzo>\n");
        xml.append("          <CAP>").append(StringUtils.defaultIfBlank(dto.getCapIntestazione(), "00000")).append("</CAP>\n");
        xml.append("          <Comune>").append(escapeXml(StringUtils.defaultIfBlank(dto.getCittaIntestazione(), "NON SPECIFICATO"))).append("</Comune>\n");
        xml.append("          <Provincia>").append(StringUtils.defaultIfBlank(dto.getProvinciaIntestazione(), "EE").toUpperCase()).append("</Provincia>\n");
        xml.append("          <Nazione>").append(isoCliente).append("</Nazione>\n");
        xml.append("        </Sede>\n");
        xml.append("      </AltriDatiIdentificativi>\n");
        xml.append("    </CessionarioCommittente>\n");
        xml.append("  </FatturaElettronicaHeader>\n");

        // Body
        xml.append("  <FatturaElettronicaBody>\n");
        xml.append("    <DatiGenerali>\n");
        xml.append("      <DatiGeneraliDocumento>\n");
        xml.append("        <TipoDocumento>TD07</TipoDocumento>\n");
        xml.append("        <Divisa>EUR</Divisa>\n");
        xml.append("        <Data>").append(FastDateFormat.getInstance("yyyy-MM-dd").format(dateFattura)).append("</Data>\n");
        xml.append("        <Numero>").append(dto.getNumDocumento()).append("</Numero>\n");
        xml.append("      </DatiGeneraliDocumento>\n");
        xml.append("    </DatiGenerali>\n");

        for (ProdottoDocumentoDto p : dto.getProdotti()) {
            AliquotaIvaDto ai = aiDao.getById(p.getIdAliquotaIva());
            xml.append("    <DatiBeniServizi>\n");
            xml.append("      <Descrizione>").append(escapeXml(StringUtils.defaultIfBlank(p.getFmDescrizione(), p.getDescProdotto()))).append("</Descrizione>\n");
            xml.append("      <Importo>").append(String.format(java.util.Locale.US, "%.2f", p.getPrezzoImponibile())).append("</Importo>\n");
            xml.append("      <DatiIVA>\n");
            if (ai != null && ai.getImposta() != null && ai.getImposta().doubleValue() > 0) {
                xml.append("        <Aliquota>").append(String.format(java.util.Locale.US, "%.2f", ai.getImposta())).append("</Aliquota>\n");
            } else if (ai != null) {
                xml.append("        <Imposta>0.00</Imposta>\n");
                xml.append("        <Natura>").append(ai.getClasse()).append("</Natura>\n");
            }
            xml.append("      </DatiIVA>\n");
            xml.append("    </DatiBeniServizi>\n");
        }

        xml.append("  </FatturaElettronicaBody>\n");
        xml.append("</p:FatturaElettronicaSemplificata>");

        dto.setXmlFattura(xml.toString());
        dto.setErroreValidazioneXml(null);
        dto.setXmlNonValido(null);
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
    }

}

