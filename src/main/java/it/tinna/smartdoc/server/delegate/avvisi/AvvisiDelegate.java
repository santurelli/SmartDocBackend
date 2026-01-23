package it.tinna.smartdoc.server.delegate.avvisi;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.avvisi.AvvisiDao;
import it.tinna.smartdoc.shared.dto.avvisi.AvvisoDto;

@Service
public class AvvisiDelegate {

    @Autowired
    private AvvisiDao avvisiDao;

    public List<AvvisoDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return avvisiDao.getList(search, length, start, orderCol, orderDir);
    }

    public List<AvvisoDto> getListForCombo() throws SQLException {
        return avvisiDao.getListForCombo();
    }
    
    public AvvisoDto getById(Integer id) throws SQLException {
        return avvisiDao.getById(id);
    }

    @Transactional
    public void insert(AvvisoDto dto, Integer userId) throws SQLException {
        avvisiDao.insert(dto, userId);
    }

    @Transactional
    public void update(AvvisoDto dto, Integer userId) throws SQLException {
        avvisiDao.update(dto, userId);
    }

    @Transactional
    public void delete(Integer id) throws SQLException {
        avvisiDao.delete(id);
    }
    
    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        return avvisiDao.checkUniqueness(descrizione, id);
    }
}
