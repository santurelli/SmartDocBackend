package it.tinna.smartdoc.server.delegate.documenti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import it.tinna.smartdoc.server.dao.documenti.CausaliEsigibilitaDifferitaDao;
import it.tinna.smartdoc.shared.dto.documenti.CausaleEsigibilitaDifferitaDto;

@Service
@CacheConfig(cacheNames = "causaliesigibilita", cacheResolver = "companyCacheResolver")
public class CausaliEsigibilitaDifferitaDelegate {

    @Autowired
    private CausaliEsigibilitaDifferitaDao dao;

    public List<CausaleEsigibilitaDifferitaDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return dao.getList(search, length, start, orderCol, orderDir);
    }

    @Cacheable
    public List<CausaleEsigibilitaDifferitaDto> getListForCombo() throws SQLException {
        return dao.getListForCombo();
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void insert(CausaleEsigibilitaDifferitaDto dto, Integer userId) throws SQLException {
        dao.insert(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void update(CausaleEsigibilitaDifferitaDto dto, Integer userId) throws SQLException {
        dao.update(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void delete(Integer id, Integer userId) throws SQLException {
        dao.delete(id, userId);
    }
}

