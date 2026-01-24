package it.tinna.smartdoc.server.delegate.unitamisura;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.unitamisura.UnitaMisuraDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.unitamisura.UnitaMisuraDto;

import org.springframework.transaction.annotation.Transactional;

@Service
public class UnitaMisuraDelegate extends BaseDelegate {

    public List<UnitaMisuraDto> getList(String search, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        return dao.getList(search, start, length, orderColumn, orderDir);
    }

    public List<UnitaMisuraDto> getListForCombo() throws SQLException {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public UnitaMisuraDto getById(long id) throws SQLException {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        return dao.getById(id);
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        return dao.checkUniqueness(descrizione, id != null ? id.longValue() : null);
    }

    @Transactional
    public void insert(UnitaMisuraDto dto) throws SQLException {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        dao.insert(dto, dto.getUserCreated().intValue());
    }

    @Transactional
    public void update(UnitaMisuraDto dto) throws SQLException {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        dao.update(dto, dto.getUserLastUpdate().intValue());
    }

    @Transactional
    public void delete(long id, long userId) throws SQLException {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        dao.delete(id, (int) userId);
    }
}
