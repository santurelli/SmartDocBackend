package it.tinna.smartdoc.server.delegate.formatiarticolo;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.formatiarticolo.FormatiArticoloDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.formatiarticolo.FormatoArticoloDto;

import java.sql.SQLException;
import java.util.List;

@Service
public class FormatiArticoloDelegate extends BaseDelegate {

    public List<FormatoArticoloDto> getList(String search, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        FormatiArticoloDao dao = new FormatiArticoloDao(jdbcTemplate);
        return dao.getList(search, length, start, orderColumn, orderDir);
    }

    @Cacheable(value = "formatiArticoloCombo", cacheResolver = "companyCacheResolver")
    public List<FormatoArticoloDto> getListForCombo() throws SQLException {
        FormatiArticoloDao dao = new FormatiArticoloDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public FormatoArticoloDto getById(Integer id) throws SQLException {
        FormatiArticoloDao dao = new FormatiArticoloDao(jdbcTemplate);
        return dao.getById(id);
    }

    @CacheEvict(value = "formatiArticoloCombo", allEntries = true)
    public void insert(FormatoArticoloDto dto, Integer userId) throws SQLException {
        FormatiArticoloDao dao = new FormatiArticoloDao(jdbcTemplate);
        dao.insert(dto, userId);
    }

    @CacheEvict(value = "formatiArticoloCombo", allEntries = true)
    public void update(FormatoArticoloDto dto, Integer userId) throws SQLException {
        FormatiArticoloDao dao = new FormatiArticoloDao(jdbcTemplate);
        dao.update(dto, userId);
    }

    @CacheEvict(value = "formatiArticoloCombo", allEntries = true)
    public void delete(Integer id, Integer userId) throws SQLException {
        FormatiArticoloDao dao = new FormatiArticoloDao(jdbcTemplate);
        dao.delete(id, userId);
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        FormatiArticoloDao dao = new FormatiArticoloDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }
}

