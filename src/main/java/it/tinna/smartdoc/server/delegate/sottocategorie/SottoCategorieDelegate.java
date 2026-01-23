package it.tinna.smartdoc.server.delegate.sottocategorie;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.sottocategorie.SottoCategorieDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.sottocategorie.SottoCategoriaDto;

@Service
public class SottoCategorieDelegate extends BaseDelegate {

    @Autowired
    private SottoCategorieDao sottoCategorieDao;

    public List<SottoCategoriaDto> getList(Integer idCategoria, String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return sottoCategorieDao.getList(idCategoria, search, length, start, orderCol, orderDir);
    }

    public List<SottoCategoriaDto> getListForCombo(Integer idCategoria) throws SQLException {
        return sottoCategorieDao.getListForCombo(idCategoria);
    }

    public SottoCategoriaDto getById(Integer id) throws SQLException {
        return sottoCategorieDao.getById(id);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void insert(SottoCategoriaDto dto, Integer userId) throws SQLException {
        sottoCategorieDao.insert(dto, userId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void update(SottoCategoriaDto dto, Integer userId) throws SQLException {
        sottoCategorieDao.update(dto, userId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(Integer id, Integer userId) throws SQLException {
        sottoCategorieDao.delete(id, userId);
    }

    public boolean isExistent(Integer parentId, String descrizione, Integer id) throws SQLException {
        return sottoCategorieDao.checkUniqueness(parentId, descrizione, id);
    }
}
