package it.tinna.smartdoc.server.delegate.primanota;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.server.dao.clienti.ClientiDao;
import it.tinna.smartdoc.server.dao.configurazione.ConfigurazioneDao;
import it.tinna.smartdoc.server.dao.dipendenti.DipendentiDao;
import it.tinna.smartdoc.server.dao.divisioni.DivisioniDao;
import it.tinna.smartdoc.server.dao.fornitori.FornitoriDao;
import it.tinna.smartdoc.server.dao.primanota.PrimaNotaDao;
import it.tinna.smartdoc.server.dao.progetti.ProgettiDao;
import it.tinna.smartdoc.server.dao.risorse.RisorseDao;
import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.clienti.BaseClienteDto;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.divisioni.DivisioneDto;
import it.tinna.smartdoc.shared.dto.primanota.PagamentoPrimaNotaDto;
import it.tinna.smartdoc.shared.dto.primanota.PrimaNotaDto;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service(value = "primanotaDelegate")
public class PrimaNotaDelegate extends BaseDelegate
{
    @Autowired
    private DatiAziendaDelegate datiaziendaDelegate;

    public void deletePagamento(long idPagamento,
                                long idUtente) throws SQLException
    {
        PrimaNotaDao dao = new PrimaNotaDao(jdbcTemplate);
        dao.deletePagamento(idPagamento, idUtente);
    }

    public Map<String, Object> getCombosMap() throws SQLException
    {
        RisorseDao risorseDao = new RisorseDao(jdbcTemplate);
        TipiPagamentoDao tipiPagamentoDao = new TipiPagamentoDao(jdbcTemplate);
        DivisioniDao divisioniDao = new DivisioniDao(jdbcTemplate);

        Map<String, Object> map = new HashMap<>();
        map.put(ISharedConstants.COMBOSMAP_KEY_TIPIPAGAMENTO, tipiPagamentoDao.getListForPrimaNota());
        map.put(ISharedConstants.COMBOSMAP_KEY_RISORSE, risorseDao.getListForCombo(null));
        map.put(ISharedConstants.COMBOSMAP_KEY_DIVISIONI, divisioniDao.getListForCombo());
        return map;
    }

    /**
     * Genera il pdf relativo all'elenco prima nota (viene usato per esportare la lista in pdf oppure per la stampa diretta)
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
                                Integer tipoPagamento,
                                String soggetto,
                                String dtDal,
                                String dtAl,
                                Integer risorsa,
                                String tipologia,
                                long idDivisione) throws Exception
    {
        PrimaNotaDao dao = new PrimaNotaDao(jdbcTemplate);
        List<PrimaNotaDto> list = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        // dati azienda
        DatiAziendaDto daDto = datiaziendaDelegate.getDatiAzienda();
        if ( daDto != null )
        {
            if ( daDto.getByteLogo() != null )
            {
                params.put("logopath", new ByteArrayInputStream(daDto.getByteLogo()));
            }
        }
        params.put("dal", dtDal);
        params.put("al", dtAl);
        if ( idDivisione != 0L )
        {
            DivisioniDao divisioniDao = new DivisioniDao(jdbcTemplate);
            DivisioneDto divisioneDto = divisioniDao.getById(idDivisione);
            if ( divisioneDto != null )
            {
                params.put("descrizioneDivisione", divisioneDto.getValue());
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
            reportIs = FileUtils.openInputStream(new File(new StringBuilder(baseDir).append(ISharedConstants.PRIMANOTA_TEMPLATE_NAME).toString()));
        }
        catch ( IOException e )
        {
            _log.error("Errore nell'apertura dello stream per il template della prima nota", e);
            throw e;
        }
        list = dao.getList(tipoPagamento, soggetto, dtDal, dtAl, risorsa, tipologia, idDivisione, null, null, 0, "asc");
        try
        {
            bytes = JasperRunManager.runReportToPdf(reportIs, params, new JRBeanCollectionDataSource(list));
        }
        catch ( JRException e )
        {
            _log.error("Errore nella generazione della prima nota in formato pdf", e);
            throw e;
        }
        return bytes;
    }

    public List<PrimaNotaDto> getList(Integer tipoPagamento,
                                      String soggetto,
                                      String dtDal,
                                      String dtAl,
                                      Integer risorsa,
                                      String tipologia,
                                      long idDivisione,
                                      Integer length,
                                      Integer start,
                                      Integer orderColumn,
                                      String orderDir) throws SQLException
    {
        PrimaNotaDao dao = new PrimaNotaDao(jdbcTemplate);
        ProgettiDao progettiDao = new ProgettiDao(jdbcTemplate);
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        DipendentiDao dipendentiDao = new DipendentiDao(jdbcTemplate);
        List<PrimaNotaDto> list = dao.getList(tipoPagamento, soggetto, dtDal, dtAl, risorsa, tipologia, idDivisione, length, start, orderColumn, orderDir);
        for ( PrimaNotaDto dto : list )
        {
            if ( dto.getIdProgetto() != null )
            {
                dto.setProgetto(progettiDao.getById(dto.getIdProgetto()));
            }
            if ( StringUtils.isNotBlank(dto.getIdSoggetto()) )
            {
                String[] arr = dto.getIdSoggetto().split("-");
                String tipoSoggetto = arr[0];
                long idSoggetto = Long.parseLong(arr[1]);
                BaseClienteDto bcDto = null;
                if ( tipoSoggetto.equalsIgnoreCase("C") )
                {
                    bcDto = clientiDao.getById(idSoggetto);
                }
                else if ( tipoSoggetto.equalsIgnoreCase("F") )
                {
                    bcDto = fornitoriDao.getById(idSoggetto);
                }
                else
                {
                    bcDto = dipendentiDao.getById(idSoggetto);
                }
                PrimaNotaDto.SoggettoPrimaNotaDto spnDto = dto.new SoggettoPrimaNotaDto();
                spnDto.setId(tipoSoggetto + "-" + bcDto.getId());
                spnDto.setDenominazione(bcDto.getDenominazione());
                spnDto.setCodiceFiscale(bcDto.getCodiceFiscale());
                spnDto.setPartitaIva(bcDto.getPartitaIva());
                dto.setObjSoggetto(spnDto);
            }
        }
        return list;
    }

    public List<PrimaNotaDto> getPagamentiByProgetto(long idProgetto,
                                                     String tipo,
                                                     int length,
                                                     int start,
                                                     int orderColumn,
                                                     String orderDir) throws SQLException
    {
        PrimaNotaDao dao = new PrimaNotaDao(jdbcTemplate);
        List<PrimaNotaDto> list = dao.getPagamentiByProgetto(idProgetto, tipo, length, start, orderColumn, orderDir);
        for ( PrimaNotaDto dto : list )
        {
            if ( StringUtils.isNotBlank(dto.getModalita()) )
            {
                dto.setModalita(ModalitaPagamentoEnum.valueOf(dto.getModalita()).getDescrizione());
            }
        }
        return list;
    }

    public List<BaseClienteDto> getSoggettoSuggestion(String query) throws SQLException
    {
        PrimaNotaDao dao = new PrimaNotaDao(jdbcTemplate);
        return dao.getSoggettoSuggestion(query);
    }

    public void insertPagamento(PagamentoPrimaNotaDto dto) throws SQLException
    {
        PrimaNotaDao dao = new PrimaNotaDao(jdbcTemplate);
        dao.insertPagamento(dto);
    }

    public void updatePagamento(PagamentoPrimaNotaDto dto) throws SQLException
    {
        PrimaNotaDao dao = new PrimaNotaDao(jdbcTemplate);
        dao.updatePagamento(dto);
    }

}

