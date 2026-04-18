package it.tinna.smartdoc.server.delegate.documenti;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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

import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.server.constants.TipoScontoDocumentoEnum;
import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.dao.configurazione.ConfigurazioneDao;
import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.server.dao.datiazienda.DatiAziendaDao;
import it.tinna.smartdoc.server.dao.divisioni.DivisioniDao;
import it.tinna.smartdoc.server.dao.documenti.DocumentiDao;
import it.tinna.smartdoc.server.dao.documenti.FattureDao;
import it.tinna.smartdoc.server.dao.documenti.FattureFornitoreDao;
import it.tinna.smartdoc.server.dao.fornitori.FornitoriDao;
import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.server.dao.prodotti.ProdottiDao;
import it.tinna.smartdoc.server.dao.progetti.ProgettiDao;
import it.tinna.smartdoc.server.dao.risorse.RisorseDao;
import it.tinna.smartdoc.server.dao.speseincasso.SpeseIncassoDao;
import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.server.dao.unitamisura.UnitaMisuraDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.aliquoteiva.AliquoteIvaDelegate;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.server.delegate.fornitori.FornitoriDelegate;
import it.tinna.smartdoc.server.util.DateUtility;
import it.tinna.smartdoc.server.util.IOUtility;
import it.tinna.smartdoc.server.util.NumberUtils;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DatiPagamentoType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DettaglioLineeType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.DettaglioPagamentoType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.FatturaElettronicaType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.ScontoMaggiorazioneType;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaFornitoreDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto.TipologiaIndirizzo;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.risorse.RisorsaDto;
import it.tinna.smartdoc.shared.dto.speseincasso.SpesaIncassoDto;
import it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto;
import it.tinna.smartdoc.shared.dto.template.TemplateData;
import it.tinna.smartdoc.shared.dto.template.fatture.FatturaTemplate;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import it.tinna.smartdoc.server.util.ReportLoader;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service(value = "fattureFornitoreDelegate")
public class FattureFornitoreDelegate extends BaseDelegate
{

    @Autowired
    private AliquoteIvaDelegate aliquoteivaDelegate;

    @Autowired
    private DatiAziendaDelegate datiaziendaDelegate;

    @Autowired
    private FornitoriDelegate   fornitoriDelegate;

    // public FattureFornitoreDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    /**
     * Genera il pdf relativo all'elenco delle fatture fornitore (viene usato per esportare la lista in pdf oppure per la stampa diretta)
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
                                Integer idFornitore,
                                String dtDocumentoFrom,
                                String dtDocumentoTo,
                                String numeroDocumento,
                                String dtRegistrazioneFrom,
                                String dtRegistrazioneTo,
                                Integer numeroRegistrazione,
                                String stato) throws Exception
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        List<FatturaFornitoreDto> list = new ArrayList<>();
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
        InputStream reportIs = FileUtils.openInputStream(new File(new StringBuilder(baseDir).append(ISharedConstants.ELENCOFATTUREFORNITORE_TEMPLATE_NAME).toString()));
        list = dao.getList(idFornitore, dtDocumentoFrom, dtDocumentoTo, numeroDocumento, dtRegistrazioneFrom, dtRegistrazioneTo, numeroRegistrazione, stato, null, null, 0, "asc");
        try
        {
            bytes = JasperRunManager.runReportToPdf(reportIs, params, new JRBeanCollectionDataSource(list));
        }
        catch ( JRException e )
        {
            _log.error("Errore nella generazione dell'elenco fatture fornitore in formato pdf", e);
            throw e;
        }
        return bytes;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void delete(List<FatturaFornitoreDto> lista) throws SQLException
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        for ( FatturaFornitoreDto dto : lista )
        {
            dao.delete(dto);
        }
    }

    public void deleteScadenzaPagamento(Integer id) throws SQLException
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        dao.deleteScadenzaPagamento(id);
    }

    public DocumentoWrapperDto esportaFatturaFornitorePdf(String dbKey,
                                                          long id)
    {
        try
        {
            TemplateData td = new TemplateData();
            Map<String, Object> params = new HashMap<>();
            td.setParameters(params);
            FatturaTemplate ft = new FatturaTemplate();
            AliquoteIvaDao aiDao = new AliquoteIvaDao(jdbcTemplate);
            // DatiAziendaDao daDao = new DatiAziendaDao(jdbcTemplate);

            FatturaFornitoreDto dto = this.getById(id);
            String outputName = new StringBuilder(IOUtility.getValidFilename(new StringBuilder("fatturafornitore_").append(dto.getNumeroDocumentoFornitore()).toString())).append(".pdf").toString();

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
                        BigDecimal val = BigDecimal.valueOf(pdDto.getTotaleSenzaIva());
                        val = val.multiply(BigDecimal.valueOf(aiDto.getImposta()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                        val = val.setScale(2, RoundingMode.HALF_UP);
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImportoIva(BigDecimal.valueOf(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImportoIva()).add(val).doubleValue());
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImportoIvaFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImportoIva()));
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
                        BigDecimal val = BigDecimal.valueOf(pdDto.getTotaleSenzaIva());
                        val = val.multiply(BigDecimal.valueOf(aiDto.getImposta()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                        val = val.setScale(2, RoundingMode.HALF_UP);
                        riDto.setImportoIva(val.doubleValue());
                        riDto.setImportoIvaFormattato(NumberUtils.formatAsCurrency(riDto.getImportoIva()));
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
                    riDto.setIdAliquotaIva(siDto.getIdAliquotaIva());
                    AliquotaIvaDto aiDto = aiDao.getById(siDto.getIdAliquotaIva());
                    pdDto.setPercentualeIvaFormattata(NumberUtils.formatAsPercentage(aiDto.getImposta()));
                    if ( riepilogoIva.contains(riDto) )
                    {
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImponibileSpese(siDto.getImporto());
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImponibileSpeseFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImponibileSpese()));
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setTotaleImponibile(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getTotaleImponibile() + siDto.getImporto());
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getTotaleImponibile()));
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImportoIva(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImportoIva() + (siDto.getImporto() * aiDto.getImposta() / 100));
                        riepilogoIva.get(riepilogoIva.indexOf(riDto)).setImportoIvaFormattato(NumberUtils.formatAsCurrency(riepilogoIva.get(riepilogoIva.indexOf(riDto)).getImportoIva()));
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
                        riDto.setImportoIva(siDto.getImporto() * aiDto.getImposta() / 100);
                        riDto.setImportoIvaFormattato(NumberUtils.formatAsCurrency(riDto.getImportoIva()));
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
            // context.put("riepilogoiva", riepilogoIva);
            // params.put("riepilogoiva", riepilogoIva);
            ft.setRiepilogoIva(riepilogoIva);
            BigDecimal totaleImponibile = BigDecimal.valueOf(totaleMerce);
            BigDecimal totaleIva = new BigDecimal(0);
            for ( RiepilogoIvaDto riDto : riepilogoIva )
            {
                totaleIva = totaleIva.add(BigDecimal.valueOf(riDto.getImportoIva()));
                totaleImponibile = totaleImponibile.add(riDto.getImponibileSpese() == null ? new BigDecimal(0) : BigDecimal.valueOf(riDto.getImponibileSpese()));
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
            // context.put("totalefattura",
            // NumberUtils.formatAsCurrency(totaleImponibile.doubleValue() +
            // totaleIva.doubleValue() + totSpeseArt15));
            params.put("totalefattura", NumberUtils.formatAsCurrency(totaleImponibile.doubleValue() + totaleIva.doubleValue() + totSpeseArt15));
            // context.put("totalenetto",
            // NumberUtils.formatAsCurrency(totaleImponibile.doubleValue() +
            // totaleIva.doubleValue() + totSpeseArt15 - totAcconto));
            params.put("totalenetto", NumberUtils.formatAsCurrency(totaleImponibile.doubleValue() + totaleIva.doubleValue() + totSpeseArt15 - totAcconto));
            params.put("totale_escluso_iva", NumberUtils.formatAsCurrency(totaleImponibile.doubleValue() + totSpeseArt15 - totAcconto));
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
            JasperReport report = ReportLoader.getReport("fatturafornitore.jrxml");
            JasperReport subreportScadenze = ReportLoader.getReport("fatturafornitore_scadenze.jrxml");
            
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
            _log.error("Errore nella generazione del pdf della fattura fornitore {}", id, e);
            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(new byte[0]);
            result.setNome("blank.pdf");
            return result;
        }
    }

    public FatturaFornitoreDto getById(long id) throws Exception
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        ProdottiDao prodottiDao = new ProdottiDao(jdbcTemplate);
        FatturaFornitoreDto dto = dao.getById(id);
        if ( dto != null )
        {
            FornitoreDto fornitoreDto = fornitoriDao.getById(dto.getIdFornitore());
            IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
            ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
            fornitoreDto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getIdFornitore()));
            fornitoreDto.setElencoContatti(contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), dto.getIdFornitore()));
            dto.setFornitoreDto(fornitoreDto);
            if ( dto.getIdProgetto() != null )
            {
                ProgettiDao progettiDao = new ProgettiDao(jdbcTemplate);
                dto.setProgettoDto(progettiDao.getById(dto.getIdProgetto().longValue()));
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
        }
        return dto;
    }

    public FatturaFornitoreDto getByNumeroEFornitore(String numeroDocumentoFornitore,
                                                     long idFornitore) throws SQLException
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        return dao.getByNumeroEFornitore(numeroDocumentoFornitore, idFornitore);
    }

    public Map<String, Object> getCombosMap() throws SQLException
    {
        AliquoteIvaDao aliquoteIvaDao = new AliquoteIvaDao(jdbcTemplate);
        UnitaMisuraDao unitaMisuraDao = new UnitaMisuraDao(jdbcTemplate);
        TipiPagamentoDao tipiPagamentoDao = new TipiPagamentoDao(jdbcTemplate);
        RisorseDao risorseDao = new RisorseDao(jdbcTemplate);
        DivisioniDao divisioniDao = new DivisioniDao(jdbcTemplate);

        Map<String, Object> map = new HashMap<>();
        map.put(ISharedConstants.COMBOSMAP_KEY_ALIQUOTEIVA, aliquoteIvaDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_UNITAMISURA, unitaMisuraDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_TIPIPAGAMENTO, tipiPagamentoDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_BANCHE, risorseDao.getListForCombo(RisorsaDto.Tipologia.BANCA.getValore()));
        map.put(ISharedConstants.COMBOSMAP_KEY_DIVISIONI, divisioniDao.getListForCombo());
        return map;
    }

    public List<FatturaFornitoreDto> getListForExcel(Integer idFornitore,
                                                     String dtFrom,
                                                     String dtTo,
                                                     String numeroDocumento,
                                                     String dtRegistrazioneFrom,
                                                     String dtRegistrazioneTo,
                                                     Integer numeroRegistrazione,
                                                     String stato,
                                                     Integer length,
                                                     Integer start,
                                                     Integer orderColumn,
                                                     String orderDir) throws SQLException
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        return dao.getList(idFornitore, dtFrom, dtTo, numeroDocumento, dtRegistrazioneFrom, dtRegistrazioneTo, numeroRegistrazione, stato, length, start, orderColumn, orderDir);
    }

    public DatatablesResponseDto<FatturaFornitoreDto> getList(Integer idFornitore,
                                                              String dtFrom,
                                                              String dtTo,
                                                              String numeroDocumento,
                                                              String dtRegistrazioneFrom,
                                                              String dtRegistrazioneTo,
                                                              Integer numeroRegistrazione,
                                                              String stato,
                                                              Integer length,
                                                              Integer start,
                                                              Integer orderColumn,
                                                              String orderDir) throws SQLException
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        List<FatturaFornitoreDto> list = dao.getList(idFornitore, dtFrom, dtTo, numeroDocumento, dtRegistrazioneFrom, dtRegistrazioneTo, numeroRegistrazione, stato, length, start, orderColumn, orderDir);
        DatatablesResponseDto<FatturaFornitoreDto> dto = new DatatablesResponseDto<>();
        dto.setTotalCount(list == null || list.isEmpty() ? 0 : list.get(0).getTotal());
        dto.setTotalFiltered(dto.getTotalCount());
        dto.setList(list);
        dto.setList(list);
        return dto;
    }

    public Integer getNextNum(String data) throws SQLException
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        return dao.getNextNum(data);
    }

    public ScadenzaPagamentoDocumentoDto getScadenzaPagamento(long idScadenza) throws SQLException
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        return dao.getScadenzaPagamento(idScadenza);
    }

    public List<MovimentiDocumentoDto> getUltimeFatture() throws SQLException
    {
        FattureDao dao = new FattureDao(jdbcTemplate);
        return dao.getUltimeFatture();
    }

    public void importFromSdi(byte[] fileFatturaElettronica) throws SQLException
    {
        FastDateFormat fastFormat = FastDateFormat.getInstance("dd/MM/yyyy");
        // FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(FatturaElettronicaType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<FatturaElettronicaType> root = jaxbUnmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(fileFatturaElettronica)), FatturaElettronicaType.class);
            FatturaElettronicaType fattura = root.getValue();
            FatturaFornitoreDto fatturaDto = new FatturaFornitoreDto();
            fatturaDto.setNumeroDocumentoFornitore(fattura.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiGeneraliDocumento().getNumero());
            fatturaDto.setDataDocumentoFornitore(fastFormat.format(fattura.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiGeneraliDocumento().getData()));
            String partitaIva = null;
            if ( fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA() != null && StringUtils.isNotBlank(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice()) )
            {
                partitaIva = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
            }
            else if ( StringUtils.isNotBlank(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale()) )
            {
                partitaIva = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale();
            }
            else
            {
                _log.error("Non posso importare la fattura fornitore nel database da SdI poichè la partita iva è nulla");
                return;
            }
            FornitoreDto fornitoreDto = fornitoriDelegate.getByPartitaIva(partitaIva);
            if ( fornitoreDto != null )
            {
                FatturaFornitoreDto existentDto = this.getByNumeroEFornitore(fatturaDto.getNumeroDocumentoFornitore(), fornitoreDto.getId());
                if ( existentDto != null )
                {
                    _log.info("Per il fornitore {} - {} esiste già la fattura con numero {} - Id fattura: {}", fornitoreDto.getId(), fornitoreDto.getDenominazione(), fatturaDto.getNumeroDocumentoFornitore(), existentDto.getId());
                    fatturaDto.setId(existentDto.getId());
                    fatturaDto.setDataDocumento(existentDto.getDataDocumento());
                    fatturaDto.setNumDocumento(existentDto.getNumDocumento());
                }
                fatturaDto.setIdFornitore(fornitoreDto.getId());
                if ( fornitoreDto.getElencoIndirizzi() != null )
                {
                    for ( IndirizzoDto indirizzoDto : fornitoreDto.getElencoIndirizzi() )
                    {
                        if ( indirizzoDto.getTipologia().equals(TipologiaIndirizzo.SEDE_OPERATIVA.getValore()) )
                        {
                            fatturaDto.setIndirizzoIntestazione(indirizzoDto.getIndirizzo());
                            fatturaDto.setCapIntestazione(indirizzoDto.getCap());
                            fatturaDto.setCittaIntestazione(indirizzoDto.getCitta());
                            fatturaDto.setProvinciaIntestazione(indirizzoDto.getProvincia());
                            fatturaDto.setNazioneIntestazione(indirizzoDto.getNazione());
                            fatturaDto.setIndirizzoDestinazione(indirizzoDto.getIndirizzo());
                            fatturaDto.setCapDestinazione(indirizzoDto.getCap());
                            fatturaDto.setCittaDestinazione(indirizzoDto.getCitta());
                            fatturaDto.setProvinciaDestinazione(indirizzoDto.getProvincia());
                            fatturaDto.setNazioneDestinazione(indirizzoDto.getNazione());
                        }
                    }
                }
            }
            else
            {
                fornitoreDto = new FornitoreDto();
                fornitoreDto.setCodice(fornitoriDelegate.generaCodice());
                String denominazione = null;
                if ( StringUtils.isNotEmpty(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione()) )
                {
                    denominazione = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione();
                }
                else
                {
                    denominazione = new StringBuilder(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getCognome()).append(" ").append(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getNome()).toString();
                }
                fornitoreDto.setDenominazione(denominazione);
                fornitoreDto.setPartitaIva(partitaIva);
                IndirizzoDto indirizzoDto = new IndirizzoDto();
                indirizzoDto.setTipologia(TipologiaIndirizzo.SEDE_OPERATIVA.getValore());
                indirizzoDto.setIndirizzo(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getIndirizzo());
                indirizzoDto.setCap(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getCAP());
                indirizzoDto.setCitta(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getComune());
                indirizzoDto.setProvincia(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getProvincia());
                indirizzoDto.setNazione(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getNazione());
                List<IndirizzoDto> elencoIndirizzi = new ArrayList<>();
                elencoIndirizzi.add(indirizzoDto);
                fornitoreDto.setElencoIndirizzi(elencoIndirizzi);
                long idFornitore = fornitoriDelegate.insert(fornitoreDto, elencoIndirizzi, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                fatturaDto.setIdFornitore(idFornitore);
                fatturaDto.setIndirizzoIntestazione(indirizzoDto.getIndirizzo());
                fatturaDto.setCapIntestazione(indirizzoDto.getCap());
                fatturaDto.setCittaIntestazione(indirizzoDto.getCitta());
                fatturaDto.setProvinciaIntestazione(indirizzoDto.getProvincia());
                fatturaDto.setNazioneIntestazione(indirizzoDto.getNazione());
                fatturaDto.setIndirizzoDestinazione(indirizzoDto.getIndirizzo());
                fatturaDto.setCapDestinazione(indirizzoDto.getCap());
                fatturaDto.setCittaDestinazione(indirizzoDto.getCitta());
                fatturaDto.setProvinciaDestinazione(indirizzoDto.getProvincia());
                fatturaDto.setNazioneDestinazione(indirizzoDto.getNazione());
            }
            List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
            fatturaDto.setProdotti(prodotti);
            List<DettaglioLineeType> linee = fattura.getFatturaElettronicaBody().get(0).getDatiBeniServizi().getDettaglioLinee();
            for ( DettaglioLineeType linea : linee )
            {
                ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
                if ( linea.getCodiceArticolo() != null && !linea.getCodiceArticolo().isEmpty() )
                {
                    pdDto.setFmCodice(linea.getCodiceArticolo().get(0).getCodiceValore());
                }
                pdDto.setFmDescrizione(linea.getDescrizione());
                pdDto.setQuantita(linea.getQuantita() == null ? 1 : linea.getQuantita());
                pdDto.setFmUnitaMisura(linea.getUnitaMisura());
                // pdDto.setIdUnitaMisura(8);
                pdDto.setPrezzo(linea.getPrezzoUnitario());
                List<AliquotaIvaDto> aliquote = aliquoteivaDelegate.getByAliquota(linea.getAliquotaIVA());
                if ( aliquote != null && !aliquote.isEmpty() )
                {
                    pdDto.setIdAliquotaIva((int) aliquote.get(0).getId());
                }
                else
                {
                    _log.error("Nessuna aliquota iva trovata con valore aliquota {}", linea.getAliquotaIVA());
                }
                if ( linea.getScontoMaggiorazione() != null && !linea.getScontoMaggiorazione().isEmpty() )
                {
                    for ( ScontoMaggiorazioneType scontoMaggiorazione : linea.getScontoMaggiorazione() )
                    {
                        if ( scontoMaggiorazione.getTipo() == TipoScontoDocumentoEnum.SCONTO || scontoMaggiorazione.getTipo() == TipoScontoDocumentoEnum.SCONTO_MERCE )
                        {
                            if ( scontoMaggiorazione.getPercentuale() != null )
                            {
                                if ( StringUtils.isNotBlank(pdDto.getSconto()) )
                                {
                                    pdDto.setSconto(pdDto.getSconto() + "+");
                                }
                                pdDto.setSconto(new StringBuilder(StringUtils.defaultIfEmpty(pdDto.getSconto(), StringUtils.EMPTY)).append(scontoMaggiorazione.getPercentuale()).append("%").toString());
                            }
                            else
                            {
                                pdDto.setSconto(scontoMaggiorazione.getImporto().toString());
                            }
                        }
                    }
                }
                BigDecimal prezzoUnitario = BigDecimal.valueOf(pdDto.getPrezzo());
                if ( StringUtils.isNotBlank(pdDto.getSconto()) )
                {
                    prezzoUnitario = NumberUtils.getPrezzoScontato(pdDto.getPrezzo(), pdDto.getSconto());
                }
                pdDto.setPrezzoImponibile(prezzoUnitario.multiply(BigDecimal.valueOf(pdDto.getQuantita())).doubleValue());
                prodotti.add(pdDto);
            }
            List<ScadenzaPagamentoDocumentoDto> scadenze = new ArrayList<>();
            if ( fattura.getFatturaElettronicaBody().get(0).getDatiPagamento() != null )
            {
                for ( DatiPagamentoType datiPagamento : fattura.getFatturaElettronicaBody().get(0).getDatiPagamento() )
                {
                    if ( datiPagamento.getDettaglioPagamento() != null )
                    {
                        for ( DettaglioPagamentoType dettaglioPagamento : datiPagamento.getDettaglioPagamento() )
                        {
                            ScadenzaPagamentoDocumentoDto scadenzaDto = new ScadenzaPagamentoDocumentoDto();
                            scadenzaDto.setAcconto(0);
                            if ( dettaglioPagamento.getDataScadenzaPagamento() == null )
                            {
                                scadenzaDto.setDtScadenza(fatturaDto.getDataDocumento());
                            }
                            else
                            {
                                scadenzaDto.setDtScadenza(DateUtility.format(dettaglioPagamento.getDataScadenzaPagamento(), "dd/MM/yyyy"));
                            }
                            scadenzaDto.setImporto(dettaglioPagamento.getImportoPagamento());
                            scadenze.add(scadenzaDto);
                        }
                    }
                }
            }

//            ScadenzaPagamentoDocumentoDto scadenzaDto = new ScadenzaPagamentoDocumentoDto();
//            scadenzaDto.setDtScadenza(fatturaDto.getDataDocumento());
//            scadenzaDto.setAcconto(0);
//            if ( fattura.getFatturaElettronicaBody().get(0).getDatiBeniServizi().getDatiRiepilogo() != null && !fattura.getFatturaElettronicaBody().get(0).getDatiBeniServizi().getDatiRiepilogo().isEmpty() )
//            {
//                scadenzaDto.setImporto(fattura.getFatturaElettronicaBody().get(0).getDatiBeniServizi().getDatiRiepilogo().get(0).getImponibileImporto() + fattura.getFatturaElettronicaBody().get(0).getDatiBeniServizi().getDatiRiepilogo().get(0).getImposta());
//                fatturaDto.setEsigibilitaDifferita(fattura.getFatturaElettronicaBody().get(0).getDatiBeniServizi().getDatiRiepilogo().get(0).getEsigibilitaIVA() == EsigibilitaIvaEnum.IMMEDIATA ? 0 : 1);
//            }
//            scadenze.add(scadenzaDto);
            fatturaDto.setListaScadenzePagamentiDocumento(scadenze);
            if ( fatturaDto.getId() == 0 )
            {
                fatturaDto.setNumDocumento(this.getNextNum(fatturaDto.getDataDocumento()));
                fatturaDto.setDataDocumento(fastFormat.format(new Date()));
                this.insert(fatturaDto);
            }
            else
            {
                this.update(fatturaDto);
            }
        }
        catch ( JAXBException e )
        {
            _log.error("Errore nell'unmarshalling del file della fattura elettronica", e);
        }
    }

    @Transactional(rollbackFor = SQLException.class)
    public Integer insert(FatturaFornitoreDto dto) throws SQLException
    {
        FattureFornitoreDao ffDao = new FattureFornitoreDao(jdbcTemplate);
        Integer idFattura = ffDao.insert(dto);
        for ( ProdottoDocumentoDto prodottoFatturaDto : dto.getProdotti() )
        {
            prodottoFatturaDto.setIdDocumento(idFattura);
            ffDao.insertProdotto(prodottoFatturaDto);
        }
        if ( dto.getListaSpeseIncassoFattura() != null )
        {
            for ( SpesaIncassoDocumentoDto spesaIncassoDdtDto : dto.getListaSpeseIncassoFattura() )
            {
                spesaIncassoDdtDto.setIdFattura(idFattura);
                ffDao.insertSpesaIncasso(spesaIncassoDdtDto);
            }
        }
        if ( dto.getListaScadenzePagamentiDocumento() != null )
        {
            for ( ScadenzaPagamentoDocumentoDto scadenzaPagamentoDocumentoDto : dto.getListaScadenzePagamentiDocumento() )
            {
                scadenzaPagamentoDocumentoDto.setIdDocumento(idFattura);
                ffDao.insertScadenzaPagamento(scadenzaPagamentoDocumentoDto);
            }
        }
        if ( dto.getIdOrdini() != null && !dto.getIdOrdini().isEmpty() )
        {
            DocumentiDao documentiDao = new DocumentiDao(jdbcTemplate);
            for ( Integer idOrdine : dto.getIdOrdini() )
            {
                documentiDao.associaDoc(idFattura, ISharedConstants.TIPODOCASSOCIATO_FATTURAFORNITORE, idOrdine, ISharedConstants.TIPODOCASSOCIATO_ORDINE);
            }
        }
        else if ( dto.getIdBolleCarico() != null && !dto.getIdBolleCarico().isEmpty() )
        {
            DocumentiDao documentiDao = new DocumentiDao(jdbcTemplate);
            for ( Integer idBollaCarico : dto.getIdBolleCarico() )
            {
                documentiDao.associaDoc(idFattura, ISharedConstants.TIPODOCASSOCIATO_FATTURAFORNITORE, idBollaCarico, ISharedConstants.TIPODOCASSOCIATO_BOLLACARICO);
            }
        }
        // if (dto.getIdOrdini() != null && !dto.getIdOrdini().isEmpty()) {
        // for (Integer idOrdine : dto.getIdOrdini()) {
        // documentiDao.associaDoc(idFattura,
        // ISharedConstants.TIPODOCASSOCIATO_FATTURAFORNITORE,
        // idOrdine,
        // ISharedConstants.TIPODOCASSOCIATO_ORDINE);
        // }
        // }
        // if (dto.getIdBolleCarico() != null &&
        // !dto.getIdBolleCarico().isEmpty()) {
        // for (Integer idBollaCarico : dto.getIdBolleCarico()) {
        // documentiDao.associaDoc(idFattura,
        // ISharedConstants.TIPODOCASSOCIATO_FATTURAFORNITORE,
        // idBollaCarico,
        // ISharedConstants.TIPODOCASSOCIATO_BOLLACARICO);
        // }
        // }

        return idFattura;
    }

    public boolean isExistentNumero(Integer numeroDdt,
                                    String particella,
                                    String data,
                                    Integer id) throws SQLException
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        return dao.isExistentNumero(numeroDdt, particella, data, id);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void update(FatturaFornitoreDto dto) throws SQLException
    {
        FattureFornitoreDao documentiDao = new FattureFornitoreDao(jdbcTemplate);
        documentiDao.update(dto);
        documentiDao.deleteProdottiById(dto.getId());
        for ( ProdottoDocumentoDto prodottoDocumentoDto : dto.getProdotti() )
        {
            prodottoDocumentoDto.setIdDocumento(dto.getId());
            documentiDao.insertProdotto(prodottoDocumentoDto);
        }
        documentiDao.deleteSpeseIncassoById(dto.getId());
        if ( dto.getListaSpeseIncassoFattura() != null )
        {
            for ( SpesaIncassoDocumentoDto spesaIncassoDocumentoDto : dto.getListaSpeseIncassoFattura() )
            {
                spesaIncassoDocumentoDto.setIdFattura(dto.getId());
                documentiDao.insertSpesaIncasso(spesaIncassoDocumentoDto);
            }
        }
        documentiDao.deleteScadenzePagamento(dto.getId());
        if ( dto.getListaScadenzePagamentiDocumento() != null )
        {
            for ( ScadenzaPagamentoDocumentoDto scadenzaPagamentoDocumentoDto : dto.getListaScadenzePagamentiDocumento() )
            {
                scadenzaPagamentoDocumentoDto.setIdDocumento(dto.getId());
                documentiDao.insertScadenzaPagamento(scadenzaPagamentoDocumentoDto);
            }
        }
    }

    @Transactional(rollbackFor = SQLException.class)
    public void updateScadenzaPagamento(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        
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

        dao.aggiornaTotaliFatturaFornitore(currentDto.getIdDocumento());
    }

}

