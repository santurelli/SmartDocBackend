package it.tinna.smartdoc.server.dao.indirizzi;

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
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;

@Repository
public class IndirizziDao extends BaseDao {

    public IndirizziDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void deleteByIdRichiedente(String richiedente, long idRichiedente, long id) throws SQLException {
        String sql = FileQueryReader.getQuery("INDIRIZZI_D01");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("nometabella", "d_e_indirizzi_" + richiedente);
        sql = StringSubstitutor.replace(sql, valuesMap);
        try {
            jdbcTemplate.update(sql, idRichiedente, id);
        } catch (DataAccessException e) {
            _log.error("Errore nella cancellazione dell'indirizzo {} per il richiedente {}", id, idRichiedente, e);
            throw new SQLException(e);
        }
    }

    public List<IndirizzoDto> getListByIdRichiedente(String richiedente, long idRichiedente) throws SQLException {
        String sql = FileQueryReader.getQuery("INDIRIZZI_S01");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("nometabella", "d_e_indirizzi_" + richiedente);
        sql = StringSubstitutor.replace(sql, valuesMap);
        try {
            BeanPropertyRowMapper<IndirizzoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(IndirizzoDto.class);
            return jdbcTemplate.query(sql, rowMapper, idRichiedente);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'elenco indirizzi per il richiedente {} con id {}", richiedente,
                    idRichiedente, e);
            throw new SQLException(e);
        }
    }

    public Integer insert(String richiedente, IndirizzoDto dto) throws SQLException {
        String sql = FileQueryReader.getQuery("INDIRIZZI_I01");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("nometabella", "d_e_indirizzi_" + richiedente);
        sql = StringSubstitutor.replace(sql, valuesMap);
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, StringUtils.defaultIfEmpty(dto.getTipologia(), null),
                    StringUtils.defaultIfEmpty(dto.getDescrizione(), null),
                    StringUtils.defaultIfEmpty(dto.getIndirizzo(), null),
                    StringUtils.defaultIfEmpty(dto.getCap(), null), StringUtils.defaultIfEmpty(dto.getCitta(), null),
                    StringUtils.defaultIfEmpty(dto.getProvincia(), null),
                    StringUtils.defaultIfEmpty(dto.getNazione(), null),
                    StringUtils.defaultIfEmpty(dto.getCodiceUfficio(), null), dto.getIdRichiedente(),
                    dto.getUserCreated());
        } catch (DataAccessException e) {
            _log.error("Errore nell'inserimento dell'indirizzo per il richiedente {}", richiedente, e);
            throw new SQLException(e);
        }
    }

    public void update(String richiedente, IndirizzoDto dto) throws SQLException {
        String sql = FileQueryReader.getQuery("INDIRIZZI_U01");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("nometabella", "d_e_indirizzi_" + richiedente);
        sql = StringSubstitutor.replace(sql, valuesMap);
        try {
            jdbcTemplate.update(sql, StringUtils.defaultIfEmpty(dto.getTipologia(), null),
                    StringUtils.defaultIfEmpty(dto.getDescrizione(), null),
                    StringUtils.defaultIfEmpty(dto.getIndirizzo(), null), StringUtils.defaultIfEmpty(dto.getCap(), null),
                    StringUtils.defaultIfEmpty(dto.getCitta(), null),
                    StringUtils.defaultIfEmpty(dto.getProvincia(), null),
                    StringUtils.defaultIfEmpty(dto.getNazione(), null),
                    StringUtils.defaultIfEmpty(dto.getCodiceUfficio(), null), dto.getUserLastUpdate(), dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'aggiornamento dell'indirizzo {} per il richiedente {}", dto.getId(), richiedente,
                    e);
            throw new SQLException(e);
        }
    }
}
