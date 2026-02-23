package it.tinna.smartdoc.server.delegate.causalitrasporto;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import it.tinna.smartdoc.server.dao.causalitrasporto.CausaliTrasportoDao;
import it.tinna.smartdoc.shared.dto.documenti.CausaleTrasportoDto;

@Service
@CacheConfig(cacheNames = "causalitrasporto", cacheResolver = "companyCacheResolver")
public class CausaliTrasportoDelegate {

    @Autowired
    private CausaliTrasportoDao causaliTrasportoDao;

    public List<CausaleTrasportoDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return causaliTrasportoDao.getList(search, length, start, orderCol, orderDir);
    }

    @Cacheable
    public List<CausaleTrasportoDto> getListForCombo() throws SQLException {
        return causaliTrasportoDao.getListForCombo();
    }
    
    public CausaleTrasportoDto getById(Integer id) throws SQLException {
        return causaliTrasportoDao.getById(id);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void insert(CausaleTrasportoDto dto, Integer userId) throws SQLException {
        if (dto.getPredefinita() != null && dto.getPredefinita() == 1) {
            causaliTrasportoDao.resetPredefinita(userId);
        }
        causaliTrasportoDao.insert(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void update(CausaleTrasportoDto dto, Integer userId) throws SQLException {
        if (dto.getPredefinita() != null && dto.getPredefinita() == 1) {
            causaliTrasportoDao.resetPredefinita(userId);
        }
        causaliTrasportoDao.update(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void delete(Integer id, Integer userId) throws SQLException {
        causaliTrasportoDao.delete(id, userId);
    }
    
    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        return causaliTrasportoDao.checkUniqueness(descrizione, id);
    }
}

