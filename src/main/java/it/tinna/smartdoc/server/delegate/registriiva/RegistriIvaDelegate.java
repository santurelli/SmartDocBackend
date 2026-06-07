package it.tinna.smartdoc.server.delegate.registriiva;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.util.ArrayList;
import java.util.List;
import it.tinna.smartdoc.server.dao.configurazione.ConfigurazioneDao;
import it.tinna.smartdoc.server.dao.datiazienda.DatiAziendaDao;
import it.tinna.smartdoc.server.dao.registriiva.RegistriIvaDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.util.NumberUtils;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.constants.Periodi;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.IvaDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.RegistriIvaListResponse;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

@Transactional(readOnly = true)
@Service(value = "registriIvaDelegate")
public class RegistriIvaDelegate extends BaseDelegate
{

    public byte[] exportProspettoLiquidazione(Periodi periodo,
                                              int anno,
                                              String totIvaVendite,
                                              String totIvaAcquisti,
                                              String acconto,
                                              Double saldoPrecedente,
                                              String ivaDebito,
                                              String percMaggiorazione,
                                              String totMaggiorazione,
                                              String ivaDaVersare,
                                              String ivaCredito) throws Exception
    {
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
        params.put("datiazienda", daDto);
        params.put("periodo", periodo.getDescrizione() + " " + anno);
        params.put("ivaVendite", totIvaVendite);
        params.put("ivaAcquisti", totIvaAcquisti);
        params.put("acconto", acconto);
        params.put("precedenteDebito", saldoPrecedente == null || saldoPrecedente.compareTo(Double.valueOf(0d)) > 1 ? StringUtils.EMPTY : "€ " + NumberUtils.formatAsCurrency(saldoPrecedente * -1));
        params.put("precedenteCredito", saldoPrecedente == null || saldoPrecedente.compareTo(Double.valueOf(0d)) < 1 ? StringUtils.EMPTY : "€ " + NumberUtils.formatAsCurrency(saldoPrecedente));
        params.put("ivaDebito", ivaDebito);
        params.put("maggiorazionePerc", StringUtils.isEmpty(percMaggiorazione) ? StringUtils.EMPTY : NumberUtils.formatAsPercentage(Double.parseDouble(percMaggiorazione)) + " %");
        params.put("maggiorazioneTot", totMaggiorazione);
        params.put("totDaVersare", ivaDaVersare);
        params.put("ivaCredito", ivaCredito);
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
        model.put("DB_KEY", DatabaseContextHolder.getClientDatabase());
        String baseDir = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
        InputStream reportIs = null;
        try
        {
            reportIs = FileUtils.openInputStream(new File(new StringBuilder(baseDir).append(ISharedConstants.PROSPETTO_LIQUIDAZIONE_TEMPLATE_NAME).toString()));
        }
        catch ( IOException e )
        {
            _log.error("Errore nell'apertura dello stream per il template del prospetto liquidazione IVA", e);
            throw e;
        }
        try
        {
            bytes = JasperRunManager.runReportToPdf(reportIs, params, new JREmptyDataSource());
        }
        catch ( JRException e )
        {
            _log.error("Errore nella generazione del prospetto liquidazione IVA", e);
            throw e;
        }
        return bytes;
    }

    public List<Integer> getElencoAnniDocIva() throws SQLException
    {
        RegistriIvaDao dao = new RegistriIvaDao(jdbcTemplate);
        return dao.getElencoAnniDocIva();
    }

    public RegistriIvaListResponse getListIvaDocumenti(Periodi periodo,
                                                       int anno,
                                                       Integer length,
                                                       Integer start,
                                                       Integer orderColumn,
                                                       String orderDir) throws SQLException
    {
        RegistriIvaDao dao = new RegistriIvaDao(jdbcTemplate);
        RegistriIvaListResponse dto = new RegistriIvaListResponse();
        List<IvaDocumentoDto> list = dao.getListIvaDocumenti(periodo, anno, length, start, orderColumn, orderDir);
        dto.setList(list);
        return dto;
    }

    public byte[] exportRegistroIva(Periodi periodo, int anno, String tipoRegistro) throws Exception
    {
        Map<String, Object> params = new HashMap<>();

        // 1. Fetch Dati Azienda
        DatiAziendaDao daDao = new DatiAziendaDao(jdbcTemplate);
        DatiAziendaDto daDto = daDao.getDatiAzienda();
        params.put("datiazienda", daDto);

        // 2. Definizione Periodo
        String periodoStr = periodo.getDescrizione() + " " + anno;
        params.put("periodo", periodoStr);

        // 3. Fetch Documenti
        RegistriIvaDao dao = new RegistriIvaDao(jdbcTemplate);
        List<IvaDocumentoDto> allDocs = dao.getListIvaDocumenti(periodo, anno, null, null, 0, "ASC");

        // 4. Filter by tipoRegistro ("vendite", "acquisti", "differita")
        List<IvaDocumentoDto> filteredDocs = new ArrayList<>();
        double totaleImponibileNum = 0d;
        double totaleIvaNum = 0d;

        int rowNum = 1;

        for (IvaDocumentoDto doc : allDocs)
        {
            boolean matches = false;
            if (doc.getGruppoDocumento() != null) {
                String gruppo = doc.getGruppoDocumento().toLowerCase();
                if ("vendite".equals(tipoRegistro) && gruppo.contains("vendit") && doc.getEsigibilitaDifferita() != 1) {
                    matches = true;
                } else if ("differita".equals(tipoRegistro) && gruppo.contains("vendit") && doc.getEsigibilitaDifferita() == 1) {
                    matches = true;
                } else if ("acquisti".equals(tipoRegistro) && gruppo.contains("acquist")) {
                    matches = true;
                }
            }

            if (matches) {
                doc.setnProgr(rowNum++);
                doc.setDescrTipoDocumento((doc.getTipoDocumento() != null ? doc.getTipoDocumento() : "") + " n. " + 
                                          (doc.getNumeroDocumento() != null ? doc.getNumeroDocumento() : "") + " del " + 
                                          (doc.getDataDocumento() != null ? doc.getDataDocumento() : ""));
                doc.setTotaleFormattato(NumberUtils.formatAsCurrency(doc.getTotale().doubleValue()));
                double ivaVal = "acquisti".equals(tipoRegistro) ? 
                    (doc.getIvaCredito() != null ? doc.getIvaCredito().doubleValue() : doc.getIva()) : 
                    (doc.getIvaDebito() != null ? doc.getIvaDebito().doubleValue() : doc.getIva());
                
                doc.setIvaDebitoFormattato(NumberUtils.formatAsCurrency(ivaVal));
                doc.setIvaCreditoFormattato(NumberUtils.formatAsCurrency(ivaVal));

                // Since we don't fetch granular aliquota details in the main query, wrap it up for the DTO
                filteredDocs.add(doc);
                
                totaleImponibileNum += doc.getTotale().doubleValue();
                totaleIvaNum += ivaVal;
            }
        }

        params.put("totaleImponibile", NumberUtils.formatAsCurrency(totaleImponibileNum));
        params.put("totaleImposta", NumberUtils.formatAsCurrency(totaleIvaNum));

        // 5. Creazione riepilogo generico (visto che i dettagli non ci sono nella query principale)
        List<it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto> riepilogoList = new ArrayList<>();
        it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto riep = new it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto();
        riep.setAliquotaIvaFormattata("Misto");
        riep.setDescrizioneAliquota("Consultare i documenti");
        riep.setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(totaleImponibileNum));
        riep.setImportoIvaFormattato(NumberUtils.formatAsCurrency(totaleIvaNum));
        riepilogoList.add(riep);
        
        params.put("riepilogo", riepilogoList);

        // 6. Caricamento del template dal Classpath (invece che dal file system legacy)
        String templateName = "acquisti".equals(tipoRegistro) ? "iva_acquisti.jrxml" : "iva_vendite.jrxml";
        InputStream reportIs = null;
        try
        {
            org.springframework.core.io.ClassPathResource resource = new org.springframework.core.io.ClassPathResource("report_stampa/" + templateName);
            if (!resource.exists()) {
                _log.error("Template di stampa non trovato in classpath: report_stampa/" + templateName);
                throw new Exception("Template non trovato");
            }
            reportIs = resource.getInputStream();
        }
        catch ( IOException e )
        {
            _log.error("Errore nell'apertura dello stream per il template", e);
            throw e;
        }

        byte[] bytes = null;
        try
        {
            net.sf.jasperreports.engine.data.JRBeanCollectionDataSource ds = new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(filteredDocs);
            
            // If the report is a .jrxml, we must compile it on the fly. JasperRunManager mostly expects .jasper. Let's use JasperCompileManager if it's .jrxml
            if (templateName.endsWith(".jrxml")) {
                net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(reportIs);
                bytes = net.sf.jasperreports.engine.JasperRunManager.runReportToPdf(jasperReport, params, ds);
            } else {
                bytes = net.sf.jasperreports.engine.JasperRunManager.runReportToPdf(reportIs, params, ds);
            }
        }
        catch ( JRException e )
        {
            _log.error("Errore nella generazione del report IVA", e);
            throw e;
        }

        return bytes;
    }

}

