package it.tinna.smartdoc.server.delegate.toniarticolo;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.toniarticolo.ToniArticoloDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.toniarticolo.TonoArticoloDto;

import java.sql.SQLException;
import java.util.List;

@Service
public class ToniArticoloDelegate extends BaseDelegate {

    public List<TonoArticoloDto> getList(String search, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        return dao.getList(search, length, start, orderColumn, orderDir);
    }

    @Cacheable(value = "toniArticoloCombo", cacheResolver = "companyCacheResolver")
    public List<TonoArticoloDto> getListForCombo() throws SQLException {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public TonoArticoloDto getById(Integer id) throws SQLException {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        return dao.getById(id);
    }

    @CacheEvict(value = "toniArticoloCombo", allEntries = true)
    public void insert(TonoArticoloDto dto, Integer userId) throws SQLException {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        dao.insert(dto, userId);
    }

    @CacheEvict(value = "toniArticoloCombo", allEntries = true)
    public void update(TonoArticoloDto dto, Integer userId) throws SQLException {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        dao.update(dto, userId);
    }

    @CacheEvict(value = "toniArticoloCombo", allEntries = true)
    public void delete(Integer id, Integer userId) throws SQLException {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        dao.delete(id, userId);
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }
}
