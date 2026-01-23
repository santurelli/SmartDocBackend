package it.tinna.smartdoc.server.dao.contatti;

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

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;

public class ContattiDao extends BaseDao {

    public ContattiDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void deleteByIdRichiedente(String richiedente, long idRichiedente, long id) throws SQLException {
        String sql = FileQueryReader.getQuery("CONTATTI_D01");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("nometabella", "d_e_contatti_" + richiedente);
        sql = StringSubstitutor.replace(sql, valuesMap);
        try {
            jdbcTemplate.update(sql, idRichiedente, id);
        } catch (DataAccessException e) {
            _log.error("Errore nella cancellazione del contatto {} per il richiedente {} con id {}", id, richiedente,
                    idRichiedente, e);
            throw new SQLException(e);
        }
    }

    public List<ContattoDto> getListByIdRichiedente(String richiedente, long idRichiedente) throws SQLException {
        String sql = FileQueryReader.getQuery("CONTATTI_S01");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("nometabella", "d_e_contatti_" + richiedente);
        sql = StringSubstitutor.replace(sql, valuesMap);
        try {
            BeanPropertyRowMapper<ContattoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ContattoDto.class);
            return jdbcTemplate.query(sql, rowMapper, idRichiedente);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'elenco contatti per il richiedente {} con id {}", richiedente,
                    idRichiedente, e);
            throw new SQLException(e);
        }
    }

    public Integer insert(String richiedente, ContattoDto dto) throws SQLException {
        String sql = FileQueryReader.getQuery("CONTATTI_I01");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("nometabella", "d_e_contatti_" + richiedente);
        sql = StringSubstitutor.replace(sql, valuesMap);
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, StringUtils.defaultIfEmpty(dto.getTipologia(), null),
                    StringUtils.defaultIfEmpty(dto.getDescrizione(), null),
                    StringUtils.defaultIfEmpty(dto.getTelefono(), null),
                    StringUtils.defaultIfEmpty(dto.getCellulare(), null), StringUtils.defaultIfEmpty(dto.getFax(), null),
                    StringUtils.defaultIfEmpty(dto.getEmail(), null), StringUtils.defaultIfEmpty(dto.getPec(), null),
                    dto.getIdRichiedente(), dto.getUserCreated());
        } catch (DataAccessException e) {
            _log.error("Errore nell'inserimento del contatto di tipo {}", richiedente, e);
            throw new SQLException(e);
        }
    }

    public void update(String richiedente, ContattoDto dto) throws SQLException {
        String sql = FileQueryReader.getQuery("CONTATTI_U01");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("nometabella", "d_e_contatti_" + richiedente);
        sql = StringSubstitutor.replace(sql, valuesMap);
        try {
            jdbcTemplate.update(sql, StringUtils.defaultIfEmpty(dto.getTipologia(), null),
                    StringUtils.defaultIfEmpty(dto.getDescrizione(), null),
                    StringUtils.defaultIfEmpty(dto.getTelefono(), null),
                    StringUtils.defaultIfEmpty(dto.getCellulare(), null), StringUtils.defaultIfEmpty(dto.getFax(), null),
                    StringUtils.defaultIfEmpty(dto.getEmail(), null), StringUtils.defaultIfEmpty(dto.getPec(), null),
                    dto.getUserLastUpdate(), dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'aggiornamento del contatto {} di tipo {}", dto.getId(), richiedente, e);
            throw new SQLException(e);
        }
    }
}
