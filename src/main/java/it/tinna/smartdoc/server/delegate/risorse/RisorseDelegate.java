package it.tinna.smartdoc.server.delegate.risorse;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.risorse.RisorseDao;
import it.tinna.smartdoc.shared.dto.risorse.RisorsaDto;

@Service
public class RisorseDelegate {

    @Autowired
    private RisorseDao risorseDao;

    public List<RisorsaDto> getList(String tipologia, String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return risorseDao.getList(tipologia, search, length, start, orderCol, orderDir);
    }

    public List<RisorsaDto> getListForCombo(String tipologia) throws SQLException {
        return risorseDao.getListForCombo(tipologia);
    }
    
    public RisorsaDto getById(Integer id) throws SQLException {
        return risorseDao.getById(id);
    }

    @Transactional
    public void insert(RisorsaDto dto, Integer userId) throws SQLException {
        if (dto.getPredefinita() != null && dto.getPredefinita() == 1) {
            risorseDao.resetPredefinita(dto.getTipologia(), userId);
        }
        risorseDao.insert(dto, userId);
    }

    @Transactional
    public void update(RisorsaDto dto, Integer userId) throws SQLException {
         if (dto.getPredefinita() != null && dto.getPredefinita() == 1) {
            risorseDao.resetPredefinita(dto.getTipologia(), userId);
        }
        risorseDao.update(dto, userId);
    }

    @Transactional
    public void delete(Integer id, Integer userId) throws SQLException {
        risorseDao.delete(id, userId);
    }
    
    public boolean isExistent(String tipologia, String descrizione, Integer id) throws SQLException {
        return risorseDao.checkUniqueness(tipologia, descrizione, id);
    }
}
