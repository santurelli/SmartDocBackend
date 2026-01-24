package it.tinna.smartdoc.server.delegate.aliquoteiva;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;

@Service
public class AliquoteIvaDelegate extends BaseDelegate {

    public List<AliquotaIvaDto> getList(String search, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        return dao.getList(search, start, length, orderColumn, orderDir);
    }

    public List<AliquotaIvaDto> getListForCombo() throws SQLException {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public AliquotaIvaDto getById(long id) throws SQLException {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        return dao.getById(id);
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        return dao.checkUniqueness(descrizione, id != null ? id.longValue() : null);
    }

    @Transactional
    public void insert(AliquotaIvaDto dto) throws SQLException {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        if (dto.getPredefinita() != null && dto.getPredefinita() == 1) {
            dao.resetPredefinita(dto.getUserCreated().intValue());
        }
        dao.insert(dto, dto.getUserCreated().intValue());
    }

    @Transactional
    public void update(AliquotaIvaDto dto) throws SQLException {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        if (dto.getPredefinita() != null && dto.getPredefinita() == 1) {
            dao.resetPredefinita(dto.getUserLastUpdate().intValue());
        }
        dao.update(dto, dto.getUserLastUpdate().intValue());
    }

    @Transactional
    public void delete(long id, long userId) throws SQLException {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        dao.delete(id, (int) userId);
    }
}
