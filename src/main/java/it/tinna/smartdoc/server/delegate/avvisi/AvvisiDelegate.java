package it.tinna.smartdoc.server.delegate.avvisi;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.avvisi.AvvisiDao;
import it.tinna.smartdoc.shared.dto.avvisi.AvvisoDto;

@Service
@CacheConfig(cacheNames = "avvisi", cacheResolver = "companyCacheResolver")
public class AvvisiDelegate {

    @Autowired
    private AvvisiDao avvisiDao;

    public List<AvvisoDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return avvisiDao.getList(search, length, start, orderCol, orderDir);
    }

    @Cacheable
    public List<AvvisoDto> getListForCombo() throws SQLException {
        return avvisiDao.getListForCombo();
    }
    
    public AvvisoDto getById(Integer id) throws SQLException {
        return avvisiDao.getById(id);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void insert(AvvisoDto dto, Integer userId) throws SQLException {
        avvisiDao.insert(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void update(AvvisoDto dto, Integer userId) throws SQLException {
        avvisiDao.update(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void delete(Integer id, Integer userId) throws SQLException {
        avvisiDao.delete(id, userId);
    }
    
    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        return avvisiDao.checkUniqueness(descrizione, id);
    }
}
