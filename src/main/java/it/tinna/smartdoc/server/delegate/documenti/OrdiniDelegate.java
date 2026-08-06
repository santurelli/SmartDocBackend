package it.tinna.smartdoc.server.delegate.documenti;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.dao.aspettobeni.AspettoBeniDao;
import it.tinna.smartdoc.server.dao.causalitrasporto.CausaliTrasportoDao;
import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.server.dao.documenti.OrdiniDao;
import it.tinna.smartdoc.server.dao.fornitori.FornitoriDao;
import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.server.dao.risorse.RisorseDao;
import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.server.dao.tipiporto.TipiPortoDao;
import it.tinna.smartdoc.server.dao.unitamisura.UnitaMisuraDao;
import it.tinna.smartdoc.server.dao.vettori.VettoriDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.documenti.OrdineDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.risorse.RisorsaDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;

@Transactional(readOnly = true)
@Service("ordiniDelegate")
public class OrdiniDelegate extends BaseDelegate
{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public DatatablesResponseDto<OrdineDto> getList(String dtFrom,
                                                     String dtTo,
                                                     String soggetto,
                                                     Integer length,
                                                     Integer start,
                                                     String orderColumn,
                                                     String orderDir) throws SQLException
    {
        OrdiniDao dao = new OrdiniDao(jdbcTemplate);
        List<OrdineDto> list = dao.getList(dtFrom, dtTo, soggetto, length, start, orderColumn, orderDir);
        long total = 0;
        if ( !list.isEmpty() )
        {
            total = list.get(0).getTotal();
        }
        DatatablesResponseDto<OrdineDto> response = new DatatablesResponseDto<>();
        response.setList(list);
        response.setTotalCount(total);
        response.setTotalFiltered(total);
        return response;
    }

    public OrdineDto getById(long id) throws SQLException
    {
        OrdiniDao dao = new OrdiniDao(jdbcTemplate);
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        OrdineDto dto = dao.getById(id);
        if ( dto != null )
        {
            FornitoreDto fornitoreDto = fornitoriDao.getById(dto.getIdFornitore());
            if ( fornitoreDto != null )
            {
                IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
                ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
                fornitoreDto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getIdFornitore()));
                fornitoreDto.setElencoContatti(contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), dto.getIdFornitore()));
                dto.setFornitoreDto(fornitoreDto);
            }
            dto.setProdotti(dao.getListProdottiById(dto.getId()));
            dto.setListaSpeseIncassoFattura(dao.getListSpeseIncassoById(dto.getId()));
            dto.setListaScadenzePagamentiDocumento(dao.getScadenzePagamento(dto.getId()));
        }
        return dto;
    }

    @Transactional(rollbackFor = SQLException.class)
    public Integer insert(OrdineDto dto) throws SQLException
    {
        OrdiniDao dao = new OrdiniDao(jdbcTemplate);
        Integer id = dao.insert(dto);
        salvaRigheCollegate(dao, dto, id);
        return id;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void update(OrdineDto dto) throws SQLException
    {
        OrdiniDao dao = new OrdiniDao(jdbcTemplate);
        dao.update(dto);
        dao.deleteProdottiById(dto.getId());
        dao.deleteSpeseIncassoById(dto.getId());
        dao.deleteScadenzePagamento(dto.getId());
        salvaRigheCollegate(dao, dto, (int) dto.getId());
    }

    private void salvaRigheCollegate(OrdiniDao dao,
                                     OrdineDto dto,
                                     int idOrdine) throws SQLException
    {
        if ( dto.getProdotti() != null )
        {
            for ( ProdottoDocumentoDto prodottoDto : dto.getProdotti() )
            {
                prodottoDto.setIdDocumento(idOrdine);
                dao.insertProdotto(prodottoDto);
            }
        }
        if ( dto.getListaScadenzePagamentiDocumento() != null )
        {
            for ( ScadenzaPagamentoDocumentoDto scadenzaDto : dto.getListaScadenzePagamentiDocumento() )
            {
                scadenzaDto.setIdDocumento(idOrdine);
                dao.insertScadenzaPagamento(scadenzaDto);
            }
        }
    }

    @Transactional(rollbackFor = SQLException.class)
    public void delete(OrdineDto dto) throws SQLException
    {
        OrdiniDao dao = new OrdiniDao(jdbcTemplate);
        dao.delete(dto);
    }

    public boolean isExistentNumero(Integer numero,
                                    String particella,
                                    String data,
                                    Integer id) throws SQLException
    {
        OrdiniDao dao = new OrdiniDao(jdbcTemplate);
        return dao.isExistentNumero(numero, particella, data, id);
    }

    public Integer getNextNum(String data) throws SQLException
    {
        OrdiniDao dao = new OrdiniDao(jdbcTemplate);
        return dao.getNextNum(data);
    }

    /**
     * Elenco ordini non ancora completamente ricevuti per un fornitore, usato per
     * precompilare una bolla di carico o una fattura fornitore a partire da un ordine aperto.
     */
    public List<OrdineDto> getOrdiniAperti(long idFornitore) throws SQLException
    {
        OrdiniDao dao = new OrdiniDao(jdbcTemplate);
        return dao.getOrdiniAperti(idFornitore);
    }

    public Map<String, Object> getCombosMap() throws SQLException
    {
        AliquoteIvaDao aliquoteIvaDao = new AliquoteIvaDao(jdbcTemplate);
        UnitaMisuraDao unitaMisuraDao = new UnitaMisuraDao(jdbcTemplate);
        TipiPagamentoDao tipiPagamentoDao = new TipiPagamentoDao(jdbcTemplate);
        RisorseDao risorseDao = new RisorseDao(jdbcTemplate);
        AspettoBeniDao aspettoBeniDao = new AspettoBeniDao(jdbcTemplate);
        CausaliTrasportoDao causaliTrasportoDao = new CausaliTrasportoDao(jdbcTemplate);
        TipiPortoDao tipiPortoDao = new TipiPortoDao(jdbcTemplate);
        VettoriDao vettoriDao = new VettoriDao(jdbcTemplate);

        Map<String, Object> map = new HashMap<>();
        map.put(ISharedConstants.COMBOSMAP_KEY_ALIQUOTEIVA, aliquoteIvaDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_UNITAMISURA, unitaMisuraDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_TIPIPAGAMENTO, tipiPagamentoDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_BANCHE, risorseDao.getListForCombo(RisorsaDto.Tipologia.BANCA.getValore()));
        map.put("ASPETTIBENI", aspettoBeniDao.getListForCombo());
        map.put("CAUSALITRASPORTO", causaliTrasportoDao.getListForCombo());
        map.put("TIPIPORTO", tipiPortoDao.getListForCombo());
        map.put("VETTORI", vettoriDao.getListForCombo());
        return map;
    }

}
