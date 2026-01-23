package it.tinna.smartdoc.server.delegate.municipality;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.municipality.MunicipalityDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;

@Service
public class MunicipalityDelegate extends BaseDelegate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<MunicipalityDto> getSuggestion(String q) throws SQLException {
        MunicipalityDao dao = new MunicipalityDao(jdbcTemplate);
        return dao.getSuggestion(q);
    }

}
