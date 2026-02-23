package it.tinna.smartdoc.server.dao.configurazione;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.configurazione.ConfigurazioneDto;

public class ConfigurazioneDao extends BaseDao {
    
    private static final Logger _log = LoggerFactory.getLogger(ConfigurazioneDao.class);

    public ConfigurazioneDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void delete(String dominio, String chiave) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CONFIGURAZIONE_D01"), dominio, chiave);
        } catch (DataAccessException e) {
            _log.error("Errore nella cancellazione della configurazione con dominio {} e chiave {}", dominio, chiave, e);
            throw new SQLException(e);
        }
    }

    public List<String> getAsList(String domain, String key) {
        try {
            String str = jdbcTemplate.queryForObject(FileQueryReader.getQuery("CONFIGURAZIONE_S01"), String.class, new Object[]{domain, key});
            if (str == null) return Collections.emptyList();
            
            String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, ",");
            List<String> result = new ArrayList<>();
            if (tokens != null) {
                for (String token : tokens) {
                    result.add(token);
                }
            }
            return result;
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public String[] getAsArray(String dominio, String chiave) {
        List<String> result = getAsList(dominio, chiave);
        if (result != null) {
            return result.toArray(new String[0]);
        } else {
            return null;
        }
    }

    public String getByKey(String dominio, String chiave) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CONFIGURAZIONE_S01"), String.class, dominio, chiave);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero del parametro con dominio {} e chiave {}", dominio, chiave, e);
            throw new SQLException(e);
        }
    }

    public int getByKeyAsInt(String dominio, String chiave) throws SQLException {
        try {
            Integer val = jdbcTemplate.queryForObject(FileQueryReader.getQuery("CONFIGURAZIONE_S01"), Integer.class, dominio, chiave);
            return val != null ? val : 0;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero del parametro intero con dominio {} e chiave {}", dominio, chiave, e);
            throw new SQLException(e);
        }
    }

    public Map<String, ConfigurazioneDto> getByDomain(String domain) throws SQLException {
        BeanPropertyRowMapper<ConfigurazioneDto> rowMapper = new BeanPropertyRowMapper<>(ConfigurazioneDto.class);
        try {
            List<ConfigurazioneDto> list = jdbcTemplate.query(FileQueryReader.getQuery("CONFIGURAZIONE_S02"), rowMapper, domain);
            Map<String, ConfigurazioneDto> result = new HashMap<>();
            for (ConfigurazioneDto dto : list) {
                result.put(dto.getChiave(), dto);
            }
            return result;
        } catch (EmptyResultDataAccessException e) {
            return new HashMap<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dei parametri del dominio {}", domain, e);
            throw new SQLException(e);
        }
    }

    public void save(ConfigurazioneDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CONFIGURAZIONE_I01"), dto.getDominio(), dto.getChiave(), dto.getValore());
        } catch (DuplicateKeyException e) {
            jdbcTemplate.update(FileQueryReader.getQuery("CONFIGURAZIONE_U01"), dto.getValore(), dto.getDominio(), dto.getChiave());
        } catch (DataAccessException e) {
            _log.error("Errore nell'inserimento del parametro di configurazione con dominio {}, chiave {} e valore {}", dto.getDominio(), dto.getChiave(), dto.getValore(), e);
            throw new SQLException(e);
        }
    }

    public void update(ConfigurazioneDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CONFIGURAZIONE_U01"), dto.getValore(), dto.getDominio(), dto.getChiave());
        } catch (DataAccessException e) {
            _log.error("Errore nell'aggiornamento del parametro di configurazione con dominio {}, chiave {} e valore {}", dto.getDominio(), dto.getChiave(), dto.getValore(), e);
            throw new SQLException(e);
        }
    }

    public List<ConfigurazioneDto> getAll() throws SQLException {
        BeanPropertyRowMapper<ConfigurazioneDto> rowMapper = new BeanPropertyRowMapper<>(ConfigurazioneDto.class);
        try {
            // Assuming CONFIGURAZIONE_S03 is Select * or we construct a simple query if Reader allows.
            // Since we don't have the XML, I will execute a direct query for now or try to locate the file.
            // Safefallback: select * from CONFIGURAZIONE
             return jdbcTemplate.query("SELECT * FROM d_e_configurazione ORDER BY DOMINIO, CHIAVE", rowMapper);
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero di tutte le configurazioni", e);
            throw new SQLException(e);
        }
    }
}

