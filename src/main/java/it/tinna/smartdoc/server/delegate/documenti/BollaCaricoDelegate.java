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
import it.tinna.smartdoc.server.dao.documenti.BollaCaricoDao;
import it.tinna.smartdoc.server.dao.documenti.DocumentiDao;
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
import it.tinna.smartdoc.shared.dto.documenti.BollaCaricoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.risorse.RisorsaDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;

@Transactional(readOnly = true)
@Service("bollaCaricoDelegate")
public class BollaCaricoDelegate extends BaseDelegate
{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public DatatablesResponseDto<BollaCaricoDto> getList(String dtFrom,
                                                          String dtTo,
                                                          String soggetto,
                                                          String numDocFornitore,
                                                          Integer length,
                                                          Integer start,
                                                          String orderColumn,
                                                          String orderDir) throws SQLException
    {
        BollaCaricoDao dao = new BollaCaricoDao(jdbcTemplate);
        List<BollaCaricoDto> list = dao.getList(dtFrom, dtTo, soggetto, numDocFornitore, length, start, orderColumn, orderDir);
        long total = 0;
        if ( !list.isEmpty() )
        {
            total = list.get(0).getTotal();
        }
        DatatablesResponseDto<BollaCaricoDto> response = new DatatablesResponseDto<>();
        response.setList(list);
        response.setTotalCount(total);
        response.setTotalFiltered(total);
        return response;
    }

    public BollaCaricoDto getById(long id) throws SQLException
    {
        BollaCaricoDao dao = new BollaCaricoDao(jdbcTemplate);
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        BollaCaricoDto dto = dao.getById(id);
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

    @Transactional(rollbackFor = Exception.class)
    public Integer insert(BollaCaricoDto dto) throws SQLException
    {
        BollaCaricoDao dao = new BollaCaricoDao(jdbcTemplate);
        Integer id = dao.insert(dto);
        salvaRigheCollegate(dao, dto, id);
        associaOrdini(dto, id);
        // Nota: NON inseriamo un movimento esplicito in d_e_movimenti_magazzino qui: get_totale_disponibile()
        // legge gia' direttamente le righe di d_e_prodotti_bollecarico, quindi un movimento esplicito
        // causerebbe un doppio conteggio.
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(BollaCaricoDto dto) throws SQLException
    {
        BollaCaricoDao dao = new BollaCaricoDao(jdbcTemplate);
        dao.update(dto);
        dao.deleteProdottiById(dto.getId());
        dao.deleteSpeseIncassoById(dto.getId());
        dao.deleteScadenzePagamento(dto.getId());
        salvaRigheCollegate(dao, dto, (int) dto.getId());
        associaOrdini(dto, (int) dto.getId());
        // Nota: in caso di modifica righe, i movimenti di magazzino gia' registrati per la
        // versione precedente della bolla NON vengono retrattati automaticamente (evitiamo di
        // toccare in automatico giacenze gia' eventualmente movimentate a valle). Il carico
        // magazzino avviene quindi solo in inserimento, non in modifica.
    }

    private void salvaRigheCollegate(BollaCaricoDao dao,
                                     BollaCaricoDto dto,
                                     int idBollaCarico) throws SQLException
    {
        if ( dto.getProdotti() != null )
        {
            for ( ProdottoDocumentoDto prodottoDto : dto.getProdotti() )
            {
                prodottoDto.setIdDocumento(idBollaCarico);
                dao.insertProdotto(prodottoDto);
            }
        }
        if ( dto.getListaScadenzePagamentiDocumento() != null )
        {
            for ( ScadenzaPagamentoDocumentoDto scadenzaDto : dto.getListaScadenzePagamentiDocumento() )
            {
                scadenzaDto.setIdDocumento(idBollaCarico);
                dao.insertScadenzaPagamento(scadenzaDto);
            }
        }
    }

    private void associaOrdini(BollaCaricoDto dto,
                               int idBollaCarico) throws SQLException
    {
        if ( dto.getIdOrdine() != null && !dto.getIdOrdine().isEmpty() )
        {
            DocumentiDao documentiDao = new DocumentiDao(jdbcTemplate);
            for ( Integer idOrdine : dto.getIdOrdine() )
            {
                documentiDao.associaDoc(idBollaCarico, ISharedConstants.TIPODOCASSOCIATO_BOLLACARICO, idOrdine, ISharedConstants.TIPODOCASSOCIATO_ORDINE);
            }
        }
    }

    @Transactional(rollbackFor = SQLException.class)
    public void delete(BollaCaricoDto dto) throws SQLException
    {
        BollaCaricoDao dao = new BollaCaricoDao(jdbcTemplate);
        dao.delete(dto);
    }

    public boolean isExistentNumero(Integer numero,
                                    String particella,
                                    String data,
                                    Integer id) throws SQLException
    {
        BollaCaricoDao dao = new BollaCaricoDao(jdbcTemplate);
        return dao.isExistentNumero(numero, particella, data, id);
    }

    public Integer getNextNum(String data) throws SQLException
    {
        BollaCaricoDao dao = new BollaCaricoDao(jdbcTemplate);
        return dao.getNextNum(data);
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
