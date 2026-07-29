package it.tinna.smartdoc.server.dao.municipality;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import org.springframework.beans.factory.annotation.Qualifier;
import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;

@Repository
public class MunicipalityDao extends BaseDao {

    public MunicipalityDao(@Qualifier("serviceJdbcTemplate") JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<MunicipalityDto> getSuggestion(String q) throws SQLException {
        BeanPropertyRowMapper<MunicipalityDto> rowMapper = new BeanPropertyRowMapper<>();
        rowMapper.setMappedClass(MunicipalityDto.class);
        try {
            // Ensure search string is formatted for LIKE
            String searchParam = "%" + q.trim().toLowerCase() + "%"; // Simple wildcard wrapper
             return jdbcTemplate.query(FileQueryReader.getQuery("COMUNI_S01"), rowMapper, searchParam);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'elenco aziende contenenti la stringa {}", q, e);
            throw new SQLException(e);
        }
    }

    public MunicipalityDto getByPartitaIva(String partitaIva) throws SQLException {
        BeanPropertyRowMapper<MunicipalityDto> rowMapper = new BeanPropertyRowMapper<>();
        rowMapper.setMappedClass(MunicipalityDto.class);
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("COMUNI_S07"), rowMapper, partitaIva);
        } catch (EmptyResultDataAccessException e) {
            _log.error("Nessuna azienda trovata con partita IVA {}", partitaIva);
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'azienda con partita IVA {}", partitaIva, e);
            throw new SQLException(e);
        }
    }

    public List<MunicipalityDto> getAziendeConFatturazioneElettronica() throws SQLException {
        BeanPropertyRowMapper<MunicipalityDto> rowMapper = new BeanPropertyRowMapper<>(MunicipalityDto.class);
        try {
            return jdbcTemplate.query(FileQueryReader.getQuery("COMUNI_S06"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero delle aziende con fatturazione elettronica", e);
            throw new SQLException(e);
        }
    }

    public List<MunicipalityDto> getAziendeAttive() throws SQLException {
        BeanPropertyRowMapper<MunicipalityDto> rowMapper = new BeanPropertyRowMapper<>(MunicipalityDto.class);
        try {
            return jdbcTemplate.query(FileQueryReader.getQuery("COMUNI_S08"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero delle aziende attive", e);
            throw new SQLException(e);
        }
    }

    public String getEmailErroriSdi(String dbKey) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("ENTI_S02"), String.class, dbKey);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'email errori SDI per dbKey {}", dbKey, e);
            throw new SQLException(e);
        }
    }
}

