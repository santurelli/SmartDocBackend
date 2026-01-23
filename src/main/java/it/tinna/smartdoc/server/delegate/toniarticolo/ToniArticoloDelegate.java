package it.tinna.smartdoc.server.delegate.toniarticolo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.toniarticolo.ToniArticoloDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.toniarticolo.TonoArticoloDto;

@Service
public class ToniArticoloDelegate extends BaseDelegate {

    public List<TonoArticoloDto> getListForCombo() throws SQLException {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        return dao.getListForCombo();
    }
}
