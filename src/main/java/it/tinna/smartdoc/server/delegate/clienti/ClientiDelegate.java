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
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;

@Service(value = "clientiDelegate")
public class ClientiDelegate extends BaseDelegate {

    @Transactional(rollbackFor = SQLException.class)
    public void delete(List<ClienteDto> listaClienti) throws SQLException {
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        for (ClienteDto clienteDto : listaClienti) {
            clientiDao.delete(clienteDto);
        }
    }

    public String generaCodice() throws SQLException {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        return dao.generaCodice();
    }

    public ClienteDto getByDenominazione(String denominazione) throws SQLException {
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        ClienteDto dto = clientiDao.getByDenominazione(denominazione);
        if (dto != null) {
            IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
            ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
            dto.setElencoIndirizzi(
                    indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(), dto.getId()));
            dto.setElencoContatti(
                    contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(), dto.getId()));
        }
        return dto;
    }

    public ClienteDto getById(Integer id) throws SQLException {
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        ClienteDto dto = clientiDao.getById(id);
        if (dto != null) {
            IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
            ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
            dto.setElencoIndirizzi(
                    indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(), dto.getId()));
            dto.setElencoContatti(
                    contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(), dto.getId()));
        }
        return dto;
    }

    public List<ClienteDto> getList(String strToSearch,
            Integer length,
            Integer start,
            Integer orderColumn,
            String orderDir) throws SQLException {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        List<ClienteDto> listaClienti = dao.getList(strToSearch, length, start, orderColumn, orderDir);
        return listaClienti;
    }

    public List<ClienteDto> getListForCombo() throws SQLException {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public List<ClienteDto> getSuggestion(String q) throws SQLException {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        return dao.getSuggestion(q);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Integer insert(ClienteDto dto) throws SQLException {
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
        Integer idCliente = clientiDao.insert(dto);

        if (dto.getElencoIndirizzi() != null) {
            for (IndirizzoDto indirizzoDto : dto.getElencoIndirizzi()) {
                indirizzoDto.setIdRichiedente(idCliente);
                indirizzoDto.setUserCreated(dto.getUserCreated());
                indirizziDao.insert(IndirizzoDto.Richiedente.CLIENTI.getValore(), indirizzoDto);
            }
        }

        if (dto.getElencoContatti() != null) {
            for (ContattoDto contattoDto : dto.getElencoContatti()) {
                contattoDto.setIdRichiedente(idCliente);
                contattoDto.setUserCreated(dto.getUserCreated());
                contattiDao.insert(ContattoDto.Richiedente.CLIENTI.getValore(), contattoDto);
            }
        }
        return idCliente;
    }

    public boolean isExistentCodice(String codice, Integer id) throws SQLException {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        return dao.isExistentCodice(codice, id);
    }

    public boolean isExistentDenominazione(String denominazione, Integer id) throws SQLException {
        ClientiDao dao = new ClientiDao(jdbcTemplate);
        return dao.isExistentDenominazione(denominazione, id);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void update(ClienteDto dto) throws SQLException {
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);

        // Update main Client entity
        clientiDao.update(dto);

        // Handle Indirizzi
        List<IndirizzoDto> currentIndirizzi = indirizziDao
                .getListByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(), dto.getId());
        List<IndirizzoDto> newIndirizzi = dto.getElencoIndirizzi() != null ? dto.getElencoIndirizzi()
                : List.of();

        // 1. Delete removed addresses
        // Using Apache Commons ListUtils.subtract or manual stream logging
        // We delete items that are in 'current' but not in 'new' (based on equals/ID)
        List<IndirizzoDto> addressesToDelete = ListUtils.subtract(currentIndirizzi, newIndirizzi);
        for (IndirizzoDto addr : addressesToDelete) {
            indirizziDao.deleteByIdRichiedente(IndirizzoDto.Richiedente.CLIENTI.getValore(), dto.getId(), addr.getId());
        }

        // 2. Insert/Update
        for (IndirizzoDto addr : newIndirizzi) {
            if (addr.getId() <= 0) {
                // Insert
                addr.setIdRichiedente(dto.getId());
                addr.setUserCreated(dto.getUserLastUpdate()); // Use userLastUpdate as creator for new items during edit
                indirizziDao.insert(IndirizzoDto.Richiedente.CLIENTI.getValore(), addr);
            } else {
                // Update
                addr.setIdRichiedente(dto.getId());
                addr.setUserLastUpdate(dto.getUserLastUpdate());
                indirizziDao.update(IndirizzoDto.Richiedente.CLIENTI.getValore(), addr);
            }
        }

        // Handle Contatti
        List<ContattoDto> currentContatti = contattiDao
                .getListByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(), dto.getId());
        List<ContattoDto> newContatti = dto.getElencoContatti() != null ? dto.getElencoContatti() : List.of();

        // 1. Delete removed contacts
        List<ContattoDto> contactsToDelete = ListUtils.subtract(currentContatti, newContatti);
        for (ContattoDto contact : contactsToDelete) {
            contattiDao.deleteByIdRichiedente(ContattoDto.Richiedente.CLIENTI.getValore(), dto.getId(),
                    contact.getId());
        }

        // 2. Insert/Update
        for (ContattoDto contact : newContatti) {
            if (contact.getId() <= 0) {
                // Insert
                contact.setIdRichiedente(dto.getId());
                contact.setUserCreated(dto.getUserLastUpdate());
                contattiDao.insert(ContattoDto.Richiedente.CLIENTI.getValore(), contact);
            } else {
                // Update
                contact.setIdRichiedente(dto.getId());
                contact.setUserLastUpdate(dto.getUserLastUpdate());
                contattiDao.update(ContattoDto.Richiedente.CLIENTI.getValore(), contact);
            }
        }
    }
}
