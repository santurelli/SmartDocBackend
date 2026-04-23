package it.tinna.smartdoc.server.delegate.documenti;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import it.tinna.smartdoc.server.delegate.aliquoteiva.AliquoteIvaDelegate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.web.multipart.MultipartFile;
import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.*;
import it.tinna.smartdoc.shared.dto.clienti.TipologiaClienteFornitore;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.util.ArrayList;
import java.util.List;

import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.server.dao.agenti.AgentiDao;
import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.dao.aspettobeni.AspettoBeniDao;
import it.tinna.smartdoc.server.dao.causaliesigibilitadifferita.CausaliEsigibilitaDifferitaDao;
import it.tinna.smartdoc.server.dao.causalitrasporto.CausaliTrasportoDao;
import it.tinna.smartdoc.server.dao.clienti.ClientiDao;
import it.tinna.smartdoc.server.dao.configurazione.ConfigurazioneDao;
import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.server.dao.datiazienda.DatiAziendaDao;
import it.tinna.smartdoc.server.dao.divisioni.DivisioniDao;
import it.tinna.smartdoc.server.dao.documenti.DocumentiDao;
import it.tinna.smartdoc.server.dao.documenti.FattureDao;
import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.server.dao.prodotti.ProdottiDao;
import it.tinna.smartdoc.server.dao.progetti.ProgettiDao;
import it.tinna.smartdoc.server.dao.risorse.RisorseDao;
import it.tinna.smartdoc.server.dao.speseincasso.SpeseIncassoDao;
import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.server.dao.tipiporto.TipiPortoDao;
import it.tinna.smartdoc.server.dao.unitamisura.UnitaMisuraDao;
import it.tinna.smartdoc.server.dao.vettori.VettoriDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.server.util.IOUtility;
import it.tinna.smartdoc.server.util.NumberUtils;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.agenti.AgenteDto;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.FattureListResponse;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.StatoFatturaElettronica;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import it.tinna.smartdoc.shared.dto.risorse.RisorsaDto;
import it.tinna.smartdoc.shared.dto.speseincasso.SpesaIncassoDto;
import it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto;
import it.tinna.smartdoc.shared.dto.template.TemplateData;
import it.tinna.smartdoc.shared.dto.template.fatture.FatturaTemplate;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import it.tinna.smartdoc.server.util.ReportLoader;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service(value = "fattureDelegate")
public class FattureDelegate extends BaseDelegate
{

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    @Autowired
    private DatiAziendaDelegate        datiaziendaDelegate;

    @Autowired
    private ConfigurazioneDelegate     configurazioneDelegate;
    
    @Autowired
    private AliquoteIvaDelegate aliquoteIvaDelegate;

    // public FattureDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    /**
     * Genera il pdf relativo all'elenco delle fatture (viene usato per esportare la lista in pdf oppure per la stampa diretta)
     * 
     * @param dbKey
     * @param idFornitore
     * @param dtDocumentoFrom
     * @param dtDocumentoTo
     * @param numeroDocumento
     * @param dtRegistrazioneFrom
     * @param dtRegistrazioneTo
     * @param numeroRegistrazione
     * @param stato
     * @return
     * @throws Exception
     */
    public byte[] createListPdf(String dbKey,
                                String tipoDocumento,
                                Integer idCliente,
                                String dtFrom,
                                String dtTo,
                                Integer idAgente,
                                String stato,
                                String statoFatturaElettronica) throws Exception
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        List<MovimentiDocumentoDto> list = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        // dati azienda
        DatiAziendaDao daDao = new DatiAziendaDao(jdbcTemplate);
        DatiAziendaDto daDto = daDao.getDatiAzienda();
        if ( daDto != null )
        {
            if ( daDto.getByteLogo() != null )
            {
                params.put("logopath", new ByteArrayInputStream(daDto.getByteLogo()));
            }
        }
        byte[] bytes = null;
        ConfigurazioneDao configurazioneDao = new ConfigurazioneDao(jdbcTemplate);
        String baseDirTemplate = configurazioneDao.getByKey(ISharedConstants.CONFIGURAZIONE_DOMINIO_STAMPA, ISharedConstants.CONFIG_KEY_STAMPA_BASEDIR);
        if ( StringUtils.isEmpty(baseDirTemplate) )
        {
            _log.error("Il parametro basedir per i template è vuoto o nullo");
            throw new Exception("Il parametro basedir per i template è vuoto o nullo");
        }
        Template t = new Template("name", new StringReader(baseDirTemplate), new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
        Map<String, Object> model = new HashMap<>();
        model.put("DB_KEY", dbKey);
        String baseDir = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
        InputStream reportIs = null;
        try
        {
            reportIs = FileUtils.openInputStream(new File(new StringBuilder(baseDir).append(ISharedConstants.ELENCOFATTURE_TEMPLATE_NAME).toString()));
        }
        catch ( IOException e )
        {
            _log.error("Errore nell'apertura dello stream per il template dell'elenco fatture", e);
            throw e;
        }
        list = dao.getList(tipoDocumento, idCliente, dtFrom, dtTo, idAgente, stato, statoFatturaElettronica, null, null, 1, "asc", null);
        for ( MovimentiDocumentoDto mdDto : list )
        {
            if ( mdDto.getFlFatturaElettronica() == 1 )
            {
                if ( mdDto.getStatoFatturaElettronica() == StatoFatturaElettronica.NS || mdDto.getStatoFatturaElettronica() == StatoFatturaElettronica.DI )
                {
                    try
                    {
                        it.tinna.smartdoc.shared.dto.documenti.EsitoSdiDto esito = fatturaelettronicaDelegate.getEsitoInvioSdi(mdDto.getIdDocumento(), dbKey);
                        if ( mdDto.getStatoFatturaElettronica() == StatoFatturaElettronica.NS )
                        {
                            mdDto.setErroreConsegna(esito.getDescrizioneScarto());
                        }
                        else
                        {
                            mdDto.setErroreXml(esito.getErroreValidazioneXml());
                        }
                    }
                    catch ( Exception e )
                    {
                        _log.error("Errore nel recupero esito SDI per fattura {}", mdDto.getIdDocumento(), e);
                    }
                }
            }
        }
        try
        {
            bytes = JasperRunManager.runReportToPdf(reportIs, params, new JRBeanCollectionDataSource(list));
        }
        catch ( JRException e )
        {
            _log.error("Errore nella generazione dell'elenco fatture in formato pdf", e);
            throw e;
        }
        return bytes;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void delete(List<FatturaDto> lista) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        for ( FatturaDto dto : lista )
        {
            dao.delete(dto);
        }
    }

    public void deleteScadenzaPagamento(Integer id) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        dao.deleteScadenzaPagamento(id);
    }

    public DocumentoWrapperDto esportaFatturaPdf(String dbKey,
                                                 long id)
    {
        try
        {
            TemplateData td = new TemplateData();
            FatturaDto dto = this.getById(id);
            FatturaTemplate ft = new FatturaTemplate();
            ft.setAnnotazioneEstesa(dto.getAnnotazioneEstesa());
            Map<String, Object> params = new HashMap<>();
            params.put("annotazioni", dto.getAnnotazioneEstesa());
            td.setParameters(params);

            AliquoteIvaDao aiDao = new AliquoteIvaDao(jdbcTemplate);
            // DatiAziendaDao daDao = new DatiAziendaDao(jdbcTemplate);

            String outputName = new StringBuilder(IOUtility.getValidFilename(new StringBuilder("fattura_").append(dto.getNumDocumento()).append(StringUtils.isEmpty(dto.getParticella()) ? "" : dto.getParticella()).toString())).append(".pdf").toString();

            // //dati azienda
            DatiAziendaDto daDto = datiaziendaDelegate.getDatiAzienda();
            if ( daDto != null )
            {
                if ( daDto.getByteLogo() != null )
                {
                    params.put("logopath", new ByteArrayInputStream(daDto.getByteLogo()));
                }
            }
            params.put("datiazienda", daDto);

            params.put("documento", dto);
            if ( !StringUtils.isEmpty(dto.getDataDocumento()) )
            {
                String[] str = dto.getDataDocumento().split("/");
                if ( str.length >= 3 )
                {
                    params.put("anno", str[2]);
                }
            }

            ConfigurazioneDao confDao = new ConfigurazioneDao(jdbcTemplate);
            String tipoStore = confDao.getByKey(ISharedConstants.CONFIG_DOMAIN_GLOBAL, ISharedConstants.CONFIG_KEY_TIPOSTORE);

            double totaleMerce = 0;
            for ( ProdottoDocumentoDto pdDto : dto.getProdotti() )
            {
                if ( pdDto.isProdotto() || pdDto.isFuoriMagazzino() )
                {
                    pdDto.setQuantitaFormattata(NumberUtils.formatAsQuantity(pdDto.getQuantita()));
                    pdDto.setPercentualeIvaFormattata(pdDto.getPercentualeIva() == null ? "" : NumberUtils.formatAsPercentage(pdDto.getPercentualeIva()));
                    pdDto.setPrezzoFormattato(NumberUtils.formatAsCurrency(pdDto.getPrezzo()));
                    pdDto.setTotaleFormattato(NumberUtils.formatAsCurrency(pdDto.getTotaleSenzaIva()));
                    totaleMerce += pdDto.getTotaleSenzaIva();
                    if ( pdDto.isFuoriMagazzino() )
                    {
                        pdDto.setProdottoDto(new ProdottoDto());
                        pdDto.getProdottoDto().setCodice(StringUtils.isEmpty(pdDto.getFmCodice()) ? "" : pdDto.getFmCodice());
                        pdDto.getProdottoDto().setDescrizione(pdDto.getFmDescrizione());
                        // pdDto.setDescUnitaMisura(pdDto.getFmUnitaMisura());
                    }
                    pdDto.getProdottoDto().setDescrizioneDocumento(tipoStore);
                }
            }
            ft.setProdotti(dto.getProdotti());
            List<RiepilogoIvaDto> riepilogoIva = new ArrayList<>();
            for ( ProdottoDocumentoDto pdDto : dto.getProdotti() )
            {
                if ( pdDto.isProdotto() || pdDto.isFuoriMagazzino() )
                {
                    RiepilogoIvaDto riDto = new RiepilogoIvaDto();
                    riDto.setIdAliquotaIva(pdDto.getIdAliquotaIva() == null ? 0 : pdDto.getIdAliquotaIva());
                    AliquotaIvaDto aiDto = aiDao.getById(pdDto.getIdAliquotaIva());
                    if ( aiDto == null )
                    {
                        aiDto = new AliquotaIvaDto();
                        aiDto.setImposta(0.0);
                        aiDto.setCodice("N.D.");
                        aiDto.setDescrizione("Aliquota non definita");
                    }
                    if ( riepilogoIva.contains(riDto) )
                    {
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImponibileMerce(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImponibileMerce() + pdDto.getTotaleSenzaIva());
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImponibileMerceFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImponibileMerce()));
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setTotaleImponibile(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getTotaleImponibile() + pdDto.getTotaleSenzaIva());
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getTotaleImponibile()));
                        BigDecimal val = BigDecimal.valueOf(pdDto.getTotaleSenzaIva());
                        val = val.multiply(BigDecimal.valueOf(aiDto.getImposta()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                        val = val.setScale(2, RoundingMode.HALF_UP);
//                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImportoIva(BigDecimal.valueOf(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImportoIva()).add(val).doubleValue());
//                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImportoIvaFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImportoIva()));
                    }
                    else
                    {
                        riepilogoIva.add(riDto);
                        riDto.setAliquotaIva(aiDto.getImposta());
                        riDto.setAliquotaIvaFormattata(new StringBuilder(aiDto.getCodice()).append(" ").append(aiDto.getDescrizione()).toString());
                        riDto.setImponibileMerce(pdDto.getTotaleSenzaIva());
                        riDto.setImponibileMerceFormattato(NumberUtils.formatAsCurrency(riDto.getImponibileMerce()));
                        riDto.setTotaleImponibile(pdDto.getTotaleSenzaIva());
                        riDto.setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(riDto.getTotaleImponibile()));
//                        BigDecimal val = BigDecimal.valueOf(pdDto.getTotaleSenzaIva());
//                        val = val.multiply(BigDecimal.valueOf(aiDto.getImposta()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
//                        val = val.setScale(2, RoundingMode.HALF_UP);
//                        riDto.setImportoIva(val.doubleValue());
//                        riDto.setImportoIvaFormattato(NumberUtils.formatAsCurrency(riDto.getImportoIva()));
                    }
                }
            }

            List<ScadenzaPagamentoDocumentoDto> scadenze = new ArrayList<>();
            double totAcconto = 0;
            if ( dto.getListaScadenzePagamentiDocumento() != null )
            {
                for ( ScadenzaPagamentoDocumentoDto spDto : dto.getListaScadenzePagamentiDocumento() )
                {
                    if ( spDto.getAcconto() == null || spDto.getAcconto().intValue() == 0 )
                    {
                        scadenze.add(spDto);
                        spDto.setImporto(spDto.getImporto() + (spDto.getImportoSpeseIncasso() == null ? 0 : spDto.getImportoSpeseIncasso()));
                        spDto.setImportoFormattato(NumberUtils.formatAsCurrency(spDto.getImporto()));
                    }
                    else
                    {
                        totAcconto += spDto.getImporto() == null ? 0.0 : spDto.getImporto();
                    }
                }
            }
            ft.setScadenze(scadenze);
            for ( SpesaIncassoDocumentoDto siDto : dto.getListaSpeseIncassoFattura() )
            {
                siDto.setImportoFormattato(NumberUtils.formatAsCurrency(siDto.getImporto()));
            }
            TipiPagamentoDao tpDao = new TipiPagamentoDao(jdbcTemplate);
            TipoPagamentoDto tpDto = null;
            if ( dto.getIdTipoPagamento() != null )
            {
                tpDto = tpDao.getById(dto.getIdTipoPagamento());
                if ( tpDto != null )
                {
                    if ( tpDto.getIdSpeseIncasso() != null && tpDto.getIdSpeseIncasso().intValue() != 0 )
                    {
                        double importoSpesaIncassoScadenze = (scadenze != null && !scadenze.isEmpty()) ? scadenze.get(0).getImportoSpeseIncasso() * scadenze.size() : 0;
                        SpesaIncassoDocumentoDto spesaDto = new SpesaIncassoDocumentoDto();
                        spesaDto.setIdSpesaIncasso(tpDto.getIdSpeseIncasso());
                        boolean trovata = false;
                        for ( SpesaIncassoDocumentoDto siDto : dto.getListaSpeseIncassoFattura() )
                        {
                            if ( siDto.getIdSpesaIncasso().equals(tpDto.getIdSpeseIncasso()) )
                            {
                                siDto.setImporto(siDto.getImporto() + importoSpesaIncassoScadenze);
                                siDto.setImportoFormattato(NumberUtils.formatAsCurrency(siDto.getImporto()));
                                trovata = true;
                                break;
                            }
                            AliquotaIvaDto aiDto = aiDao.getById(siDto.getIdAliquotaIva());
                            if ( aiDto == null )
                            {
                                aiDto = new AliquotaIvaDto();
                                aiDto.setImpostaFormattata("");
                            }
                            else
                            {
                                siDto.setAliquotaIvaDto(aiDto);
                                aiDto.setImpostaFormattata(NumberUtils.formatAsPercentage(aiDto.getImposta()));
                            }
                        }
                        if ( !trovata )
                        {
                            SpeseIncassoDao siDao = new SpeseIncassoDao(jdbcTemplate);
                            SpesaIncassoDto siDto = siDao.getById(tpDto.getIdSpeseIncasso());
                            AliquotaIvaDto aiDto = aiDao.getById(siDto.getIdAliquotaIva());
                            spesaDto.setImporto(importoSpesaIncassoScadenze);
                            spesaDto.setImportoFormattato(NumberUtils.formatAsCurrency(spesaDto.getImporto()));
                            spesaDto.setIdAliquotaIva(siDto.getIdAliquotaIva());
                            spesaDto.setDescrizione(siDto.getDescrizione());
                            spesaDto.setPercIva(aiDto == null ? 0 : aiDto.getImposta());
                            if ( aiDto == null )
                            {
                                aiDto = new AliquotaIvaDto();
                                aiDto.setImpostaFormattata("");
                            }
                            else
                            {
                                aiDto.setImpostaFormattata(NumberUtils.formatAsPercentage(aiDto.getImposta()));
                            }
                            spesaDto.setAliquotaIvaDto(aiDto);
                            dto.getListaSpeseIncassoFattura().add(spesaDto);
                        }
                    }
                }
            }
            double totSpeseArt15 = 0;
            int progrSpesa = 1;
            double totTrasporto = 0;
            double totAltreSpese = 0;
            for ( SpesaIncassoDocumentoDto siDto : dto.getListaSpeseIncassoFattura() )
            {
                ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
                ProdottoDto pDto = new ProdottoDto();
                pdDto.setProdottoDto(pDto);
                dto.getProdotti().add(pdDto);
                pdDto.setProdotto(true);
                pDto.setCodice(new StringBuilder("/S").append(progrSpesa).toString());
                pDto.setDescrizione(siDto.getDescrizione());
                pdDto.setDescUnitaMisura("N");
                pdDto.setQuantitaFormattata("1");
                pdDto.setPrezzoFormattato(NumberUtils.formatAsCurrency(siDto.getImporto()));
                pdDto.setSconto("");
                pdDto.setTotaleFormattato(pdDto.getPrezzoFormattato());
                progrSpesa++;
                if ( siDto.getTrasporto() != null && siDto.getTrasporto().intValue() == 1 )
                {
                    totTrasporto += (siDto.getImporto() == null ? 0 : siDto.getImporto());
                }
                else
                {
                    totAltreSpese += (siDto.getImporto() == null ? 0 : siDto.getImporto());
                }
                if ( siDto.getPercIva().intValue() == 0 )
                {
                    pdDto.setPercentualeIvaFormattata("");
                    totSpeseArt15 += siDto.getImporto();
                }
                else
                {
                    RiepilogoIvaDto riDto = new RiepilogoIvaDto();
                    riDto.setIdAliquotaIva(siDto.getIdAliquotaIva() == null ? 0 : siDto.getIdAliquotaIva());
                    AliquotaIvaDto aiDto = aiDao.getById(siDto.getIdAliquotaIva());
                    if ( aiDto == null )
                    {
                        aiDto = new AliquotaIvaDto();
                        aiDto.setImposta(0.0);
                        aiDto.setCodice("N.D.");
                        aiDto.setDescrizione("Aliquota non definita");
                    }
                    pdDto.setPercentualeIvaFormattata(NumberUtils.formatAsPercentage(aiDto.getImposta()));
                    if ( riepilogoIva.contains(riDto) )
                    {
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImponibileSpese(siDto.getImporto());
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImponibileSpeseFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImponibileSpese()));
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setTotaleImponibile(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getTotaleImponibile() + siDto.getImporto());
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getTotaleImponibile()));
//                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImportoIva(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImportoIva() + (siDto.getImporto() * aiDto.getImposta() / 100));
//                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImportoIvaFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImportoIva()));
                    }
                    else
                    {
                        riepilogoIva.add(riDto);
                        riDto.setAliquotaIva(aiDto.getImposta());
                        riDto.setAliquotaIvaFormattata(new StringBuilder(aiDto.getCodice()).append(" ").append(aiDto.getDescrizione()).toString());
                        riDto.setImponibileSpese(siDto.getImporto());
                        riDto.setImponibileSpeseFormattato(NumberUtils.formatAsCurrency(riDto.getImponibileSpese()));
                        riDto.setTotaleImponibile(siDto.getImporto());
                        riDto.setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(riDto.getTotaleImponibile()));
//                        riDto.setImportoIva(siDto.getImporto() * aiDto.getImposta() / 100);
//                        riDto.setImportoIvaFormattato(NumberUtils.formatAsCurrency(riDto.getImportoIva()));
                    }
                }
            }
            // verifico se la fattura è ad esigibilità differita e, se lo è,
            // inserisco una riga con l'indicazione e l'eventuale motivo
            if ( dto.getEsigibilitaDifferita() != null && dto.getEsigibilitaDifferita().intValue() == 1 )
            {
                ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
                ProdottoDto pDto = new ProdottoDto();
                pdDto.setProdottoDto(pDto);
                dto.getProdotti().add(pdDto);
                pdDto.setProdotto(false);
                pdDto.setNota("");
                //
                pdDto = new ProdottoDocumentoDto();
                pDto = new ProdottoDto();
                pdDto.setProdottoDto(pDto);
                dto.getProdotti().add(pdDto);
                pdDto.setProdotto(false);
                StringBuilder sb = new StringBuilder();
                sb.append("IVA AD ESIGIBILITA' DIFFERITA");
                if ( !dto.getDescCausaleEsigibilitaDifferita().equals("") )
                {
                    sb.append(" - ").append(dto.getDescCausaleEsigibilitaDifferita());
                }
                pdDto.setNota(sb.toString());
            }
            // verifico se la fattura è con split payment e, se lo è, inserisco
            // una riga con l'indicazione
            if ( dto.getSplitPayment() != null && dto.getSplitPayment().intValue() == 1 )
            {
                ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
                ProdottoDto pDto = new ProdottoDto();
                pdDto.setProdottoDto(pDto);
                dto.getProdotti().add(pdDto);
                pdDto.setProdotto(false);
                pdDto.setNota("");
                //
                pdDto = new ProdottoDocumentoDto();
                pDto = new ProdottoDto();
                pdDto.setProdottoDto(pDto);
                dto.getProdotti().add(pdDto);
                pdDto.setProdotto(false);
                pdDto.setNota("Operazione assoggettata a split payment con IVA non incassata dal cedente ai sensi dell' art.17-ter DPR 633/1972 e successive modifiche");
            }
            // riferimento scontrino (se esiste)
            if ( dto.getNumeroScontrino() != null && StringUtils.isNotEmpty(dto.getDataScontrino()) )
            {
                ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
                ProdottoDto pDto = new ProdottoDto();
                pdDto.setProdottoDto(pDto);
                dto.getProdotti().add(pdDto);
                pdDto.setProdotto(false);
                pdDto.setNota(new StringBuilder("RIFERIMENTO SCONTRINO N. ").append(dto.getNumeroScontrino()).append(" DEL ").append(dto.getDataScontrino()).toString());
            }
            // se la fattura è elettronica e la causale è compilata allora la faccio comparire nella stampa della fattura
            if ( StringUtils.isNotEmpty(dto.getCausale()) )
            {
                ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
                ProdottoDto pDto = new ProdottoDto();
                pdDto.setProdottoDto(pDto);
                dto.getProdotti().add(pdDto);
                pdDto.setProdotto(false);
                pdDto.setNota("");
                pdDto = new ProdottoDocumentoDto();
                pDto = new ProdottoDto();
                pdDto.setProdottoDto(pDto);
                dto.getProdotti().add(pdDto);
                pdDto.setProdotto(false);
                pdDto.setNota(dto.getCausale());
            }
            // context.put("riepilogoiva", riepilogoIva);
            // params.put("riepilogoiva", riepilogoIva);
            // Aggiunta Rivalsa INPS al riepilogo IVA se presente
            double importoRivalsa = 0;
            if (dto.getImportoRivalsaInps() != null) {
                importoRivalsa = dto.getImportoRivalsaInps().doubleValue();
            }
            double ivaRivalsaCalculated = 0;
            if (importoRivalsa > 0) {
                double impostaRivalsa = 22.0;
                Integer idIvaRivalsa = dto.getIdAliquotaIvaRivalsa();
                
                if (idIvaRivalsa != null && idIvaRivalsa > 0) {
                    AliquotaIvaDto ai = aliquoteIvaDelegate.getById(idIvaRivalsa);
                    if (ai != null) impostaRivalsa = ai.getImposta();
                } else if (dto.getProdotti() != null && !dto.getProdotti().isEmpty()) {
                    AliquotaIvaDto ai = aliquoteIvaDelegate.getById(dto.getProdotti().get(0).getIdAliquotaIva());
                    if (ai != null) impostaRivalsa = ai.getImposta();
                }
                
                ivaRivalsaCalculated = BigDecimal.valueOf(importoRivalsa).multiply(BigDecimal.valueOf(impostaRivalsa).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).doubleValue();

                boolean found = false;
                for (RiepilogoIvaDto riDto : riepilogoIva) {
                    if (riDto.getAliquotaIva() == impostaRivalsa) {
                        riDto.setTotaleImponibile(riDto.getTotaleImponibile() + importoRivalsa);
                        riDto.setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(riDto.getTotaleImponibile()));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    RiepilogoIvaDto riDto = new RiepilogoIvaDto();
                    riDto.setAliquotaIva(impostaRivalsa);
                    riDto.setAliquotaIvaFormattata(impostaRivalsa + "%");
                    riDto.setTotaleImponibile(importoRivalsa);
                    riDto.setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(importoRivalsa));
                    riepilogoIva.add(riDto);
                }
            }

            ft.setRiepilogoIva(riepilogoIva);
            BigDecimal totaleImponibile = new BigDecimal(0);
            BigDecimal totaleIva = new BigDecimal(0);
            for ( RiepilogoIvaDto riDto : riepilogoIva )
            {
                riDto.setImportoIva(BigDecimal.valueOf(riDto.getTotaleImponibile()).multiply(BigDecimal.valueOf(riDto.getAliquotaIva()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).doubleValue());
                riDto.setImportoIvaFormattato(NumberUtils.formatAsCurrency(riDto.getImportoIva()));
                totaleIva = totaleIva.add(BigDecimal.valueOf(riDto.getImportoIva()));
                totaleImponibile = totaleImponibile.add(BigDecimal.valueOf(riDto.getTotaleImponibile()));
            }

            totaleIva = totaleIva.setScale(2, BigDecimal.ROUND_HALF_UP);
            totaleImponibile = totaleImponibile.setScale(2, BigDecimal.ROUND_HALF_UP);
            // context.put("totalemerce",
            // NumberUtils.formatAsCurrency(totaleMerce));
            params.put("totalemerce", NumberUtils.formatAsCurrency(totaleMerce));
            // context.put("totaleimponibile",
            // NumberUtils.formatAsCurrency(totaleImponibile.doubleValue()));
            params.put("totaleimponibile", NumberUtils.formatAsCurrency(totaleImponibile.doubleValue()));
            // context.put("totaleiva",
            // NumberUtils.formatAsCurrency(totaleIva.doubleValue()));
            params.put("totaleiva", NumberUtils.formatAsCurrency(totaleIva.doubleValue()));
            // context.put("acconto", NumberUtils.formatAsCurrency(totAcconto));
            params.put("acconto", NumberUtils.formatAsCurrency(totAcconto));
            //
            // context.put("speseart15",
            // NumberUtils.formatAsCurrency(totSpeseArt15));
            params.put("speseart15", NumberUtils.formatAsCurrency(totSpeseArt15));
            // context.put("spesetrasporto",
            // NumberUtils.formatAsCurrency(totTrasporto));
            params.put("spesetrasporto", NumberUtils.formatAsCurrency(totTrasporto));
            // context.put("spesealtre",
            // NumberUtils.formatAsCurrency(totAltreSpese));
            params.put("spesealtre", NumberUtils.formatAsCurrency(totAltreSpese));
            
            // Parametri aggiuntivi per Rivalsa e Ritenuta
            double ritenuta = 0;
            if (dto.getImportoRitenutaAcconto() != null) {
                ritenuta = dto.getImportoRitenutaAcconto().doubleValue();
            }
            params.put("importoRivalsaInps", NumberUtils.formatAsCurrency(importoRivalsa));
            params.put("importoRitenutaAcconto", NumberUtils.formatAsCurrency(ritenuta));
            params.put("importoIvaRivalsa", NumberUtils.formatAsCurrency(ivaRivalsaCalculated));
            params.put("annotazioni", dto.getAnnotazioneEstesa());

            double grandTotal = totaleImponibile.doubleValue() + totaleIva.doubleValue() + totSpeseArt15;
            params.put("totalefattura", NumberUtils.formatAsCurrency(grandTotal));

            double nettoAPagare = grandTotal - totAcconto - ritenuta;
            if (dto.getSplitPayment() != null && dto.getSplitPayment() == 1) {
                nettoAPagare = nettoAPagare - totaleIva.doubleValue();
            }
            params.put("totalenetto", NumberUtils.formatAsCurrency(nettoAPagare));
            params.put("totale_escluso_iva", NumberUtils.formatAsCurrency(totaleImponibile.doubleValue() + totSpeseArt15 - totAcconto - ritenuta));
            String coordinate = "";
            if ( !StringUtils.isEmpty(dto.getModalitaPagamento()) )
            {
                ModalitaPagamentoEnum pagamentoEnum = ModalitaPagamentoEnum.fromCodice(dto.getModalitaPagamento());
                if ( pagamentoEnum == ModalitaPagamentoEnum.RIBA )
                {
                    if ( !StringUtils.isEmpty(dto.getDescrizioneBanca()) )
                    {
                        coordinate = dto.getDescrizioneBanca() + " - ";
                    }
                    if ( dto.getIban() != null && !dto.getIban().equals("") )
                    {
                        coordinate = coordinate + "IBAN " + dto.getIban();
                    }
                    else
                    {
                        if ( dto.getCin() != null && !dto.getCin().equals("") )
                        {
                            coordinate = coordinate + "CIN " + dto.getCin() + " ";
                        }
                        if ( dto.getAbi() != null && !dto.getAbi().equals("") )
                        {
                            coordinate = coordinate + "ABI " + dto.getAbi() + " ";
                        }
                        if ( dto.getCab() != null && !dto.getCab().equals("") )
                        {
                            coordinate = coordinate + "CAB " + dto.getCab() + " ";
                        }
                        if ( dto.getConto() != null && !dto.getConto().equals("") )
                        {
                            coordinate = coordinate + "CONTO " + dto.getConto() + " ";
                        }
                    }
                }
                else if ( pagamentoEnum == ModalitaPagamentoEnum.BONIFICO )
                {
                    if ( !StringUtils.isEmpty(dto.getDescrizioneNsBanca()) )
                    {
                        coordinate = dto.getDescrizioneNsBanca() + " - ";
                    }
                    if ( !StringUtils.isEmpty(dto.getIbanNsBanca()) )
                    {
                        coordinate = coordinate + "IBAN " + dto.getIbanNsBanca();
                    }
                    else
                    {
                        if ( !StringUtils.isEmpty(dto.getCinNsBanca()) )
                        {
                            coordinate = coordinate + "CIN " + dto.getCinNsBanca() + " ";
                        }
                        if ( !StringUtils.isEmpty(dto.getAbiNsBanca()) )
                        {
                            coordinate = coordinate + "ABI " + dto.getAbiNsBanca() + " ";
                        }
                        if ( !StringUtils.isEmpty(dto.getCabNsBanca()) )
                        {
                            coordinate = coordinate + "CAB " + dto.getCabNsBanca() + " ";
                        }
                        if ( !StringUtils.isEmpty(dto.getContoNsBanca()) )
                        {
                            coordinate = coordinate + "CONTO " + dto.getContoNsBanca() + " ";
                        }
                    }
                }
            }
            // context.put("coordinate", coordinate);
            params.put("coordinate", coordinate);
            JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(Arrays.asList(ft));
            td.setDataSource(beanColDataSource);
            ConfigurazioneDao configurazioneDao = new ConfigurazioneDao(jdbcTemplate);
            
            String stampaAgente = StringUtils.defaultIfEmpty(configurazioneDao.getByKey(ISharedConstants.CONFIGURAZIONE_DOMINIO_STAMPA, ISharedConstants.CONFIG_KEY_STAMPA_AGENTE), "0");
            td.getParameters().put("print_codagente", Boolean.valueOf("1".equals(stampaAgente) || "true".equalsIgnoreCase(stampaAgente)));
            
            // Load reports from classpath
            JasperReport report = ReportLoader.getReport("fattura.jrxml");
            JasperReport subreportScadenze = ReportLoader.getReport("fattura_scadenze.jrxml");
            
            td.getParameters().put("SUBREPORT_SCADENZE", subreportScadenze);

            byte[] bytes = null;
            if ( td.getDataSource() != null )
            {
                bytes = JasperRunManager.runReportToPdf(report, td.getParameters(), td.getDataSource());
            }
            else
            {
                bytes = JasperRunManager.runReportToPdf(report, td.getParameters(), new JREmptyDataSource());
            }
            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(bytes);
            result.setNome(outputName);
            return result;
        }
        catch ( Exception e )
        {
            _log.error("Errore nella generazione del pdf della fattura {}", id, e);
            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(new byte[0]);
            result.setNome("blank.pdf");
            return result;
        }
    }

    public FatturaDto getById(long id) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        ProdottiDao prodottiDao = new ProdottiDao(jdbcTemplate);
        FatturaDto dto = dao.getById(id);
        if ( dto != null )
        {
            ClienteDto clienteDto = clientiDao.getById(dto.getIdCliente());
            IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
            ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
            clienteDto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(), dto.getIdCliente()));
            clienteDto.setElencoContatti(contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(), dto.getIdCliente()));
            dto.setClienteDto(clienteDto);
            if ( dto.getIdProgetto() != null && dto.getIdProgetto() != 0 )
            {
                ProgettiDao progettiDao = new ProgettiDao(jdbcTemplate);
                dto.setProgettoDto(progettiDao.getById(dto.getIdProgetto().longValue()));
            }
            if ( dto.getIdAgente() != null && dto.getIdAgente() != 0 )
            {
                AgentiDao agentiDao = new AgentiDao(jdbcTemplate);
                dto.setAgenteDto(agentiDao.getById(dto.getIdAgente()));
            }
            List<ProdottoDocumentoDto> listProdottiDdt = dao.getListProdottiById(dto.getId());
            for ( ProdottoDocumentoDto pDto : listProdottiDdt )
            {
                if ( pDto.isProdotto() )
                {
                    pDto.setProdottoDto(prodottiDao.getById(pDto.getIdProdotto()));
                }
            }
            dto.setProdotti(listProdottiDdt);
            dto.setListaSpeseIncassoFattura(dao.getListSpeseIncassoById(dto.getId()));
            dto.setListaScadenzePagamentiDocumento(dao.getScadenzePagamento(dto.getId()));
            if ( dto.getFlFatturaElettronica() == 1 )
            {
                String dbKey = DatabaseContextHolder.getClientDatabase();
                it.tinna.smartdoc.shared.dto.documenti.EsitoSdiDto esito = fatturaelettronicaDelegate.getEsitoInvioSdi(dto.getId(), dbKey);
                if ( esito != null )
                {
                    dto.setErroreValidazioneXml(esito.getErroreValidazioneXml());
                    dto.setErroreConsegna(esito.getDescrizioneScarto());
                }
            }
        }
        return dto;
    }

    public List<FatturaDto> getByProgetto(long idProgetto,
                                          int length,
                                          int start,
                                          int orderColumn,
                                          String orderDir) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        List<FatturaDto> list = dao.getByProgetto(idProgetto, length, start, orderColumn, orderDir);
        for ( FatturaDto dto : list )
        {
            dto.setProdotti(dao.getListProdottiById(dto.getId()));
        }
        return list;
    }

    public Map<String, Object> getCombosMap(String tipoFattura) throws SQLException
    {
        AliquoteIvaDao aliquoteIvaDao = new AliquoteIvaDao(jdbcTemplate);
        UnitaMisuraDao unitaMisuraDao = new UnitaMisuraDao(jdbcTemplate);
        RisorseDao risorseDao = new RisorseDao(jdbcTemplate);
        TipiPagamentoDao tipiPagamentoDao = new TipiPagamentoDao(jdbcTemplate);
        CausaliEsigibilitaDifferitaDao causaliEsigibilitaDifferitaDao = new CausaliEsigibilitaDifferitaDao(jdbcTemplate);
        DivisioniDao divisioniDao = new DivisioniDao(jdbcTemplate);
        ConfigurazioneDao configurazioneDao = new ConfigurazioneDao(jdbcTemplate);

        Map<String, Object> map = new HashMap<>();
        map.put(ISharedConstants.COMBOSMAP_KEY_ALIQUOTEIVA, aliquoteIvaDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_UNITAMISURA, unitaMisuraDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_TIPIPAGAMENTO, tipiPagamentoDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_BANCHE, risorseDao.getListForCombo(RisorsaDto.Tipologia.BANCA.getValore()));
        map.put(ISharedConstants.COMBOSMAP_KEY_CAUSALIESIGIBILITADIFFERITA, causaliEsigibilitaDifferitaDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_DIVISIONI, divisioniDao.getListForCombo());
        String particelleAsString = configurazioneDao.getByKey(ISharedConstants.CONFIG_DOMAIN_DOCUMENTI, ISharedConstants.CONFIG_KEY_PARTICELLE);
        map.put(ISharedConstants.COMBOSMAP_KEY_PARTICELLE, StringUtils.split(particelleAsString, StringUtils.CR + StringUtils.LF));
        if ( TipoFattura.valueOf(tipoFattura) == TipoFattura.FATTURA_ACCOMPAGNATORIA )
        {
            CausaliTrasportoDao causaliTrasportoDao = new CausaliTrasportoDao(jdbcTemplate);
            TipiPortoDao tipiPortoDao = new TipiPortoDao(jdbcTemplate);
            VettoriDao vettoriDao = new VettoriDao(jdbcTemplate);
            AspettoBeniDao aspettoBeniDao = new AspettoBeniDao(jdbcTemplate);
            map.put(ISharedConstants.COMBOSMAP_KEY_CAUSALITRASPORTO, causaliTrasportoDao.getListForCombo());
            map.put(ISharedConstants.COMBOSMAP_KEY_TIPIPORTO, tipiPortoDao.getListForCombo());
            map.put(ISharedConstants.COMBOSMAP_KEY_VETTORI, vettoriDao.getListForCombo());
            map.put(ISharedConstants.COMBOSMAP_KEY_ASPETTOBENI, aspettoBeniDao.getListForCombo());
        }
        return map;
    }

    public FatturaElettronicaWrapperDto getFatturaElettronica(long idFattura)
    {
        FatturaElettronicaWrapperDto result = new FatturaElettronicaWrapperDto();
        FattureDao fattureDao = new FattureDao(jdbcTemplate);
        DatiAziendaDao datiAziendaDao = new DatiAziendaDao(jdbcTemplate);
        try
        {
            DatiAziendaDto datiAziendaDto = datiAziendaDao.getDatiAzienda();
            FatturaDto fatturaDto = this.getById(idFattura);
            result.setFattura(fatturaDto);
            String xmlFatturaElettronica = fattureDao.getXmlFatturaElettronica(idFattura);
            if ( StringUtils.isNotEmpty(xmlFatturaElettronica) )
            {
                Date dt = FastDateFormat.getInstance("dd/MM/yyyy").parse(result.getFattura().getDataDocumento());
                Calendar cal = Calendar.getInstance();
                cal.setTime(dt);
                DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
                String formattedDate = df.format(cal.getTime());
                result.getFattura().setNomeFileFattura(new StringBuilder("IT").append(datiAziendaDto.getPartitaIva()).append("_").append(formattedDate + StringUtils.leftPad("" + result.getFattura().getNumDocumento(), 3, "0")).toString());
                result.setFlussoFatturaElettronica(xmlFatturaElettronica.getBytes());
                fatturaDto.setXmlFattura(xmlFatturaElettronica);
                return result;
            }
            else
            {
                try
                {
                    fatturaelettronicaDelegate.getFatturaElettronica(fatturaDto, null);
                    if ( StringUtils.isNotBlank(fatturaDto.getErroreValidazioneXml()) )
                    {
                        result.setFlussoFatturaElettronica((fatturaDto.getErroreValidazioneXml() + StringUtils.CR + StringUtils.LF + fatturaDto.getXmlNonValido()).getBytes());
                        result.getFattura().setNomeFileFattura("fattura_" + result.getFattura().getNumDocumento());
                    }
                    else
                    {
                        result.setFlussoFatturaElettronica(fatturaDto.getXmlFattura().getBytes());
                    }
                    try
                    {
                        fattureDao.salvaXmlFatturaElettronica(idFattura, fatturaDto.getXmlFattura());
                    }
                    catch ( SQLException e )
                    {

                    }
                }
                catch ( Exception e )
                {
                    result.setFlussoFatturaElettronica(ExceptionUtils.getStackTrace(e).getBytes());
                    result.getFattura().setNomeFileFattura("fattura_" + result.getFattura().getNumDocumento());
                }
            }
        }
        catch ( SQLException | ParseException e )
        {
            // errore recupero fattura
            result.setFlussoFatturaElettronica(ExceptionUtils.getStackTrace(e).getBytes());
            result.getFattura().setNomeFileFattura("fattura_" + result.getFattura().getNumDocumento());
        }
        return result;
    }

    public List<MovimentiDocumentoDto> getFattureAssociabili(long idCliente,
                                                             String dataNotaDebito) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        return dao.getFattureAssociabili(idCliente, dataNotaDebito);
    }

    public List<Long> getFattureElettronicheDaInviare(long[] idFatture) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        return dao.getFattureElettronicheDaInviare(idFatture);
    }

    public List<MovimentiDocumentoDto> getListForExcel(String tipoDocumento,
                                                       Integer idCliente,
                                                       String dtFrom,
                                                       String dtTo,
                                                       Integer idAgente,
                                                       String stato,
                                                       String statoFatturaElettronica,
                                                       Integer length,
                                                       Integer start,
                                                       Integer orderColumn,
                                                       String orderDir,
                                                       String numDocumento) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        return dao.getList(tipoDocumento, idCliente, dtFrom, dtTo, idAgente, stato, statoFatturaElettronica, length, start, orderColumn, orderDir, numDocumento);
    }

    public FattureListResponse getList(String tipoDocumento,
                                       Integer idCliente,
                                       String dtFrom,
                                       String dtTo,
                                       Integer idAgente,
                                       String stato,
                                       String statoFatturaElettronica,
                                       Integer length,
                                       Integer start,
                                       Integer orderColumn,
                                       String orderDir,
                                       String numDocumento) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        FattureListResponse dto = new FattureListResponse();
        List<MovimentiDocumentoDto> list = dao.getList(tipoDocumento, idCliente, dtFrom, dtTo, idAgente, stato, statoFatturaElettronica, length, start, orderColumn, orderDir, numDocumento);
        BigDecimal totFatturato = BigDecimal.ZERO;
        BigDecimal totDaSaldare = BigDecimal.ZERO;
        BigDecimal totSaldato = BigDecimal.ZERO;
        String dbKey = DatabaseContextHolder.getClientDatabase();
        List<Long> idsElettroniche = new ArrayList<>();
        for ( MovimentiDocumentoDto mdDto : list )
        {
            if ( mdDto.getFlFatturaElettronica() == 1 && mdDto.getIdDocumento() != null )
            {
                idsElettroniche.add(mdDto.getIdDocumento().longValue());
            }
        }
        
        if (!idsElettroniche.isEmpty()) {
            List<it.tinna.smartdoc.shared.dto.documenti.EsitoSdiDto> esiti = fatturaelettronicaDelegate.getEsitiInvioSdi(idsElettroniche, dbKey);
            Map<Long, it.tinna.smartdoc.shared.dto.documenti.EsitoSdiDto> esitiMap = new HashMap<>();
            for (it.tinna.smartdoc.shared.dto.documenti.EsitoSdiDto e : esiti) {
                esitiMap.put(e.getIdFattura(), e);
            }
            for ( MovimentiDocumentoDto mdDto : list ) {
                if ( mdDto.getFlFatturaElettronica() == 1 && mdDto.getIdDocumento() != null ) {
                    it.tinna.smartdoc.shared.dto.documenti.EsitoSdiDto esito = esitiMap.get(mdDto.getIdDocumento().longValue());
                    if (esito != null) {
                        mdDto.setErroreXml(esito.getErroreValidazioneXml());
                        mdDto.setErroreConsegna(esito.getDescrizioneScarto());
                    }
                }
            }
        }

        for ( MovimentiDocumentoDto mdDto : list )
        {
            if ( mdDto.getFlFatturaElettronica() == 0 || (mdDto.getFlFatturaElettronica() == 1 && mdDto.getStatoFatturaElettronica() != null && mdDto.getStatoFatturaElettronica() == StatoFatturaElettronica.AC) )
            {
                totFatturato = totFatturato.add(BigDecimal.valueOf(mdDto.getTotale()));
                totDaSaldare = totDaSaldare.add(BigDecimal.valueOf(mdDto.getTotaleDaPagare()));
                totSaldato = totSaldato.add(BigDecimal.valueOf(mdDto.getTotalePagato()));
            }
        }
        dto.setTotalCount(list.isEmpty() ? 0l : (long) list.get(0).getTotal());
        dto.setTotalFiltered(list.isEmpty() ? 0l : (long) list.get(0).getTotal());
        dto.setTotFatturato(totFatturato.doubleValue());
        dto.setTotDaSaldare(totDaSaldare.doubleValue());
        dto.setTotSaldato(totSaldato.doubleValue());
        dto.setList(list);
        return dto;
    }

    public Integer getNextNumFattura(String data,
                                     int flFatturaElettronica,
                                     TipoFattura tipoFattura) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        if ( tipoFattura == TipoFattura.NOTA_DEBITO )
        {
            String value = StringUtils.defaultIfEmpty(configurazioneDelegate.getByKey(ISharedConstants.CONFIG_DOMAIN_DOCUMENTI, ISharedConstants.CONFIG_KEY_NUM_NOTE_DEBITO_FATTURE), "0");
            if ( value.equals("1") )
            {
                tipoFattura = TipoFattura.FATTURA;
            }
        }
        else if ( flFatturaElettronica == 1 )
        {
            // se sono qui vuol dire che il tipo documento non è di sicuro NOTA_DEBITO
            tipoFattura = TipoFattura.FATTURA;
        }
        return dao.getNextNum(data, flFatturaElettronica, tipoFattura);
    }

    public ScadenzaPagamentoDocumentoDto getScadenzaPagamento(Integer idScadenza) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        return dao.getScadenzaPagamento(idScadenza);
    }

    public List<MovimentiDocumentoDto> getUltimeFatture() throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        return dao.getUltimeFatture();
    }

    @Transactional(rollbackFor = Throwable.class)
    public long insert(FatturaDto dto) throws SQLException
    {
        gestisciAnnotazioniRivalsa(dto);
        if ( isExistentNumero(dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(), dto.getFlFatturaElettronica(), dto.getTipoFattura(), dto.getId()) )
        {
            throw new SQLException("Il numero di documento " + dto.getNumDocumento() + (StringUtils.isNotBlank(dto.getParticella()) ? "/" + dto.getParticella() : "") + " è già presente per l'anno di riferimento.");
        }
        FattureDao fattureDao = new FattureDao(jdbcTemplate);
        if ( (dto.getTipoFattura() == TipoFattura.FATTURA_PROFORMA || dto.getTipoFattura() == TipoFattura.FATTURA_ACCOMPAGNATORIA) && dto.getFlFatturaElettronica() == 1 )
        {
            dto.setTipoFattura(TipoFattura.FATTURA);
        }
        long idFattura = fattureDao.insert(dto);
        for ( ProdottoDocumentoDto prodottoDdtDto : dto.getProdotti() )
        {
            prodottoDdtDto.setIdDocumento(idFattura);
            fattureDao.insertProdotto(prodottoDdtDto);
        }
        if ( dto.getListaSpeseIncassoFattura() != null )
        {
            for ( SpesaIncassoDocumentoDto spesaIncassoDdtDto : dto.getListaSpeseIncassoFattura() )
            {
                spesaIncassoDdtDto.setIdFattura(idFattura);
                fattureDao.insertSpesaIncasso(spesaIncassoDdtDto);
            }
        }
        if ( dto.getListaScadenzePagamentiDocumento() != null )
        {
            for ( ScadenzaPagamentoDocumentoDto scadenzaPagamentoDocumentoDto : dto.getListaScadenzePagamentiDocumento() )
            {
                scadenzaPagamentoDocumentoDto.setIdDocumento(idFattura);
                fattureDao.insertScadenzaPagamento(scadenzaPagamentoDocumentoDto);
            }
        }
        if ( dto.getIdPreventivi() != null && !dto.getIdPreventivi().isEmpty() )
        {
            DocumentiDao documentiDao = new DocumentiDao(jdbcTemplate);
            for ( Integer idPreventivo : dto.getIdPreventivi() )
            {
                documentiDao.associaDoc(idFattura, ISharedConstants.TIPODOCASSOCIATO_FATTURA, idPreventivo, ISharedConstants.TIPODOCASSOCIATO_PREVENTIVO);
            }
        }
        else if ( dto.getIdDdt() != null && !dto.getIdDdt().isEmpty() )
        {
            DocumentiDao documentiDao = new DocumentiDao(jdbcTemplate);
            for ( Integer idDdt : dto.getIdDdt() )
            {
                documentiDao.associaDoc(idFattura, ISharedConstants.TIPODOCASSOCIATO_FATTURA, idDdt, ISharedConstants.TIPODOCASSOCIATO_DDT);
            }
        }
        double totale = fattureDao.getTotale(idFattura);
        double totalePagato = fattureDao.getTotalePagato(idFattura);
        fattureDao.aggiornaTotaliFattura(totale, totalePagato, idFattura);
        return idFattura;
    }

    public boolean isExistentNumero(Integer numeroDdt,
                                    String particella,
                                    String data,
                                    int flFatturaElettronica,
                                    TipoFattura tipoFattura,
                                    Long id) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        if ( tipoFattura == TipoFattura.NOTA_DEBITO )
        {
            String value = StringUtils.defaultIfEmpty(configurazioneDelegate.getByKey(ISharedConstants.CONFIG_DOMAIN_DOCUMENTI, ISharedConstants.CONFIG_KEY_NUM_NOTE_DEBITO_FATTURE), "0");
            if ( value.equals("1") )
            {
                tipoFattura = TipoFattura.FATTURA;
            }
        }
        return dao.isExistentNumero(numeroDdt, particella, data, flFatturaElettronica, tipoFattura, id);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void update(UtenteDto utenteDto,
                       FatturaDto dto) throws SQLException
    {
        gestisciAnnotazioniRivalsa(dto);
        if ( isExistentNumero(dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(), dto.getFlFatturaElettronica(), dto.getTipoFattura(), dto.getId()) )
        {
            throw new SQLException("Il numero di documento " + dto.getNumDocumento() + (StringUtils.isNotBlank(dto.getParticella()) ? "/" + dto.getParticella() : "") + " è già presente per l'anno di riferimento.");
        }
        FattureDao fattureDao = new FattureDao(jdbcTemplate);
        FatturaDto existentDto = fattureDao.getById(dto.getId());
        dto.setTipoFattura(existentDto.getTipoFattura());
        if ( dto.getFlFatturaElettronica() == 1 )
        {
            // recupero lo stato precedente della fattura elettronica
            if ( existentDto.getFlFatturaElettronica() != 1 )
            {
                if ( utenteDto.getFatturaElettronica() == 1 )
                {
                    dto.setStatoFatturaElettronica(StatoFatturaElettronica.DI);
                }
                if ( existentDto.getTipoFattura() == TipoFattura.FATTURA_PROFORMA )
                {
                    dto.setTipoFattura(TipoFattura.FATTURA);
                }
            }
            else
            {
                if ( utenteDto.getFatturaElettronica() == 1 )
                {
                    // Aggiorno lo stato solo se in DB è nullo (caso di migrazione o record incompleti)
                    if (existentDto.getStatoFatturaElettronica() == null) {
                        dto.setStatoFatturaElettronica(dto.getStatoFatturaElettronica());
                    } else {
                        dto.setStatoFatturaElettronica(existentDto.getStatoFatturaElettronica());
                    }
                }
            }
        }
        fattureDao.update(dto);

        // Se è una fattura elettronica, invalido l'XML salvato nel database centrale.
        // Questo forza la rigenerazione dell'XML corretto al prossimo invio se i dati sono stati modificati.
        if (dto.getFlFatturaElettronica() == 1) {
            try {
                String dbKey = DatabaseContextHolder.getClientDatabase();
                fatturaelettronicaDelegate.cancellaFatturaElettronicaCentrale(dbKey, dto.getId());
                _log.info("Invalidazione XML fattura elettronica centrale eseguita per ID: {}", dto.getId());
            } catch (Exception e) {
                _log.warn("Impossibile cancellare l'XML centrale per la fattura {}: {}", dto.getId(), e.getMessage());
            }
        }

        fattureDao.deleteProdottiById(dto.getId());
        for ( ProdottoDocumentoDto prodottoDdtDto : dto.getProdotti() )
        {
            prodottoDdtDto.setIdDocumento(dto.getId());
            fattureDao.insertProdotto(prodottoDdtDto);
        }
        fattureDao.deleteSpeseIncassoById(dto.getId());
        if ( dto.getListaSpeseIncassoFattura() != null )
        {
            for ( SpesaIncassoDocumentoDto spesaIncassoFatturaDto : dto.getListaSpeseIncassoFattura() )
            {
                spesaIncassoFatturaDto.setIdFattura(dto.getId());
                fattureDao.insertSpesaIncasso(spesaIncassoFatturaDto);
            }
        }
        fattureDao.deleteScadenzePagamento(dto.getId());
        if ( dto.getListaScadenzePagamentiDocumento() != null )
        {
            for ( ScadenzaPagamentoDocumentoDto scadenzaPagamentoDocumentoDto : dto.getListaScadenzePagamentiDocumento() )
            {
                scadenzaPagamentoDocumentoDto.setIdDocumento(dto.getId());
                fattureDao.insertScadenzaPagamento(scadenzaPagamentoDocumentoDto);
            }
        }
        double totale = fattureDao.getTotale(dto.getId());
        double totalePagato = fattureDao.getTotalePagato(dto.getId());
        fattureDao.aggiornaTotaliFattura(totale, totalePagato, dto.getId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateScadenzaPagamento(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        
        ScadenzaPagamentoDocumentoDto currentDto = dao.getScadenzaPagamento(dto.getId());
        if (currentDto == null) {
            throw new SQLException("Scadenza non trovata: " + dto.getId());
        }

        double currentImporto = (currentDto.getImporto() != null ? currentDto.getImporto() : 0);
        double currentSpese = (currentDto.getImportoSpeseIncasso() != null ? currentDto.getImportoSpeseIncasso() : 0);
        double currentTotal = currentImporto + currentSpese;

        double newImporto = (dto.getImporto() != null ? dto.getImporto() : 0);
        double newSpese = (dto.getImportoSpeseIncasso() != null ? dto.getImportoSpeseIncasso() : 0);
        double newTotal = newImporto + newSpese;

        if (dto.getSaldato() == 1 && newTotal < currentTotal && newTotal > 0) {
            // Pagamento parziale: aggiorna la scadenza attuale all'importo pagato e crea una nuova scadenza per il residuo
            dao.updateScadenzaPagamento(dto);
            
            ScadenzaPagamentoDocumentoDto residuoDto = new ScadenzaPagamentoDocumentoDto();
            residuoDto.setIdDocumento(currentDto.getIdDocumento());
            residuoDto.setDtScadenza(currentDto.getDtScadenza());
            // Il residuo viene calcolato sulla differenza totale
            residuoDto.setImporto(currentTotal - newTotal);
            residuoDto.setImportoSpeseIncasso(0.0); // Le spese si considerano pagate con la prima tranche
            residuoDto.setIvaSpeseIncasso(0.0);
            residuoDto.setIdRisorsa(currentDto.getIdRisorsa());
            residuoDto.setModalitaPagamento(currentDto.getModalitaPagamento());
            residuoDto.setSaldato(0);
            residuoDto.setAcconto(currentDto.getAcconto());
            residuoDto.setNote("Residuo da pagamento parziale di " + NumberUtils.formatAsCurrency(newTotal));
            
            dao.insertScadenzaPagamento(residuoDto);
        } else {
            dao.updateScadenzaPagamento(dto);
        }

        long idDocumento = currentDto.getIdDocumento();
        double totale = dao.getTotale(idDocumento);
        double totalePagato = dao.getTotalePagato(idDocumento);
        dao.aggiornaTotaliFattura(totale, totalePagato, idDocumento);
    }

    public void sendToSdi(long id) throws SQLException {
        FattureDao fattureDao = new FattureDao(jdbcTemplate);
        // Puliamo l'XML esistente per forzare la rigenerazione al prossimo passaggio del batch
        fattureDao.salvaXmlFatturaElettronica(id, null);
        
        // Imposta la fattura come "Da Inviare" (DI).
        fatturaelettronicaDelegate.aggiornaStatoFattura(id, StatoFatturaElettronica.DI);
    }

    /**
     * Importa una fattura elettronica SDI (XML)
     */
    public Long importXml(MultipartFile file) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(FatturaElettronicaType.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        
        FatturaElettronicaType xmlFattura;
        try (InputStream is = file.getInputStream()) {
            Object unmarshalled = unmarshaller.unmarshal(is);
            if (unmarshalled instanceof JAXBElement) {
                xmlFattura = ((JAXBElement<FatturaElettronicaType>) unmarshalled).getValue();
            } else {
                xmlFattura = (FatturaElettronicaType) unmarshalled;
            }
        }

        FattureDao dao = new FattureDao(jdbcTemplate);
        DatiAziendaDao datiAziendaDao = new DatiAziendaDao(jdbcTemplate);
        DatiAziendaDto datiAziendaDto = datiAziendaDao.getDatiAzienda();
        if (datiAziendaDto == null || StringUtils.isBlank(datiAziendaDto.getPartitaIva())) {
            throw new Exception("Dati Azienda non configurati. Per favore, inserisci la Partita IVA e i dati della tua azienda nelle impostazioni prima di importare fatture.");
        }

        // 1. Veridica Cedente (deve essere la nostra azienda)
        String pivaSdi = xmlFattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
        if (!datiAziendaDto.getPartitaIva().equals(pivaSdi)) {
            throw new Exception("L'azienda indicata come Cedente nel file XML (" + pivaSdi + ") non corrisponde alla nostra azienda.");
        }

        FatturaElettronicaBodyType body = xmlFattura.getFatturaElettronicaBody().get(0);
        DatiGeneraliDocumentoType datiGen = body.getDatiGenerali().getDatiGeneraliDocumento();
        
        String dataStr = FastDateFormat.getInstance("dd/MM/yyyy").format(datiGen.getData());

        // 2. Controllo Duplicati (Numero e Anno)
        String numSdiRaw = datiGen.getNumero();
        Integer numFattura = null;
        String particella = null;
        
        if (numSdiRaw.contains("/")) {
            String[] parts = numSdiRaw.split("/");
            particella = parts.length > 1 ? parts[1] : null;
            String onlyNums = parts[0].replaceAll("[^0-9]", "");
            if (StringUtils.isNotEmpty(onlyNums)) numFattura = Integer.parseInt(onlyNums);
        } else {
             String onlyNums = numSdiRaw.replaceAll("[^0-9]", "");
             if (StringUtils.isNotEmpty(onlyNums)) numFattura = Integer.parseInt(onlyNums);
             particella = numSdiRaw.replaceAll("[0-9]", "").trim();
             if (StringUtils.isEmpty(particella)) particella = null;
        }
        
        if (numFattura == null) numFattura = 0;

        TipoDocumentoEnum tipoSdi = datiGen.getTipoDocumento();
        TipoFattura tipoSmartDoc = TipoFattura.FATTURA;
        
        if (dao.isExistentNumero(numFattura, particella, dataStr, 1, tipoSmartDoc, null)) {
            throw new Exception("Fattura n. " + numSdiRaw + " del " + dataStr + " già esistente a sistema.");
        }

        // 3. Lookup/Creazione Cliente
        CessionarioCommittenteType cessionario = xmlFattura.getFatturaElettronicaHeader().getCessionarioCommittente();
        DatiAnagraficiCessionarioType anagCess = cessionario.getDatiAnagrafici();
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        ClienteDto clienteDto = null;
        
        if (anagCess.getIdFiscaleIVA() != null) {
            clienteDto = clientiDao.getByPartitaIva(anagCess.getIdFiscaleIVA().getIdCodice());
        }
        if (clienteDto == null && anagCess.getCodiceFiscale() != null) {
            clienteDto = clientiDao.getByCodiceFiscale(anagCess.getCodiceFiscale());
        }
        
        if (clienteDto == null) {
            clienteDto = new ClienteDto();
            clienteDto.setDenominazione(anagCess.getAnagrafica().getDenominazione() != null ? anagCess.getAnagrafica().getDenominazione() : (anagCess.getAnagrafica().getNome() + " " + anagCess.getAnagrafica().getCognome()));
            clienteDto.setPartitaIva(anagCess.getIdFiscaleIVA() != null ? anagCess.getIdFiscaleIVA().getIdCodice() : null);
            clienteDto.setCodiceFiscale(anagCess.getCodiceFiscale());
            clienteDto.setTipologia(TipologiaClienteFornitore.PRIVATO);
            clienteDto.setCodice(clientiDao.generaCodice());
            clienteDto.setUserCreated(0L);
            clienteDto.setId(clientiDao.insert(clienteDto));
            
            // Inserimento Indirizzo
            if (cessionario.getSede() != null) {
                IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
                IndirizzoDto indDto = new IndirizzoDto();
                indDto.setIdRichiedente(clienteDto.getId());
                indDto.setIndirizzo(cessionario.getSede().getIndirizzo());
                indDto.setCap(cessionario.getSede().getCAP());
                indDto.setCitta(cessionario.getSede().getComune());
                indDto.setProvincia(cessionario.getSede().getProvincia());
                indDto.setTipologia(IndirizzoDto.TipologiaIndirizzo.SEDE_OPERATIVA.getValore());
                indDto.setUserCreated(0L);
                indirizziDao.insert(IndirizzoDto.Richiedente.CLIENTI.getValore(), indDto);
            }
        }

        // 4. Mappatura DTO
        FatturaDto fDto = new FatturaDto();
        fDto.setNumDocumento(numFattura);
        fDto.setParticella(particella);
        fDto.setDataDocumento(dataStr);
        fDto.setIdCliente((int) clienteDto.getId());
        if (cessionario.getSede() != null) {
            fDto.setIndirizzoIntestazione(cessionario.getSede().getIndirizzo());
            fDto.setCapIntestazione(cessionario.getSede().getCAP());
            fDto.setCittaIntestazione(cessionario.getSede().getComune());
            fDto.setProvinciaIntestazione(cessionario.getSede().getProvincia());
        }
        fDto.setTipoFattura(tipoSmartDoc);
        fDto.setFlFatturaElettronica(1);
        fDto.setStatoFatturaElettronica(StatoFatturaElettronica.IN);
        fDto.setTotale(datiGen.getImportoTotaleDocumento());
        fDto.setSplitPayment(0);
        fDto.setUserCreated(0L);
        
        // Righe
        AliquoteIvaDao aiDao = new AliquoteIvaDao(jdbcTemplate);
        List<AliquotaIvaDto> aliquote = aiDao.getListForCombo();
        
        for (DettaglioLineeType linea : body.getDatiBeniServizi().getDettaglioLinee()) {
            ProdottoDocumentoDto p = new ProdottoDocumentoDto();
            p.setProdotto(true);
            p.setFuoriMagazzino(true);
            p.setFmDescrizione(linea.getDescrizione());
            p.setQuantita(linea.getQuantita() != null ? linea.getQuantita() : 1.0);
            p.setPrezzo(linea.getPrezzoUnitario());
            p.setPrezzoImponibile(linea.getPrezzoTotale());
            
            Double alSdi = linea.getAliquotaIVA();
            String naturaSdi = linea.getNatura() != null ? linea.getNatura().name() : null;
            
            AliquotaIvaDto match = aliquote.stream()
                .filter(a -> a.getImposta().equals(alSdi) && (naturaSdi == null || a.getClasse().equals(naturaSdi)))
                .findFirst().orElse(aliquote.get(0));
            
            p.setIdAliquotaIva((int) match.getId());
            fDto.getProdotti().add(p);
        }
        
        if (!body.getDatiPagamento().isEmpty()) {
            DatiPagamentoType pagSdi = body.getDatiPagamento().get(0);
            for (DettaglioPagamentoType dettPag : pagSdi.getDettaglioPagamento()) {
                ScadenzaPagamentoDocumentoDto s = new ScadenzaPagamentoDocumentoDto();
                s.setImporto(dettPag.getImportoPagamento());
                if (dettPag.getDataScadenzaPagamento() != null) {
                    s.setDtScadenza(FastDateFormat.getInstance("dd/MM/yyyy").format(dettPag.getDataScadenzaPagamento()));
                } else {
                    s.setDtScadenza(dataStr);
                }
                s.setModalitaPagamento(dettPag.getModalitaPagamento().name());
                s.setSaldato(0);
                fDto.getListaScadenzePagamentiDocumento().add(s);
            }
        }
        
        return this.insert(fDto);
    }

    private void gestisciAnnotazioniRivalsa(FatturaDto dto) {
        if (dto.getFlRivalsaInps() != null && dto.getFlRivalsaInps() == 1) {
            String testoRivalsa = "Contributo INPS 4% ai sensi dell'art. 1 comma 212 legge 662/96";
            if (StringUtils.isEmpty(dto.getCausale())) {
                dto.setCausale(testoRivalsa);
            } else if (!dto.getCausale().contains(testoRivalsa)) {
                dto.setCausale(dto.getCausale() + "\n" + testoRivalsa);
            }
        }
    }
}

