package it.tinna.smartdoc.server.delegate.agenti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.agenti.AgentiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.agenti.AgenteDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@Service
@CacheConfig(cacheNames = "agenti", cacheResolver = "companyCacheResolver")
public class AgentiDelegate extends BaseDelegate {

    @Autowired
    private AgentiDao agentiDao;

    @Transactional
    @CacheEvict(allEntries = true)
    public void delete(Integer idUser, List<Integer> ids) throws SQLException {
        for (Integer id : ids) {
            agentiDao.delete(idUser, id);
        }
    }

    public List<AgenteDto> getSuggestion(String query) throws SQLException {
        return agentiDao.getSuggestion(query);
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        return agentiDao.isExistent(descrizione, id);
    }

    public AgenteDto getById(Integer id) throws SQLException {
        return agentiDao.getById(id);
    }

    public List<AgenteDto> getList(String denominazione, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        return agentiDao.getList(denominazione, length, start, orderColumn, orderDir);
    }

    @Cacheable
    public List<AgenteDto> getListForCombo() throws SQLException {
        return agentiDao.getListForCombo();
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void insert(AgenteDto dto, Integer userId) throws SQLException {
        agentiDao.insert(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void update(AgenteDto dto, Integer userId) throws SQLException {
        agentiDao.update(dto, userId);
    }
}
