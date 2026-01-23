package it.tinna.smartdoc.server.delegate.categorie;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.categorie.CategorieDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.categorie.CategoriaDto;

@Service
public class CategorieDelegate extends BaseDelegate {

    @Autowired
    private CategorieDao categorieDao;

    public List<CategoriaDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return categorieDao.getList(search, length, start, orderCol, orderDir);
    }

    public List<CategoriaDto> getListForCombo() throws SQLException {
        return categorieDao.getListForCombo();
    }

    public CategoriaDto getById(Integer id) throws SQLException {
        return categorieDao.getById(id);
    }

    @Transactional
    public void insert(CategoriaDto dto, Integer userId) throws SQLException {
        categorieDao.insert(dto, userId);
    }

    @Transactional
    public void update(CategoriaDto dto, Integer userId) throws SQLException {
        categorieDao.update(dto, userId);
    }

    @Transactional
    public void delete(Integer id, Integer userId) throws SQLException {
        categorieDao.delete(id, userId);
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        return categorieDao.checkUniqueness(descrizione, id);
    }
}
