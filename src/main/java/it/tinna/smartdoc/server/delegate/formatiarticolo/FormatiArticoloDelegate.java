package it.tinna.smartdoc.server.delegate.formatiarticolo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.formatiarticolo.FormatiArticoloDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.formatiarticolo.FormatoArticoloDto;

@Service
public class FormatiArticoloDelegate extends BaseDelegate {

    public List<FormatoArticoloDto> getListForCombo() throws SQLException {
        FormatiArticoloDao dao = new FormatiArticoloDao(jdbcTemplate);
        return dao.getListForCombo();
    }
}
