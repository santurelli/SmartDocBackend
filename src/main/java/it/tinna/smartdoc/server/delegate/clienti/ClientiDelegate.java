package it.tinna.smartdoc.server.delegate.clienti;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.clienti.ClientiDao;
import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
// import it.tinna.smartdoc.server.service.clienti.BaseClienteFornitoreJsonDto;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;

@Service(value = "clientiDelegate")
public class ClientiDelegate extends BaseDelegate
{

    // public ClientiDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    @Transactional(rollbackFor = SQLException.class)
    public void delete(List<ClienteDto> listaClienti) throws SQLException
    {
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        for ( ClienteDto clienteDto : listaClienti )
        {
            clientiDao.delete(clienteDto);
        }
    }

    public String generaCodice() throws SQLException
    {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        return dao.generaCodice();
    }

    public ClienteDto getByDenominazione(String denominazione) throws SQLException
    {
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        ClienteDto dto = clientiDao.getByDenominazione(denominazione);
        if ( dto != null )
        {
            IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
            ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
            dto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(), dto.getId()));
            dto.setElencoContatti(contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(), dto.getId()));
        }
        return dto;
    }

    public ClienteDto getById(Integer id) throws SQLException
    {
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        // IndirizziDao indirizziDao = new IndirizziDao(conn);
        // ContattiDao contattiDao = new ContattiDao(conn);
        ClienteDto dto = clientiDao.getById(id);
        if ( dto != null )
        {
            IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
            ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
            dto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(), dto.getId()));
            dto.setElencoContatti(contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(), dto.getId()));
        }
        return dto;
    }

    public List<ClienteDto> getList(String strToSearch,
                                    Integer length,
                                    Integer start,
                                    Integer orderColumn,
                                    String orderDir) throws SQLException
    {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        List<ClienteDto> listaClienti = dao.getList(strToSearch, length, start, orderColumn, orderDir);
        // IndirizziDao indirizziDao = new IndirizziDao(conn);
        // ContattiDao contattiDao = new ContattiDao(conn);
        // for (ClienteDto dto: listaClienti) {
        // dto.setElencoIndirizzi(indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(),
        // dto.getId()));
        // for (IndirizzoDto iDto: dto.getElencoIndirizzi()) {
        // iDto.setDescrTipologia(IndirizzoDto.TipologiaIndirizzo.getDescrizioneByValue(iDto.getTipologia()).getDescrizione());
        // }
        // dto.setElencoContatti(contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(),
        // dto.getId()));
        // }
        return listaClienti;
    }

    public List<ClienteDto> getListForCombo() throws SQLException
    {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public List<ClienteDto> getSuggestion(String q) throws SQLException
    {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        return dao.getSuggestion(q);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Integer insert(ClienteDto dto,
                          List<IndirizzoDto> indirizziToAdd,
                          List<IndirizzoDto> indirizziToEdit,
                          List<ContattoDto> contattiToAdd,
                          List<ContattoDto> contattiToEdit) throws SQLException
    {
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
        Integer idCliente = clientiDao.insert(dto);
        for ( IndirizzoDto indirizzoDto : indirizziToAdd )
        {
            indirizzoDto.setIdRichiedente(idCliente);
            indirizzoDto.setUserCreated(dto.getUserCreated());
            indirizziDao.insert(IndirizzoDto.Richiedente.CLIENTI.getValore(), indirizzoDto);
        }
        for ( ContattoDto contattoDto : contattiToAdd )
        {
            contattoDto.setIdRichiedente(idCliente);
            contattoDto.setUserCreated(dto.getUserCreated());
            contattiDao.insert(ContattoDto.Richiedente.CLIENTI.getValore(), contattoDto);
        }
        return idCliente;
    }

    public boolean isExistentCodice(String codice,
                                    Integer id) throws SQLException
    {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        return dao.isExistentCodice(codice, id);
    }

    public boolean isExistentDenominazione(String denominazione,
                                           Integer id) throws SQLException
    {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        return dao.isExistentDenominazione(denominazione, id);
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = SQLException.class)
    public void update(ClienteDto dto,
                       List<IndirizzoDto> indirizziToAdd,
                       List<IndirizzoDto> indirizziToEdit,
                       List<ContattoDto> contattiToAdd,
                       List<ContattoDto> contattiToEdit) throws SQLException
    {
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);

        List<IndirizzoDto> indirizzi = indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(), dto.getId());
        List<IndirizzoDto> indirizziToDelete = ListUtils.subtract(indirizzi, dto.getElencoIndirizzi());
        // indirizziToAdd = ListUtils.subtract(indirizziToAdd,
        // indirizziToDelete);
        // indirizziToEdit = ListUtils.subtract(indirizziToEdit,
        // indirizziToDelete);
        // contattiToAdd = ListUtils.subtract(contattiToAdd,
        // contattiToDelete);
        // contattiToEdit = ListUtils.subtract(contattiToEdit,
        // contattiToDelete);
        List<ContattoDto> contatti = contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(), dto.getId());
        List<ContattoDto> contattiToDelete = ListUtils.subtract(contatti, dto.getElencoContatti());

        clientiDao.update(dto);
        // indirizziDao.deleteByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(),
        // dto.getId());
        for ( IndirizzoDto indirizzoDto : indirizziToDelete )
        {
            indirizziDao.deleteByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(), dto.getId(), indirizzoDto.getId());
        }
        for ( IndirizzoDto indirizzoDto : indirizziToEdit )
        {
            indirizzoDto.setIdRichiedente(dto.getId());
            indirizzoDto.setUserLastUpdate(dto.getUserLastUpdate());
            indirizziDao.update(IndirizzoDto.Richiedente.CLIENTI.getValore(), indirizzoDto);
        }
        for ( IndirizzoDto indirizzoDto : indirizziToAdd )
        {
            indirizzoDto.setIdRichiedente(dto.getId());
            indirizzoDto.setUserLastUpdate(dto.getUserLastUpdate());
            indirizziDao.insert(IndirizzoDto.Richiedente.CLIENTI.getValore(), indirizzoDto);
        }
        // for (IndirizzoDto indirizzoDto: listaIndirizzi){
        // if (indirizzoDto.getId() != null){
        // indirizzoDto.setUserCreated(dto.getUserLastUpdate());
        // indirizziDao.update(IndirizzoDto.Richiedente.CLIENTI.getValore(),
        // indirizzoDto);
        // }
        // else{
        // indirizzoDto.setIdRichiedente(dto.getId());
        // indirizzoDto.setUserCreated(dto.getUserLastUpdate());
        // indirizziDao.insert(IndirizzoDto.Richiedente.CLIENTI.getValore(),
        // indirizzoDto);
        // }
        // }
        // contattiDao.deleteByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(),
        // dto.getId());
        for ( ContattoDto contattoDto : contattiToDelete )
        {
            contattiDao.deleteByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(), dto.getId(), contattoDto.getId());
        }
        for ( ContattoDto contattoDto : contattiToEdit )
        {
            contattoDto.setIdRichiedente(dto.getId());
            contattoDto.setUserLastUpdate(dto.getUserLastUpdate());
            contattiDao.update(ContattoDto.Richiedente.CLIENTI.getValore(), contattoDto);
        }
        for ( ContattoDto contattoDto : contattiToAdd )
        {
            contattoDto.setIdRichiedente(dto.getId());
            contattoDto.setUserCreated(dto.getUserLastUpdate());
            contattiDao.insert(ContattoDto.Richiedente.CLIENTI.getValore(), contattoDto);
        }

        // for (ContattoDto contattoDto: listaContatti){
        // if (contattoDto.getId() != null){
        // contattoDto.setUserLastUpdate(dto.getUserLastUpdate());
        // contattiDao.update(ContattoDto.Richiedente.CLIENTI.getValore(),
        // contattoDto);
        // }
        // else{
        // contattoDto.setIdRichiedente(dto.getId());
        // contattoDto.setUserCreated(dto.getUserLastUpdate());
        // contattiDao.insert(ContattoDto.Richiedente.CLIENTI.getValore(),
        // contattoDto);
        // }
        // }
    }

}

