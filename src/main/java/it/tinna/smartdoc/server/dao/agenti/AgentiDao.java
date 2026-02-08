package it.tinna.smartdoc.server.dao.agenti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.agenti.AgenteDto;

@Repository
public class AgentiDao extends BaseDao {

    public AgentiDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void delete(Integer idUser, Integer idAgente) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("AGENTI_D01"), idUser, idAgente);
        } catch (DataAccessException e) {
            _log.error("Errore nella cancellazione dell'agente {}", idAgente, e);
            throw new SQLException(e);
        }
    }

    public AgenteDto getById(Integer id) throws SQLException {
        try {
            BeanPropertyRowMapper<AgenteDto> rowMapper = new BeanPropertyRowMapper<>(AgenteDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("AGENTI_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            _log.error("Nessun agente trovato con id {}", id);
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'agente con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<AgenteDto> getList(String denominazione, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        String query = FileQueryReader.getQuery("AGENTI_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(denominazione) ? null : StringUtility.formatForLike(denominazione));
        
        Map<String, String> valuesMap = new HashMap<>();
        String orderBy = "denominazione " + (orderDir != null ? orderDir : "asc");
        valuesMap.put("ORDER_BY", orderBy);

        if (length != null && start != null) {
            valuesMap.put("LIMIT", "LIMIT ? OFFSET ?");
            params.add(length);
            params.add(start);
        } else {
            valuesMap.put("LIMIT", "");
        }
        
        query = StringSubstitutor.replace(query, valuesMap);
        
        try {
            BeanPropertyRowMapper<AgenteDto> rowMapper = new BeanPropertyRowMapper<>(AgenteDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nella ricerca degli agenti", e);
            throw new SQLException(e);
        }
    }

    public void insert(AgenteDto dto, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("AGENTI_I01"), 
                dto.getDenominazione(), 
                dto.getIndirizzo(), 
                dto.getCap(), 
                dto.getCitta(), 
                dto.getProvincia(), 
                dto.getNazione(), 
                dto.getCellulare(), 
                dto.getTelefono(), 
                dto.getEmail(), 
                dto.getFax(), 
                dto.getPercProvvigione(), 
                dto.getIdZonaCompetenza(), 
                dto.getTipoMaturazioneProvvigione(), 
                userId);
        } catch (DataAccessException e) {
            _log.error("Errore nell'inserimento dell'agente", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String denominazione, Integer id) throws SQLException {
        try {
            Long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("AGENTI_S03"), Long.class, denominazione, id);
            return l != null && l > 0;
        } catch (DataAccessException e) {
            _log.error("Errore nella verifica dell'esistenza dell'agente con denominazione {}", denominazione, e);
            throw new SQLException(e);
        }
    }

    public List<AgenteDto> getSuggestion(String query) throws SQLException {
        try {
            BeanPropertyRowMapper<AgenteDto> rowMapper = new BeanPropertyRowMapper<>(AgenteDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("AGENTI_S04"), rowMapper, StringUtility.formatForLike(query.toLowerCase()));
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dei suggerimenti per gli agenti", e);
            throw new SQLException(e);
        }
    }

    public void update(AgenteDto dto, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("AGENTI_U01"), 
                dto.getDenominazione(), 
                dto.getIndirizzo(), 
                dto.getCap(), 
                dto.getCitta(), 
                dto.getProvincia(), 
                dto.getNazione(), 
                dto.getCellulare(), 
                dto.getTelefono(), 
                dto.getEmail(), 
                dto.getFax(), 
                dto.getPercProvvigione(), 
                dto.getIdZonaCompetenza(), 
                dto.getTipoMaturazioneProvvigione(), 
                userId, 
                dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'aggiornamento dell'agente con id {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public List<AgenteDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<AgenteDto> rowMapper = new BeanPropertyRowMapper<>(AgenteDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("AGENTI_S05"), rowMapper);
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero degli agenti per combo", e);
            throw new SQLException(e);
        }
    }
}
