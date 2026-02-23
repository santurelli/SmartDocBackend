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

}

