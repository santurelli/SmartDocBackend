package it.tinna.smartdoc.server.delegate.documenti;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.documenti.DdtDao;
import it.tinna.smartdoc.server.delegate.agenti.AgentiDelegate;
import it.tinna.smartdoc.server.delegate.aliquoteiva.AliquoteIvaDelegate;
import it.tinna.smartdoc.server.delegate.clienti.ClientiDelegate;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.server.delegate.listini.ListiniDelegate;
import it.tinna.smartdoc.server.delegate.progetti.ProgettiDelegate;
import it.tinna.smartdoc.server.delegate.risorse.RisorseDelegate;
import it.tinna.smartdoc.server.delegate.tipipagamento.TipiPagamentoDelegate;
import it.tinna.smartdoc.server.delegate.unitamisura.UnitaMisuraDelegate;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.documenti.DdtDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;

@Service
public class DdtDelegate {

    @Autowired
    private DdtDao ddtDao;

    @Autowired
    private ClientiDelegate clientiDelegate;
    
    @Autowired
    private AgentiDelegate agentiDelegate;
    
    @Autowired
    private ProgettiDelegate progettiDelegate;

    @Autowired
    private AliquoteIvaDelegate aliquoteIvaDelegate;

    @Autowired
    private UnitaMisuraDelegate unitaMisuraDelegate;

    @Autowired
    private ConfigurazioneDelegate configurazioneDelegate;

    @Autowired
    private TipiPagamentoDelegate tipiPagamentoDelegate;

    @Autowired
    private ListiniDelegate listiniDelegate;

    @Autowired
    private RisorseDelegate risorseDelegate;

    @Autowired
    private DatiAziendaDelegate datiaziendaDelegate;

    @Transactional(rollbackFor = Exception.class)
    public void delete(long id, long userId) throws SQLException {
        DdtDto dto = new DdtDto();
        dto.setId(id);
        dto.setUserLastUpdate(userId);
        ddtDao.delete(dto);
    }

    public DdtDto getById(long id) throws Exception {
        DdtDto dto = ddtDao.getById(id);
        if (dto != null) {
            dto.setClienteDto(clientiDelegate.getById(dto.getIdCliente()));
            if (dto.getIdProgetto() != null && dto.getIdProgetto() != 0) {
                dto.setProgettoDto(progettiDelegate.getById(dto.getIdProgetto().longValue()));
            }
            if (dto.getIdAgente() != null && dto.getIdAgente() != 0) {
                dto.setAgenteDto(agentiDelegate.getById(dto.getIdAgente()));
            }
            List<ProdottoDocumentoDto> listProdottiDdt = ddtDao.getListProdottiById(dto.getId());
            dto.setProdotti(listProdottiDdt);
            dto.setListaSpeseIncassoFattura(ddtDao.getListSpeseIncassoById(dto.getId()));
        }
        return dto;
    }

    public DatatablesResponseDto<MovimentiDocumentoDto> getList(Integer idCliente,
                                                                 String dtFrom,
                                                                 String dtTo,
                                                                 Integer idAgente,
                                                                 Integer idDocumento,
                                                                 Integer length,
                                                                 Integer start,
                                                                 Integer orderColumn,
                                                                 String orderDir) throws SQLException {
        List<MovimentiDocumentoDto> list = ddtDao.getList(idCliente, dtFrom, dtTo, idAgente, idDocumento, length, start, orderColumn, orderDir);
        long total = 0;
        if (!list.isEmpty()) {
            total = list.get(0).getTotal();
        }
        DatatablesResponseDto<MovimentiDocumentoDto> response = new DatatablesResponseDto<>();
        response.setList(list);
        response.setTotalCount(total);
        response.setTotalFiltered(total);
        return response;
    }

    public Integer getNextNum(String data) throws SQLException {
        return ddtDao.getNextNum(data);
    }

    @Transactional(rollbackFor = Exception.class)
    public long insert(DdtDto dto) throws SQLException {
        long id = ddtDao.insert(dto);
        dto.setId(id);
        if (dto.getProdotti() != null) {
            for (ProdottoDocumentoDto pDto : dto.getProdotti()) {
                pDto.setIdDocumento(id);
                ddtDao.insertProdotto(pDto);
            }
        }
        if (dto.getListaSpeseIncassoFattura() != null) {
            for (SpesaIncassoDocumentoDto sDto : dto.getListaSpeseIncassoFattura()) {
                sDto.setIdFattura(id);
                ddtDao.insertSpesaIncasso(sDto);
            }
        }
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(DdtDto dto) throws SQLException {
        ddtDao.update(dto);
        ddtDao.deleteProdottiById(dto.getId());
        ddtDao.deleteSpeseIncassoById(dto.getId());
        if (dto.getProdotti() != null) {
            for (ProdottoDocumentoDto pDto : dto.getProdotti()) {
                pDto.setIdDocumento(dto.getId());
                ddtDao.insertProdotto(pDto);
            }
        }
        if (dto.getListaSpeseIncassoFattura() != null) {
            for (SpesaIncassoDocumentoDto sDto : dto.getListaSpeseIncassoFattura()) {
                sDto.setIdFattura(dto.getId());
                ddtDao.insertSpesaIncasso(sDto);
            }
        }
    }

    public Map<String, Object> getCombosMap() throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put(ISharedConstants.COMBOSMAP_KEY_ALIQUOTEIVA, aliquoteIvaDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_UNITAMISURA, unitaMisuraDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_TIPIPAGAMENTO, tipiPagamentoDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_LISTINI, listiniDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_RISORSE, risorseDelegate.getListForCombo("BA"));
        map.put(ISharedConstants.COMBOSMAP_KEY_AGENTI, agentiDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_PROGETTI, progettiDelegate.getListForCombo());
        
        // Specific combos for DDT
        map.put("ASPETTIBENI", risorseDelegate.getListForCombo("AB"));
        map.put("CAUSALITRASPORTO", risorseDelegate.getListForCombo("CT"));
        map.put("TIPIPORTO", risorseDelegate.getListForCombo("TP"));
        map.put("VETTORI", risorseDelegate.getListForCombo("VE"));

        String particelleAsString = configurazioneDelegate.getByKey(ISharedConstants.CONFIG_DOMAIN_DOCUMENTI, ISharedConstants.CONFIG_KEY_PARTICELLE);
        if (StringUtils.isNotEmpty(particelleAsString)) {
            map.put(ISharedConstants.COMBOSMAP_KEY_PARTICELLE, StringUtils.split(particelleAsString, "\r\n"));
        } else {
            map.put(ISharedConstants.COMBOSMAP_KEY_PARTICELLE, new String[0]);
        }
        return map;
    }

    public DocumentoWrapperDto esportaDdtPdf(String dbKey, long id) {
        // This will be implemented if needed, similar to PreventiviDelegate.esportaPreventivoPdf
        // For now, I'll return an empty placeholder to keep the code compilable if called.
        DocumentoWrapperDto result = new DocumentoWrapperDto();
        result.setFlusso(new byte[0]);
        result.setNome("ddt_not_implemented.pdf");
        return result;
    }
}
