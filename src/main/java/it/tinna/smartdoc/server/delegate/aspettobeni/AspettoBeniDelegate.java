package it.tinna.smartdoc.server.delegate.aspettobeni;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import it.tinna.smartdoc.server.dao.aspettobeni.AspettoBeniDao;
import it.tinna.smartdoc.shared.dto.aspettobeni.AspettoBeniDto;

@Service
@CacheConfig(cacheNames = "aspettibeni", cacheResolver = "companyCacheResolver")
public class AspettoBeniDelegate {

    @Autowired
    private AspettoBeniDao aspettoBeniDao;

    public List<AspettoBeniDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return aspettoBeniDao.getList(search, length, start, orderCol, orderDir);
    }

    @Cacheable
    public List<AspettoBeniDto> getListForCombo() throws SQLException {
        return aspettoBeniDao.getListForCombo();
    }
    
    public AspettoBeniDto getById(Integer id) throws SQLException {
        return aspettoBeniDao.getById(id);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void insert(AspettoBeniDto dto, Integer userId) throws SQLException {
        aspettoBeniDao.insert(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void update(AspettoBeniDto dto, Integer userId) throws SQLException {
        aspettoBeniDao.update(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void delete(Integer id, Integer userId) throws SQLException {
        aspettoBeniDao.delete(id, userId);
    }
    
    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        return aspettoBeniDao.checkUniqueness(descrizione, id);
    }
}

