package it.tinna.smartdoc.server.delegate.tipiporto;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.tipiporto.TipiPortoDao;
import it.tinna.smartdoc.shared.dto.tipiporto.TipoPortoDto;

@Service
public class TipiPortoDelegate {

    @Autowired
    private TipiPortoDao tipiPortoDao;

    public List<TipoPortoDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return tipiPortoDao.getList(search, length, start, orderCol, orderDir);
    }

    public List<TipoPortoDto> getListForCombo() throws SQLException {
        return tipiPortoDao.getListForCombo();
    }
    
    public TipoPortoDto getById(Integer id) throws SQLException {
        return tipiPortoDao.getById(id);
    }

    @Transactional
    public void insert(TipoPortoDto dto, Integer userId) throws SQLException {
        tipiPortoDao.insert(dto, userId);
    }

    @Transactional
    public void update(TipoPortoDto dto, Integer userId) throws SQLException {
        tipiPortoDao.update(dto, userId);
    }

    @Transactional
    public void delete(Integer id, Integer userId) throws SQLException {
        tipiPortoDao.delete(id, userId);
    }
    
    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        return tipiPortoDao.checkUniqueness(descrizione, id);
    }
}
