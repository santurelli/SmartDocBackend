package it.tinna.smartdoc.server.dao.citta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.citta.CittaDto;

@Repository
public class CittaDao extends BaseDao {

    private static final Log _log = LogFactory.getLog(CittaDao.class);

    public CittaDao(@org.springframework.beans.factory.annotation.Qualifier("serviceJdbcTemplate") JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<CittaDto> getSuggestion(String query) throws SQLException {
        try {
            BeanPropertyRowMapper<CittaDto> rowMapper = new BeanPropertyRowMapper<>(CittaDto.class);
            String q = query.toLowerCase();
            return jdbcTemplate.query(FileQueryReader.getQuery("CITTA_S01"),
                    rowMapper,
                    q,
                    StringUtility.formatForLike(q),
                    q);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dei suggerimenti per le città", e);
            throw new SQLException(e);
        }
    }
}

