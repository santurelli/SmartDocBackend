package it.tinna.smartdoc.server.delegate.sceltearticolo;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.sceltearticolo.ScelteArticoloDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.sceltearticolo.SceltaArticoloDto;

import java.sql.SQLException;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class ScelteArticoloDelegate extends BaseDelegate {

    public List<SceltaArticoloDto> getList(String search, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        ScelteArticoloDao dao = new ScelteArticoloDao(jdbcTemplate);
        return dao.getList(search, length, start, orderColumn, orderDir);
    }

    @Cacheable(value = "scelteArticoloCombo", cacheResolver = "companyCacheResolver")
    public List<SceltaArticoloDto> getListForCombo() throws SQLException {
        ScelteArticoloDao dao = new ScelteArticoloDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public SceltaArticoloDto getById(Integer id) throws SQLException {
        ScelteArticoloDao dao = new ScelteArticoloDao(jdbcTemplate);
        return dao.getById(id);
    }

    @CacheEvict(value = "scelteArticoloCombo", allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void insert(SceltaArticoloDto dto, Integer userId) throws SQLException {
        ScelteArticoloDao dao = new ScelteArticoloDao(jdbcTemplate);
        dao.insert(dto, userId);
    }

    @CacheEvict(value = "scelteArticoloCombo", allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void update(SceltaArticoloDto dto, Integer userId) throws SQLException {
        ScelteArticoloDao dao = new ScelteArticoloDao(jdbcTemplate);
        dao.update(dto, userId);
    }

    @CacheEvict(value = "scelteArticoloCombo", allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Integer id, Integer userId) throws SQLException {
        ScelteArticoloDao dao = new ScelteArticoloDao(jdbcTemplate);
        dao.delete(id, userId);
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        ScelteArticoloDao dao = new ScelteArticoloDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }
}

