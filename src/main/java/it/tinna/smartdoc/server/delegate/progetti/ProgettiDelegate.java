package it.tinna.smartdoc.server.delegate.progetti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.progetti.ProgettiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.progetti.ProgettoDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "progettiDelegate")
@CacheConfig(cacheNames = "progetti", cacheResolver = "companyCacheResolver")
public class ProgettiDelegate extends BaseDelegate {

    public List<ProgettoDto> getSuggestion(String q) throws SQLException {
        ProgettiDao dao = new ProgettiDao(jdbcTemplate);
        return dao.getSuggestion(q);
    }

    public ProgettoDto getById(Long id) throws SQLException {
        ProgettiDao dao = new ProgettiDao(jdbcTemplate);
        return dao.getById(id);
    }

    @Cacheable
    public List<ProgettoDto> getListForCombo() throws SQLException {
        ProgettiDao dao = new ProgettiDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public Integer insert(ProgettoDto dto) throws SQLException {
        ProgettiDao dao = new ProgettiDao(jdbcTemplate);
        return dao.insert(dto);
    }
}

