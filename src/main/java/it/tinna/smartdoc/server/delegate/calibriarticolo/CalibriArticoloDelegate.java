package it.tinna.smartdoc.server.delegate.calibriarticolo;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.calibriarticolo.CalibriArticoloDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.calibriarticolo.CalibroArticoloDto;

import java.sql.SQLException;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class CalibriArticoloDelegate extends BaseDelegate {

    public List<CalibroArticoloDto> getList(String search, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        CalibriArticoloDao dao = new CalibriArticoloDao(jdbcTemplate);
        return dao.getList(search, length, start, orderColumn, orderDir);
    }

    @Cacheable(value = "calibriArticoloCombo", cacheResolver = "companyCacheResolver")
    public List<CalibroArticoloDto> getListForCombo() throws SQLException {
        CalibriArticoloDao dao = new CalibriArticoloDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public CalibroArticoloDto getById(Integer id) throws SQLException {
        CalibriArticoloDao dao = new CalibriArticoloDao(jdbcTemplate);
        return dao.getById(id);
    }

    @CacheEvict(value = "calibriArticoloCombo", allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void insert(CalibroArticoloDto dto, Integer userId) throws SQLException {
        CalibriArticoloDao dao = new CalibriArticoloDao(jdbcTemplate);
        dao.insert(dto, userId);
    }

    @CacheEvict(value = "calibriArticoloCombo", allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void update(CalibroArticoloDto dto, Integer userId) throws SQLException {
        CalibriArticoloDao dao = new CalibriArticoloDao(jdbcTemplate);
        dao.update(dto, userId);
    }

    @CacheEvict(value = "calibriArticoloCombo", allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Integer id, Integer userId) throws SQLException {
        CalibriArticoloDao dao = new CalibriArticoloDao(jdbcTemplate);
        dao.delete(id, userId);
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        CalibriArticoloDao dao = new CalibriArticoloDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }
}

