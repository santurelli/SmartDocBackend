package it.tinna.smartdoc.server.delegate.unitamisura;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.unitamisura.UnitaMisuraDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.unitamisura.UnitaMisuraDto;

@Service
public class UnitaMisuraDelegate extends BaseDelegate {

    public List<UnitaMisuraDto> getListForCombo() throws SQLException {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        return dao.getListForCombo();
    }
}
