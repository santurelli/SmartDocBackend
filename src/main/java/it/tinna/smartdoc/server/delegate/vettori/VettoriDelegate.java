package it.tinna.smartdoc.server.delegate.vettori;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.vettori.VettoriDao;
import it.tinna.smartdoc.shared.dto.vettori.VettoreDto;

@Service
public class VettoriDelegate {

    @Autowired
    private VettoriDao vettoriDao;

    public List<VettoreDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return vettoriDao.getList(search, length, start, orderCol, orderDir);
    }

    public List<VettoreDto> getListForCombo() throws SQLException {
        return vettoriDao.getListForCombo();
    }
    
    public VettoreDto getById(Integer id) throws SQLException {
        return vettoriDao.getById(id);
    }

    @Transactional
    public void insert(VettoreDto dto, Integer userId) throws SQLException {
        vettoriDao.insert(dto, userId);
    }

    @Transactional
    public void update(VettoreDto dto, Integer userId) throws SQLException {
        vettoriDao.update(dto, userId);
    }

    @Transactional
    public void delete(Integer id, Integer userId) throws SQLException {
        vettoriDao.delete(id, userId);
    }
    
    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        return vettoriDao.checkUniqueness(descrizione, id);
    }
}
