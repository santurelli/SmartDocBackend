package it.tinna.smartdoc.server.delegate.divisioni;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.divisioni.DivisioniDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.divisioni.DivisioneDto;

@Transactional(readOnly = true)
@Service
public class DivisioniDelegate extends BaseDelegate {

    public List<DivisioneDto> getListForCombo() throws SQLException {
        DivisioniDao dao = new DivisioniDao(jdbcTemplate);
        return dao.getListForCombo();
    }
}

