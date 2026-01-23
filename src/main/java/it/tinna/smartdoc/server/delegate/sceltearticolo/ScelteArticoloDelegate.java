package it.tinna.smartdoc.server.delegate.sceltearticolo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.sceltearticolo.ScelteArticoloDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.sceltearticolo.SceltaArticoloDto;

@Service
public class ScelteArticoloDelegate extends BaseDelegate {

    public List<SceltaArticoloDto> getListForCombo() throws SQLException {
        ScelteArticoloDao dao = new ScelteArticoloDao(jdbcTemplate);
        return dao.getListForCombo();
    }
}
