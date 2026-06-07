package it.tinna.smartdoc.server.delegate.fornitori;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.avvisi.AvvisiDao;
import it.tinna.smartdoc.server.dao.categoriespesa.CategorieSpesaDao;
import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.server.dao.fornitori.FornitoriDao;
import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.server.dao.notedocumenti.NoteDocumentiDao;
import it.tinna.smartdoc.server.dao.risorse.RisorseDao;
import it.tinna.smartdoc.server.dao.tipiporto.TipiPortoDao;
import it.tinna.smartdoc.server.dao.vettori.VettoriDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;
import it.tinna.smartdoc.shared.dto.risorse.RisorsaDto;

@Service(value = "fornitoriDelegate")
@Transactional(readOnly = true)
public class FornitoriDelegate extends BaseDelegate
{

//	public FornitoriDelegate(JdbcTemplate jdbcTemplate) {
//		super(jdbcTemplate);
//	}

    @Transactional(rollbackFor = SQLException.class)
    public void delete(List<FornitoreDto> listaFornitori) throws SQLException
    {
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        for ( FornitoreDto fornitoreDto : listaFornitori )
        {
            fornitoriDao.delete(fornitoreDto);
        }
    }

    public String generaCodice() throws SQLException
    {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        return dao.generaCodice();
    }

    public FornitoreDto getByDenominazione(String denominazione) throws SQLException
    {
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
        FornitoreDto dto = fornitoriDao.getByDenominazione(denominazione);
        if ( dto != null )
        {
            dto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getId()));
            dto.setElencoContatti(contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), dto.getId()));
        }
        return dto;
    }

    public FornitoreDto getById(Integer id) throws SQLException
    {
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
        FornitoreDto dto = fornitoriDao.getById(id);
        dto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), id));
        dto.setElencoContatti(contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), id));
        return dto;
    }

    public FornitoreDto getByPartitaIva(String partitaIva) throws SQLException
    {
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
        FornitoreDto dto = fornitoriDao.getByPartitaIva(partitaIva);
        if ( dto != null )
        {
            dto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getId()));
            dto.setElencoContatti(contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), dto.getId()));
        }
        return dto;
    }

    // public List<BaseClienteFornitoreJsonDto> getSuggestion(String query)
    // throws Exception {
    // Connection conn = null;
    // try {
    // conn = PooledCnn.getSingleton(dbKey);
    // FornitoriDao dao = new FornitoriDao(conn);
    // return dao.getSuggestion(query);
    // } finally {
    // PooledCnn.close(conn);
    // }
    // }

    public Map<String, Object> getCombosMap() throws SQLException
    {
        RisorseDao risorseDao = new RisorseDao(jdbcTemplate);
        CategorieSpesaDao categorieSpesaDao = new CategorieSpesaDao(jdbcTemplate);
        AvvisiDao avvisiDao = new AvvisiDao(jdbcTemplate);
        VettoriDao vettoriDao = new VettoriDao(jdbcTemplate);
        NoteDocumentiDao noteDocumentiDao = new NoteDocumentiDao(jdbcTemplate);
        TipiPortoDao tipiPortoDao = new TipiPortoDao(jdbcTemplate);

        Map<String, Object> map = new HashMap<>();
        map.put(ISharedConstants.COMBOSMAP_KEY_RISORSE, risorseDao.getListForCombo(RisorsaDto.Tipologia.BANCA.getValore()));
        map.put(ISharedConstants.COMBOSMAP_KEY_CATEGORIESPESA, categorieSpesaDao.getList(null));
        map.put(ISharedConstants.COMBOSMAP_KEY_VETTORI, vettoriDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_TIPIPORTO, tipiPortoDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_AVVISIDOCUMENTI, avvisiDao.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_NOTEDOCUMENTI, noteDocumentiDao.getList(null, null, null, null, null));
        return map;
    }

    public List<FornitoreDto> getList(String strToSearch,
                                      Integer length,
                                      Integer start,
                                      Integer orderColumn,
                                      String orderDir) throws SQLException
    {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        List<FornitoreDto> listaFornitori = dao.getList(strToSearch, length, start, orderColumn, orderDir);
        return listaFornitori;
    }

    public List<FornitoreDto> getListForCombo() throws SQLException
    {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        List<FornitoreDto> listaFornitori = dao.getListForCombo();
        return listaFornitori;
    }

    public List<FornitoreDto> getSuggestion(String query) throws SQLException
    {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        List<FornitoreDto> list = dao.getSuggestion(query);
        for ( FornitoreDto dto : list )
        {
            dto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getId()));
        }
        return list;
    }

    @Transactional(rollbackFor = SQLException.class)
    public long insert(FornitoreDto dto,
                       List<IndirizzoDto> indirizziToAdd,
                       List<IndirizzoDto> indirizziToEdit,
                       List<ContattoDto> contattiToAdd,
                       List<ContattoDto> contattiToEdit) throws SQLException
    {
        // indirizziToAdd = ListUtils.subtract(indirizziToAdd,
        // indirizziToDelete);
        // indirizziToEdit = ListUtils.subtract(indirizziToEdit,
        // indirizziToDelete);
        // contattiToAdd = ListUtils.subtract(contattiToAdd,
        // contattiToDelete);
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
        long idFornitore = fornitoriDao.insert(dto);
        for ( IndirizzoDto indirizzoDto : indirizziToAdd )
        {
            indirizzoDto.setIdRichiedente(idFornitore);
            indirizzoDto.setUserCreated(dto.getUserCreated());
            indirizziDao.insert(IndirizzoDto.Richiedente.FORNITORI.getValore(), indirizzoDto);
        }
        for ( ContattoDto contattoDto : contattiToAdd )
        {
            contattoDto.setIdRichiedente(idFornitore);
            contattoDto.setUserCreated(dto.getUserCreated());
            contattiDao.insert(ContattoDto.Richiedente.FORNITORI.getValore(), contattoDto);
        }
        // for (ContattoDto contattoDto: listaContatti){
        // contattoDto.setIdRichiedente(idFornitore);
        // contattoDto.setUserCreated(dto.getUserCreated());
        // contattiDao.insert(ContattoDto.Richiedente.FORNITORI.getValore(),
        // contattoDto);
        // }
        return idFornitore;
    }

    public boolean isExistentCodice(String codice,
                                    Integer id) throws SQLException
    {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        return dao.isExistentCodice(codice, id);
    }

    public boolean isExistentDenominazione(String denominazione,
                                           Integer id) throws SQLException
    {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        return dao.isExistentDenominazione(denominazione, id);
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = SQLException.class)
    public void update(FornitoreDto dto,
                       List<IndirizzoDto> indirizziToAdd,
                       /* List<IndirizzoDto> indirizziToDelete, */ List<IndirizzoDto> indirizziToEdit,
                       List<ContattoDto> contattiToAdd,
                       /* List<ContattoDto> contattiToDelete, */ List<ContattoDto> contattiToEdit) throws SQLException
    {
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);

        List<IndirizzoDto> indirizzi = indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getId());
        List<IndirizzoDto> indirizziToDelete = ListUtils.subtract(indirizzi, dto.getElencoIndirizzi());
        // indirizziToAdd = ListUtils.subtract(indirizziToAdd,
        // indirizziToDelete);
        // indirizziToEdit = ListUtils.subtract(indirizziToEdit,
        // indirizziToDelete);
        List<ContattoDto> contatti = contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), dto.getId());
        List<ContattoDto> contattiToDelete = ListUtils.subtract(contatti, dto.getElencoContatti());
        // contattiToAdd = ListUtils.subtract(contattiToAdd,
        // contattiToDelete);
        // contattiToEdit = ListUtils.subtract(contattiToEdit,
        // contattiToDelete);

        fornitoriDao.update(dto);
        for ( IndirizzoDto indirizzoDto : indirizziToDelete )
        {
            indirizziDao.deleteByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getId(), indirizzoDto.getId());
        }
        // indirizziDao.deleteByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(),
        // dto.getId());
        for ( IndirizzoDto indirizzoDto : indirizziToEdit )
        {
            indirizzoDto.setIdRichiedente(dto.getId());
            indirizzoDto.setUserLastUpdate(dto.getUserLastUpdate());
            indirizziDao.update(IndirizzoDto.Richiedente.FORNITORI.getValore(), indirizzoDto);
        }
        for ( IndirizzoDto indirizzoDto : indirizziToAdd )
        {
            indirizzoDto.setIdRichiedente(dto.getId());
            indirizzoDto.setUserCreated(dto.getUserLastUpdate());
            indirizziDao.insert(IndirizzoDto.Richiedente.FORNITORI.getValore(), indirizzoDto);
        }
        // for (IndirizzoDto indirizzoDto: listaIndirizzi){
        // if (indirizzoDto.getId() != null){
        // indirizzoDto.setUserLastUpdate(dto.getUserLastUpdate());
        // indirizziDao.update(IndirizzoDto.Richiedente.FORNITORI.getValore(),
        // indirizzoDto);
        // }
        // else{
        // indirizzoDto.setIdRichiedente(dto.getId());
        // indirizzoDto.setUserCreated(dto.getUserLastUpdate());
        // indirizziDao.insert(IndirizzoDto.Richiedente.FORNITORI.getValore(),
        // indirizzoDto);
        // }
        // }
        for ( ContattoDto contattoDto : contattiToDelete )
        {
            contattiDao.deleteByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), dto.getId(), contattoDto.getId());
        }
        for ( ContattoDto contattoDto : contattiToEdit )
        {
            contattoDto.setIdRichiedente(dto.getId());
            contattoDto.setUserLastUpdate(dto.getUserLastUpdate());
            contattiDao.update(ContattoDto.Richiedente.FORNITORI.getValore(), contattoDto);
        }
        for ( ContattoDto contattoDto : contattiToAdd )
        {
            contattoDto.setIdRichiedente(dto.getId());
            contattoDto.setUserCreated(dto.getUserLastUpdate());
            contattiDao.insert(ContattoDto.Richiedente.FORNITORI.getValore(), contattoDto);
        }
        // contattiDao.deleteByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(),
        // dto.getId());
        // for (ContattoDto contattoDto: listaContatti){
        // if (contattoDto.getId() != null){
        // contattoDto.setUserLastUpdate(dto.getUserLastUpdate());
        // contattiDao.update(ContattoDto.Richiedente.FORNITORI.getValore(),
        // contattoDto);
        // }
        // else{
        // contattoDto.setIdRichiedente(dto.getId());
        // contattoDto.setUserCreated(dto.getUserLastUpdate());
        // contattiDao.insert(ContattoDto.Richiedente.FORNITORI.getValore(),
        // contattoDto);
        // }
        // }
    }

}

