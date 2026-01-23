package it.tinna.smartdoc.server.delegate.fornitori;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.fornitori.FornitoriDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;

@Service
public class FornitoriDelegate extends BaseDelegate {

    public List<FornitoreDto> getListForCombo() throws SQLException {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        return dao.getListForCombo();
    }
    
    public List<FornitoreDto> getSuggestion(String query) throws SQLException {
        FornitoriDao dao = new FornitoriDao(jdbcTemplate);
        return dao.getSuggestion(query);
    }
}
