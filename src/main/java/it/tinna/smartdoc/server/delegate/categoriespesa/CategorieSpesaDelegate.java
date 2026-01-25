package it.tinna.smartdoc.server.delegate.categoriespesa;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.categoriespesa.CategorieSpesaDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.categoriespesa.CategoriaSpesaDto;

@Service(value = "categorieSpesaDelegate")
public class CategorieSpesaDelegate extends BaseDelegate {

    public void delete(Integer idUser, List<Integer> ids) throws SQLException {
        CategorieSpesaDao dao = new CategorieSpesaDao(jdbcTemplate);
        for (Integer id : ids) {
            dao.delete(idUser, id);
        }
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        CategorieSpesaDao dao = new CategorieSpesaDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }

    public CategoriaSpesaDto getById(Integer id) throws SQLException {
        CategorieSpesaDao dao = new CategorieSpesaDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<CategoriaSpesaDto> getList(String descrizione) throws SQLException {
        CategorieSpesaDao dao = new CategorieSpesaDao(jdbcTemplate);
        return dao.getList(descrizione);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void insert(CategoriaSpesaDto dto) throws SQLException {
        CategorieSpesaDao dao = new CategorieSpesaDao(jdbcTemplate);
        if (dto.getPredefinita() != null && dto.getPredefinita().intValue() == 1) {
            dao.resetPredefinite(dto.getUserCreated());
        }
        dao.insert(dto);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void update(CategoriaSpesaDto dto) throws SQLException {
        CategorieSpesaDao dao = new CategorieSpesaDao(jdbcTemplate);
        if (dto.getPredefinita() != null && dto.getPredefinita().intValue() == 1) {
            dao.resetPredefinite(dto.getUserCreated());
        }
        dao.update(dto);
    }

}
