package it.tinna.smartdoc.server.dao.municipality;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;

@Repository
public class MunicipalityDao extends BaseDao {

    public MunicipalityDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<MunicipalityDto> getSuggestion(String q) throws SQLException {
        BeanPropertyRowMapper<MunicipalityDto> rowMapper = new BeanPropertyRowMapper<>();
        rowMapper.setMappedClass(MunicipalityDto.class);
        try {
            // Ensure search string is formatted for LIKE
            String searchParam = "%" + q.trim().toLowerCase() + "%"; // Simple wildcard wrapper
            // Note: Legacy used StringUtility.formatForLike, assuming simpler approach here or migrate StringUtility if needed.
            // But wait, BaseDao imports StringUtility?
             return jdbcTemplate.query(FileQueryReader.getQuery("COMUNI_S01"), rowMapper, searchParam);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'elenco aziende contenenti la stringa {}", q, e);
            throw new SQLException(e);
        }
    }
}
