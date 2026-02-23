package it.tinna.smartdoc.server.delegate;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;

public class BaseDelegate {

    protected Logger _log = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    protected CacheManager cacheManager;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected String nomeDb;

    // public BaseDelegate(JdbcTemplate jdbcTemplate)
    // {
    // this.jdbcTemplate = jdbcTemplate;
    // }

    public BaseDelegate() {
    }

    public String getDbVersion() throws SQLException {
        try {
            DatabaseMetaData meta = this.jdbcTemplate.getDataSource().getConnection().getMetaData();
            String productVersion = meta.getDatabaseProductVersion();
            return productVersion;
        } catch (SQLException e) {
            _log.error("Errore nel recupero della versione del database", e);
            throw e;
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public String getNomeDb() {
        return nomeDb;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setNomeDb(String nomeDb) {
        this.nomeDb = nomeDb;
    }

}

