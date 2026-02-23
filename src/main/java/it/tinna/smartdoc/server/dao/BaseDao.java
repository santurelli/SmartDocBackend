package it.tinna.smartdoc.server.dao;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.database.FileQueryReader;

public class BaseDao {

    protected Logger _log = LoggerFactory.getLogger(this.getClass());

    protected JdbcTemplate jdbcTemplate;

    public BaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long getCurrSequenceValue(String seqName) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("GENERAL_S01"), Long.class, seqName);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero del valore corrente della sequence {}", seqName, e);
            throw new SQLException(e);
        }
    }

}

