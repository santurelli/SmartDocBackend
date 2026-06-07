package it.tinna.smartdoc.server.delegate.municipality;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.municipality.MunicipalityDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;

@Transactional(readOnly = true)
@Service
public class MunicipalityDelegate extends BaseDelegate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("serviceJdbcTemplate")
    private JdbcTemplate serviceJdbcTemplate;

    public String getEmailErroriSdi(String dbKey) throws SQLException {
        MunicipalityDao dao = new MunicipalityDao(serviceJdbcTemplate);
        return dao.getEmailErroriSdi(dbKey);
    }

    public List<MunicipalityDto> getSuggestion(String q) throws SQLException {
        MunicipalityDao dao = new MunicipalityDao(serviceJdbcTemplate);
        return dao.getSuggestion(q);
    }

    public MunicipalityDto getByPartitaIva(String partitaIva) throws SQLException {
        MunicipalityDao dao = new MunicipalityDao(serviceJdbcTemplate);
        return dao.getByPartitaIva(partitaIva);
    }

    public List<MunicipalityDto> getAziendeConFatturazioneElettronica() throws SQLException {
        MunicipalityDao dao = new MunicipalityDao(serviceJdbcTemplate);
        return dao.getAziendeConFatturazioneElettronica();
    }
}

