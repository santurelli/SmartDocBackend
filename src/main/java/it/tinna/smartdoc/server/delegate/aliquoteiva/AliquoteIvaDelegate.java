package it.tinna.smartdoc.server.delegate.aliquoteiva;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;

@Service
public class AliquoteIvaDelegate extends BaseDelegate {

    public List<AliquotaIvaDto> getListForCombo() throws SQLException {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        return dao.getListForCombo();
    }
}
