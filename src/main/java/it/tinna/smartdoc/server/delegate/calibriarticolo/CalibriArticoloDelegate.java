package it.tinna.smartdoc.server.delegate.calibriarticolo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.calibriarticolo.CalibriArticoloDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.calibriarticolo.CalibroArticoloDto;

@Service
public class CalibriArticoloDelegate extends BaseDelegate {

    public List<CalibroArticoloDto> getListForCombo() throws SQLException {
        CalibriArticoloDao dao = new CalibriArticoloDao(jdbcTemplate);
        return dao.getListForCombo();
    }
}
