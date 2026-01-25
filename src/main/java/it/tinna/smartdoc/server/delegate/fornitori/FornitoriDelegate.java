package it.tinna.smartdoc.server.delegate.fornitori;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.server.dao.fornitori.FornitoriDao;
import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;

@Service(value = "fornitoriDelegate")
public class FornitoriDelegate extends BaseDelegate {

    @Transactional(rollbackFor = SQLException.class)
    public void delete(List<FornitoreDto> listaFornitori) throws SQLException {
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        for (FornitoreDto fornitoreDto : listaFornitori) {
            fornitoriDao.delete(fornitoreDto);
        }
    }

    public String generaCodice() throws SQLException {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        return dao.generaCodice();
    }

    public FornitoreDto getByDenominazione(String denominazione) throws SQLException {
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        FornitoreDto dto = fornitoriDao.getByDenominazione(denominazione);
        if (dto != null) {
            IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
            ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
            dto.setElencoIndirizzi(
                    indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getId()));
            dto.setElencoContatti(
                    contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), dto.getId()));
        }
        return dto;
    }

    public FornitoreDto getById(Integer id) throws SQLException {
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        FornitoreDto dto = fornitoriDao.getById(id);
        if (dto != null) {
            IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
            ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
            dto.setElencoIndirizzi(
                    indirizziDao.getListByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getId()));
            dto.setElencoContatti(
                    contattiDao.getListByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), dto.getId()));
        }
        return dto;
    }

    public List<FornitoreDto> getList(String strToSearch,
            Integer length,
            Integer start,
            Integer orderColumn,
            String orderDir) throws SQLException {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        List<FornitoreDto> list = dao.getList(strToSearch, length, start, orderColumn, orderDir);
        return list;
    }

    public List<FornitoreDto> getListForCombo() throws SQLException {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public List<FornitoreDto> getSuggestion(String q) throws SQLException {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        return dao.getSuggestion(q);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Integer insert(FornitoreDto dto) throws SQLException {
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);
        long idFornitoreLong = fornitoriDao.insert(dto); // DAO returns long
        Integer idFornitore = (int) idFornitoreLong;

        if (dto.getElencoIndirizzi() != null) {
            for (IndirizzoDto indirizzoDto : dto.getElencoIndirizzi()) {
                indirizzoDto.setIdRichiedente(idFornitore);
                indirizzoDto.setUserCreated(dto.getUserCreated());
                indirizziDao.insert(IndirizzoDto.Richiedente.FORNITORI.getValore(), indirizzoDto);
            }
        }

        if (dto.getElencoContatti() != null) {
            for (ContattoDto contattoDto : dto.getElencoContatti()) {
                contattoDto.setIdRichiedente(idFornitore);
                contattoDto.setUserCreated(dto.getUserCreated());
                contattiDao.insert(ContattoDto.Richiedente.FORNITORI.getValore(), contattoDto);
            }
        }
        return idFornitore;
    }

    public boolean isExistentCodice(String codice, Integer id) throws SQLException {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        return dao.isExistentCodice(codice, id);
    }

    public boolean isExistentDenominazione(String denominazione, Integer id) throws SQLException {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        return dao.isExistentDenominazione(denominazione, id);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void update(FornitoreDto dto) throws SQLException {
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        IndirizziDao indirizziDao = new IndirizziDao(jdbcTemplate);
        ContattiDao contattiDao = new ContattiDao(jdbcTemplate);

        // Update main Client entity
        fornitoriDao.update(dto);

        // Handle Indirizzi
        List<IndirizzoDto> currentIndirizzi = indirizziDao
                .getListByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getId());
        List<IndirizzoDto> newIndirizzi = dto.getElencoIndirizzi() != null ? dto.getElencoIndirizzi()
                : List.of();

        List<IndirizzoDto> addressesToDelete = ListUtils.subtract(currentIndirizzi, newIndirizzi);
        for (IndirizzoDto addr : addressesToDelete) {
            indirizziDao.deleteByIdRichiedente(IndirizzoDto.Richiedente.FORNITORI.getValore(), dto.getId(), addr.getId());
        }

        for (IndirizzoDto addr : newIndirizzi) {
            if (addr.getId() <= 0) {
                addr.setIdRichiedente(dto.getId());
                addr.setUserCreated(dto.getUserLastUpdate()); 
                indirizziDao.insert(IndirizzoDto.Richiedente.FORNITORI.getValore(), addr);
            } else {
                addr.setIdRichiedente(dto.getId());
                addr.setUserLastUpdate(dto.getUserLastUpdate());
                indirizziDao.update(IndirizzoDto.Richiedente.FORNITORI.getValore(), addr);
            }
        }

        // Handle Contatti
        List<ContattoDto> currentContatti = contattiDao
                .getListByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), dto.getId());
        List<ContattoDto> newContatti = dto.getElencoContatti() != null ? dto.getElencoContatti() : List.of();

        List<ContattoDto> contactsToDelete = ListUtils.subtract(currentContatti, newContatti);
        for (ContattoDto contact : contactsToDelete) {
            contattiDao.deleteByIdRichiedente(ContattoDto.Richiedente.FORNITORI.getValore(), dto.getId(),
                    contact.getId());
        }

        for (ContattoDto contact : newContatti) {
            if (contact.getId() <= 0) {
                contact.setIdRichiedente(dto.getId());
                contact.setUserCreated(dto.getUserLastUpdate());
                contattiDao.insert(ContattoDto.Richiedente.FORNITORI.getValore(), contact);
            } else {
                contact.setIdRichiedente(dto.getId());
                contact.setUserLastUpdate(dto.getUserLastUpdate());
                contattiDao.update(ContattoDto.Richiedente.FORNITORI.getValore(), contact);
            }
        }
    }
}
