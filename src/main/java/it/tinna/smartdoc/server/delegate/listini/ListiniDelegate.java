package it.tinna.smartdoc.server.delegate.listini;

import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.tinna.smartdoc.server.dao.listini.ListiniDao;
import it.tinna.smartdoc.shared.dto.listini.ListinoDto;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@Transactional(readOnly = true)
@Service
@CacheConfig(cacheNames = "listini", cacheResolver = "companyCacheResolver")
public class ListiniDelegate extends BaseDelegate {

    @Autowired
    private ListiniDao listiniDao;

    @Cacheable
    public List<ListinoDto> getListForCombo() throws SQLException {
        return listiniDao.getListForCombo();
    }

    @Cacheable
    public List<ListinoDto> getAll() throws SQLException {
        return listiniDao.getAll();
    }

    public ListinoDto getById(Long id) throws SQLException {
        return listiniDao.getById(id);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public Long insert(ListinoDto dto, Integer user) throws SQLException {
        return listiniDao.insert(dto, user);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void update(ListinoDto dto, Integer user) throws SQLException {
        listiniDao.update(dto, user);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id, Integer user) throws SQLException {
        listiniDao.delete(id, user);
    }
}

