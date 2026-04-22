package it.tinna.smartdoc.server.delegate.documenti;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
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

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.util.ArrayList;
import java.util.List;

import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.server.dao.agenti.AgentiDao;
import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.dao.causaliesigibilitadifferita.CausaliEsigibilitaDifferitaDao;
import it.tinna.smartdoc.server.dao.clienti.ClientiDao;
import it.tinna.smartdoc.server.dao.configurazione.ConfigurazioneDao;
import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.server.dao.datiazienda.DatiAziendaDao;
import it.tinna.smartdoc.server.dao.divisioni.DivisioniDao;
import it.tinna.smartdoc.server.dao.documenti.FattureDao;
import it.tinna.smartdoc.server.dao.documenti.NoteCreditoDao;
import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.server.dao.prodotti.ProdottiDao;
import it.tinna.smartdoc.server.dao.progetti.ProgettiDao;
import it.tinna.smartdoc.server.dao.risorse.RisorseDao;
import it.tinna.smartdoc.server.dao.speseincasso.SpeseIncassoDao;
import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.server.dao.unitamisura.UnitaMisuraDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.server.util.IOUtility;
import it.tinna.smartdoc.server.util.NumberUtils;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.FattureListResponse;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.RiepilogoFattureDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.StatoFatturaElettronica;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import it.tinna.smartdoc.shared.dto.risorse.RisorsaDto;
import it.tinna.smartdoc.shared.dto.speseincasso.SpesaIncassoDto;
import it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto;
import it.tinna.smartdoc.shared.dto.template.TemplateData;
import it.tinna.smartdoc.shared.dto.template.notecredito.NotaCreditoTemplate;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import it.tinna.smartdoc.server.util.ReportLoader;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service(value = "notecreditoDelegate")
public class NoteCreditoDelegate extends BaseDelegate
{

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    //
    @Autowired
    private DatiAziendaDelegate        datiaziendaDelegate;

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
                                Integer idCliente,
                                String dtFrom,
                                String dtTo,
                                Integer idAgente,
                                String stato) throws Exception
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
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
            reportIs = FileUtils.openInputStream(new File(new StringBuilder(baseDir).append(ISharedConstants.ELENCONOTECREDITO_TEMPLATE_NAME).toString()));
        }
        catch ( IOException e )
        {
            _log.error("Errore nell'apertura dello stream per il template dell'elenco note di credito", e);
            throw e;
        }
        list = dao.getList(idCliente, dtFrom, dtTo, idAgente, stato, null, null, 1, "asc");
        try
        {
            bytes = JasperRunManager.runReportToPdf(reportIs, params, new JRBeanCollectionDataSource(list));
        }
        catch ( JRException e )
        {
            _log.error("Errore nella generazione dell'elenco note di credito in formato pdf", e);
            throw e;
        }
        return bytes;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void delete(List<NotaCreditoDto> lista) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        for ( NotaCreditoDto dto : lista )
        {
            dao.delete(dto);
        }
    }

    public void deleteScadenzaPagamento(long id) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        dao.deleteScadenzaPagamento(id);
    }

    public DocumentoWrapperDto esportaNotaCreditoPdf(String dbKey,
                                                     long id)
    {
        try
        {
            TemplateData td = new TemplateData();
            Map<String, Object> params = new HashMap<>();
            td.setParameters(params);
            NotaCreditoTemplate ft = new NotaCreditoTemplate();
            AliquoteIvaDao aiDao = new AliquoteIvaDao(jdbcTemplate);
            // DatiAziendaDao daDao = new DatiAziendaDao(jdbcTemplate);

            NotaCreditoDto dto = this.getById(id);
            String outputName = new StringBuilder(IOUtility.getValidFilename(new StringBuilder("notacredito_").append(dto.getNumDocumento()).append(StringUtils.isEmpty(dto.getParticella()) ? "" : "/" + dto.getParticella()).toString())).append(".pdf").toString();

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
                    riDto.setIdAliquotaIva(pdDto.getIdAliquotaIva());
                    AliquotaIvaDto aiDto = aiDao.getById(pdDto.getIdAliquotaIva());
                    if ( riepilogoIva.contains(riDto) )
                    {
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImponibileMerce(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImponibileMerce() + pdDto.getTotaleSenzaIva());
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImponibileMerceFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImponibileMerce()));
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setTotaleImponibile(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getTotaleImponibile() + pdDto.getTotaleSenzaIva());
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getTotaleImponibile()));
//                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImportoIva(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImportoIva() + (pdDto.getTotaleSenzaIva() * aiDto.getImposta() / 100));
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
//                        riDto.setImportoIva(pdDto.getTotaleSenzaIva() * aiDto.getImposta() / 100);
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
            // for ( SpesaIncassoDocumentoDto siDto : dto.getListaSpeseIncassoFattura() )
            // {
            // siDto.setImportoFormattato(NumberUtils.formatAsCurrency(siDto.getImporto()));
            // }
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
                    riDto.setIdAliquotaIva(siDto.getIdAliquotaIva());
                    AliquotaIvaDto aiDto = aiDao.getById(siDto.getIdAliquotaIva());
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
            ft.setRiepilogoIva(riepilogoIva);
            BigDecimal totaleImponibile = BigDecimal.valueOf(totaleMerce);
            BigDecimal totaleIva = new BigDecimal(0);
            for ( RiepilogoIvaDto riDto : riepilogoIva )
            {
                riDto.setImportoIva(BigDecimal.valueOf(riDto.getTotaleImponibile()).multiply(BigDecimal.valueOf(riDto.getAliquotaIva()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).doubleValue());
                riDto.setImportoIvaFormattato(NumberUtils.formatAsCurrency(riDto.getImportoIva()));
                totaleIva = totaleIva.add(BigDecimal.valueOf(riDto.getImportoIva()));
                totaleImponibile = totaleImponibile.add(riDto.getImponibileSpese() == null ? BigDecimal.ZERO : BigDecimal.valueOf(riDto.getImponibileSpese()));
            }

            totaleIva = totaleIva.setScale(2, BigDecimal.ROUND_HALF_UP);
            totaleImponibile = totaleImponibile.setScale(2, BigDecimal.ROUND_HALF_UP);
            // context.put("totalemerce",
            // NumberUtils.formatAsCurrency(totaleMerce));
            params.put("totalemerce", NumberUtils.formatAsCurrency(totaleMerce));

            // Rivalsa INPS and Withholding Tax
            double importoRivalsa = 0;
            double ivaRivalsaCalculated = 0;
            if (dto.getFlRivalsaInps() != null && dto.getFlRivalsaInps() == 1) {
                double percRivalsa = dto.getPercRivalsaInps() != null ? dto.getPercRivalsaInps() : 4.0;
                importoRivalsa = BigDecimal.valueOf(totaleMerce).multiply(BigDecimal.valueOf(percRivalsa).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).doubleValue();
                
                // Use a default VAT rate for Rivalsa (e.g., 22% or first product's VAT)
                double impostaRivalsa = 22.0;
                if (dto.getProdotti() != null && !dto.getProdotti().isEmpty() && dto.getProdotti().get(0).getPercentualeIva() != null) {
                    impostaRivalsa = dto.getProdotti().get(0).getPercentualeIva();
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
            BigDecimal totaleImponibileCalculated = BigDecimal.ZERO;
            BigDecimal totaleIvaCalculated = new BigDecimal(0);
            for ( RiepilogoIvaDto riDto : riepilogoIva )
            {
                riDto.setImportoIva(BigDecimal.valueOf(riDto.getTotaleImponibile()).multiply(BigDecimal.valueOf(riDto.getAliquotaIva()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).doubleValue());
                riDto.setImportoIvaFormattato(NumberUtils.formatAsCurrency(riDto.getImportoIva()));
                totaleIvaCalculated = totaleIvaCalculated.add(BigDecimal.valueOf(riDto.getImportoIva()));
                totaleImponibileCalculated = totaleImponibileCalculated.add(BigDecimal.valueOf(riDto.getTotaleImponibile()));
            }

            totaleIvaCalculated = totaleIvaCalculated.setScale(2, BigDecimal.ROUND_HALF_UP);
            totaleImponibileCalculated = totaleImponibileCalculated.setScale(2, BigDecimal.ROUND_HALF_UP);

            params.put("totaleimponibile", NumberUtils.formatAsCurrency(totaleImponibileCalculated.doubleValue()));
            params.put("totaleiva", NumberUtils.formatAsCurrency(totaleIvaCalculated.doubleValue()));
            params.put("acconto", NumberUtils.formatAsCurrency(totAcconto));

            params.put("speseart15", NumberUtils.formatAsCurrency(totSpeseArt15));
            params.put("spesetrasporto", NumberUtils.formatAsCurrency(totTrasporto));
            params.put("spesealtre", NumberUtils.formatAsCurrency(totAltreSpese));
            
            // Fiscal parameters
            double ritenuta = 0;
            if (dto.getImportoRitenutaAcconto() != null) {
                ritenuta = dto.getImportoRitenutaAcconto().doubleValue();
            }
            params.put("importoRivalsaInps", NumberUtils.formatAsCurrency(importoRivalsa));
            params.put("importoRitenutaAcconto", NumberUtils.formatAsCurrency(ritenuta));
            params.put("importoIvaRivalsa", NumberUtils.formatAsCurrency(ivaRivalsaCalculated));
            params.put("annotazioni", dto.getAnnotazioneEstesa());

            double grandTotal = totaleImponibileCalculated.doubleValue() + totaleIvaCalculated.doubleValue() + totSpeseArt15;
            params.put("totalefattura", NumberUtils.formatAsCurrency(grandTotal));
            double nettoAPagare = grandTotal - totAcconto - ritenuta;
            if (dto.getSplitPayment() != null && dto.getSplitPayment() == 1) {
                nettoAPagare = nettoAPagare - totaleIvaCalculated.doubleValue();
            }
            params.put("totalenetto", NumberUtils.formatAsCurrency(nettoAPagare));
            params.put("totale_escluso_iva", NumberUtils.formatAsCurrency(totaleImponibileCalculated.doubleValue() + totSpeseArt15 - totAcconto - ritenuta));
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
            JasperReport report = ReportLoader.getReport("nota_credito.jrxml");
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
            _log.error("Errore durante la stampa della nota di credito {}", id, e);
            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(new byte[0]);
            result.setNome("blank.pdf");
            return result;
        }
    }

    public NotaCreditoDto getById(long id) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        ProdottiDao prodottiDao = new ProdottiDao(jdbcTemplate);
        NotaCreditoDto dto = dao.getById(id);
        if ( dto != null )
        {
            ClienteDto clienteDto = clientiDao.getById(dto.getIdCliente());
            IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
            ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
            clienteDto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(), dto.getIdCliente()));
            clienteDto.setElencoContatti(contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(), dto.getIdCliente()));
            dto.setClienteDto(clienteDto);
            if ( dto.getIdProgetto() != null )
            {
                ProgettiDao progettiDao = new ProgettiDao(jdbcTemplate);
                dto.setProgettoDto(progettiDao.getById(dto.getIdProgetto().longValue()));
            }
            if ( dto.getIdAgente() != null )
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
            // dto.setListaSpeseIncassoFattura(dao.getListSpeseIncassoById(dto.getId()));
            dto.setListaSpeseIncassoFattura(new ArrayList<SpesaIncassoDocumentoDto>());
            dto.setListaScadenzePagamentiDocumento(dao.getScadenzePagamento(dto.getId()));
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

    public Map<String, Object> getCombosMap() throws SQLException
    {
        AliquoteIvaDao aliquoteIvaDao = new AliquoteIvaDao(jdbcTemplate);
        UnitaMisuraDao unitaMisuraDao = new UnitaMisuraDao(jdbcTemplate);
        TipiPagamentoDao tipiPagamentoDao = new TipiPagamentoDao(jdbcTemplate);
        RisorseDao risorseDao = new RisorseDao(jdbcTemplate);
        CausaliEsigibilitaDifferitaDao cedDao = new CausaliEsigibilitaDifferitaDao(jdbcTemplate);
        DivisioniDao divisioniDao = new DivisioniDao(jdbcTemplate);
        ConfigurazioneDao configurazioneDao = new ConfigurazioneDao(jdbcTemplate);

        Map<String, Object> map = new HashMap<>();
        map.put(ISharedConstants.COMBOSMAP_KEY_ALIQUOTEIVA, aliquoteIvaDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_UNITAMISURA, unitaMisuraDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_TIPIPAGAMENTO, tipiPagamentoDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_BANCHE, risorseDao.getListForCombo(RisorsaDto.Tipologia.BANCA.getValore()));
        map.put(ISharedConstants.COMBOSMAP_KEY_CAUSALIESIGIBILITADIFFERITA, cedDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_DIVISIONI, divisioniDao.getListForCombo());
        String particelleAsString = configurazioneDao.getByKey(ISharedConstants.CONFIG_DOMAIN_DOCUMENTI, ISharedConstants.CONFIG_KEY_PARTICELLE);
        map.put(ISharedConstants.COMBOSMAP_KEY_PARTICELLE, StringUtils.split(particelleAsString, StringUtils.CR + StringUtils.LF));
        return map;
    }

    public FatturaElettronicaWrapperDto getFatturaElettronica(long idNotaCredito)
    {
        FatturaElettronicaWrapperDto result = new FatturaElettronicaWrapperDto();
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        DatiAziendaDao datiAziendaDao = new DatiAziendaDao(jdbcTemplate);
        try
        {
            DatiAziendaDto datiAziendaDto = datiAziendaDao.getDatiAzienda();
            NotaCreditoDto notaCreditoDto = this.getById(idNotaCredito);
            result.setFattura(notaCreditoDto);
            String xmlFatturaElettronica = dao.getXmlFatturaElettronica(idNotaCredito);
            if ( StringUtils.isNotEmpty(xmlFatturaElettronica) )
            {
                Date dt = FastDateFormat.getInstance("dd/MM/yyyy").parse(result.getFattura().getDataDocumento());
                Calendar cal = Calendar.getInstance();
                cal.setTime(dt);
                DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
                String formattedDate = df.format(cal.getTime());
                result.getFattura().setNomeFileFattura(new StringBuilder("IT").append(datiAziendaDto.getPartitaIva()).append("_").append(formattedDate + StringUtils.leftPad("" + result.getFattura().getNumDocumento(), 3, "0")).toString());
                result.setFlussoFatturaElettronica(xmlFatturaElettronica.getBytes());
                notaCreditoDto.setXmlFattura(xmlFatturaElettronica);
                return result;
            }
            else
            {
                try
                {
                    fatturaelettronicaDelegate.getFatturaElettronica(notaCreditoDto, null);
                    if ( StringUtils.isNotBlank(notaCreditoDto.getErroreValidazioneXml()) )
                    {
                        result.setFlussoFatturaElettronica(notaCreditoDto.getXmlNonValido().getBytes());
                        result.getFattura().setNomeFileFattura("notacredito_" + result.getFattura().getNumDocumento());
                    }
                    else
                    {
                        result.setFlussoFatturaElettronica(notaCreditoDto.getXmlFattura().getBytes());
                    }
                    try
                    {
                        dao.salvaXmlFatturaElettronica(idNotaCredito, notaCreditoDto.getXmlFattura());
                    }
                    catch ( SQLException e )
                    {

                    }
                }
                catch ( Exception e )
                {
                    result.setFlussoFatturaElettronica(ExceptionUtils.getStackTrace(e).getBytes());
                    result.getFattura().setNomeFileFattura("notacredito_" + result.getFattura().getNumDocumento());
                }
            }
        }
        catch ( SQLException | ParseException e )
        {
            // errore recupero fattura
            result.setFlussoFatturaElettronica(ExceptionUtils.getStackTrace(e).getBytes());
            result.getFattura().setNomeFileFattura("notacredito_" + result.getFattura().getNumDocumento());
        }
        return result;
    }

    public List<MovimentiDocumentoDto> getFattureAssociabili(long idCliente,
                                                             String dataNotaCredito) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        return dao.getFattureAssociabili(idCliente, dataNotaCredito);
    }

    public List<MovimentiDocumentoDto> getListForExcel(Integer idCliente,
                                                       String dtFrom,
                                                       String dtTo,
                                                       Integer idAgente,
                                                       String stato,
                                                       Integer length,
                                                       Integer start,
                                                       Integer orderColumn,
                                                       String orderDir) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        return dao.getList(idCliente, dtFrom, dtTo, idAgente, stato, length, start, orderColumn, orderDir);
    }

    public FattureListResponse getList(Integer idCliente,
                                       String dtFrom,
                                       String dtTo,
                                       Integer idAgente,
                                       String stato,
                                       Integer length,
                                       Integer start,
                                       Integer orderColumn,
                                       String orderDir) throws SQLException
    {
        FattureListResponse dto = new FattureListResponse();
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        List<MovimentiDocumentoDto> list = dao.getList(idCliente, dtFrom, dtTo, idAgente, stato, length, start, orderColumn, orderDir);
        BigDecimal totFatturato = BigDecimal.ZERO;
        BigDecimal totDaSaldare = BigDecimal.ZERO;
        BigDecimal totSaldato = BigDecimal.ZERO;
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
        // RiepilogoFattureDto riepilogoDto = dao.getRiepilogo(idCliente, dtFrom, dtTo, idAgente, stato);
        // dto.setTotalCount(riepilogoDto.getTotRighe());
        // dto.setTotalFiltered(riepilogoDto.getTotRighe());
        // dto.setTotFatturato(riepilogoDto.getTotFatturato());
        // dto.setTotDaSaldare(riepilogoDto.getTotDaSaldare());
        // dto.setTotSaldato(riepilogoDto.getTotSaldato());
        // dto.setList(list);
        return dto;
    }

    public Integer getNextNumNotaCredito(String data,
                                         int flFatturaElettronica) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        return dao.getNextNum(data, flFatturaElettronica);
    }

    public List<Long> getNoteCreditoDaInviare(long[] idFatture) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        return dao.getNoteCreditoDaInviare(idFatture);
    }

    public RiepilogoFattureDto getRiepilogo(Integer idCliente,
                                            String dtFrom,
                                            String dtTo,
                                            Integer idAgente,
                                            String stato) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        return dao.getRiepilogo(idCliente, dtFrom, dtTo, idAgente, stato);
    }

    public ScadenzaPagamentoDocumentoDto getScadenzaPagamento(long idScadenza) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        return dao.getScadenzaPagamento(idScadenza);
    }

    @Transactional(rollbackFor = SQLException.class)
    public long insert(NotaCreditoDto dto) throws SQLException
    {
        gestisciAnnotazioniRivalsa(dto);
        if ( isExistentNumero(dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(), dto.getFlFatturaElettronica(), dto.getId()) )
        {
            throw new SQLException("Il numero di nota credito " + dto.getNumDocumento() + (StringUtils.isNotBlank(dto.getParticella()) ? "/" + dto.getParticella() : "") + " è già presente per l'anno di riferimento.");
        }
        NoteCreditoDao noteCreditoDao = new NoteCreditoDao(jdbcTemplate);
        long idNotaCredito = noteCreditoDao.insert(dto);
        for ( ProdottoDocumentoDto prodottoDdtDto : dto.getProdotti() )
        {
            prodottoDdtDto.setIdDocumento(idNotaCredito);
            noteCreditoDao.insertProdotto(prodottoDdtDto);
        }
        // if ( dto.getListaSpeseIncassoFattura() != null )
        // {
        // for ( SpesaIncassoDocumentoDto spesaIncassoDdtDto : dto.getListaSpeseIncassoFattura() )
        // {
        // spesaIncassoDdtDto.setIdFattura(idFattura);
        // noteCreditoDao.insertSpesaIncasso(spesaIncassoDdtDto);
        // }
        // }
        if ( dto.getListaScadenzePagamentiDocumento() != null )
        {
            for ( ScadenzaPagamentoDocumentoDto scadenzaPagamentoDocumentoDto : dto.getListaScadenzePagamentiDocumento() )
            {
                scadenzaPagamentoDocumentoDto.setIdDocumento(idNotaCredito);
                noteCreditoDao.insertScadenzaPagamento(scadenzaPagamentoDocumentoDto);
            }
        }
        double totale = noteCreditoDao.getTotale(idNotaCredito);
        double totalePagato = noteCreditoDao.getTotalePagato(idNotaCredito);
        noteCreditoDao.aggiornaTotaliNotaCredito(totale, totalePagato, idNotaCredito);
        return idNotaCredito;
    }

    public boolean isExistentNumero(Integer numeroDdt,
                                    String particella,
                                    String data,
                                    int flFatturaElettronica,
                                    Long id) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);
        return dao.isExistentNumero(numeroDdt, particella, data, flFatturaElettronica, id);
    }

    @Transactional(rollbackFor = SQLException.class)
    public long update(NotaCreditoDto dto) throws SQLException
    {
        gestisciAnnotazioniRivalsa(dto);
        if ( isExistentNumero(dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(), dto.getFlFatturaElettronica(), dto.getId()) )
        {
            throw new SQLException("Il numero di nota credito " + dto.getNumDocumento() + (StringUtils.isNotBlank(dto.getParticella()) ? "/" + dto.getParticella() : "") + " è già presente per l'anno di riferimento.");
        }
        NoteCreditoDao noteCreditoDao = new NoteCreditoDao(jdbcTemplate);
        noteCreditoDao.update(dto);
        noteCreditoDao.deleteProdottiById(dto.getId());
        for ( ProdottoDocumentoDto prodottoDdtDto : dto.getProdotti() )
        {
            prodottoDdtDto.setIdDocumento(dto.getId());
            noteCreditoDao.insertProdotto(prodottoDdtDto);
        }
        // noteCreditoDao.deleteSpeseIncassoById(dto.getId());
        // if ( dto.getListaSpeseIncassoFattura() != null )
        // {
        // for ( SpesaIncassoDocumentoDto spesaIncassoDdtDto : dto.getListaSpeseIncassoFattura() )
        // {
        // spesaIncassoDdtDto.setIdFattura(dto.getId());
        // noteCreditoDao.insertSpesaIncasso(spesaIncassoDdtDto);
        // }
        // }
        noteCreditoDao.deleteScadenzePagamento(dto.getId());
        if ( dto.getListaScadenzePagamentiDocumento() != null )
        {
            for ( ScadenzaPagamentoDocumentoDto scadenzaPagamentoDocumentoDto : dto.getListaScadenzePagamentiDocumento() )
            {
                scadenzaPagamentoDocumentoDto.setIdDocumento(dto.getId());
                noteCreditoDao.insertScadenzaPagamento(scadenzaPagamentoDocumentoDto);
            }
        }
        double totale = noteCreditoDao.getTotale(dto.getId());
        double totalePagato = noteCreditoDao.getTotalePagato(dto.getId());
        noteCreditoDao.aggiornaTotaliNotaCredito(totale, totalePagato, dto.getId());
        return dto.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateScadenzaPagamento(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        NoteCreditoDao dao = new NoteCreditoDao(jdbcTemplate);

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
            // Pagamento parziale
            dao.updateScadenzaPagamento(dto);
            
            ScadenzaPagamentoDocumentoDto residuoDto = new ScadenzaPagamentoDocumentoDto();
            residuoDto.setIdDocumento(currentDto.getIdDocumento());
            residuoDto.setDtScadenza(currentDto.getDtScadenza());
            residuoDto.setImporto(currentTotal - newTotal);
            residuoDto.setImportoSpeseIncasso(0.0);
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

        double totale = dao.getTotale(currentDto.getIdDocumento());
        double totalePagato = dao.getTotalePagato(currentDto.getIdDocumento());
        dao.aggiornaTotaliNotaCredito(totale, totalePagato, currentDto.getIdDocumento());
    }

    private void gestisciAnnotazioniRivalsa(NotaCreditoDto dto) {
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

