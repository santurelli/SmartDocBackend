package it.tinna.smartdoc.server.dao.tipipagamento;

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
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;

@Repository
public class TipiPagamentoDao extends BaseDao {

    public TipiPagamentoDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void delete(Long idUser, long idTipoPagamento) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("TIPIPAGAMENTO_D01"), idUser, idTipoPagamento);
        } catch (DataAccessException e) {
            _log.error("Errore nella cancellazione del tipo pagamento {}", idTipoPagamento, e);
            throw new SQLException(e);
        }
    }

    public void deleteScadenze(long idTipoPagamento) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("TIPIPAGAMENTO_D02"), idTipoPagamento);
        } catch (DataAccessException e) {
            _log.error("Errore nella cancellazione delle scadenze per il pagamento {}", idTipoPagamento, e);
            throw new SQLException(e);
        }
    }

    public TipoPagamentoDto getById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<TipoPagamentoDto> rowMapper = new BeanPropertyRowMapper<>(TipoPagamentoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("TIPIPAGAMENTO_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            _log.error("Nessun tipo pagamento trovato con id {}", id);
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero del tipo pagamento con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<TipoPagamentoDto> getList(String strToSearch, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        String query = FileQueryReader.getQuery("TIPIPAGAMENTO_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        
        Map<String, String> valuesMap = new HashMap<>();
        String orderBy = "descrizione " + (orderDir != null ? orderDir : "asc");
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
            BeanPropertyRowMapper<TipoPagamentoDto> rowMapper = new BeanPropertyRowMapper<>(TipoPagamentoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'elenco tipi pagamento", e);
            throw new SQLException(e);
        }
    }

    public List<TipoPagamentoDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<TipoPagamentoDto> rowMapper = new BeanPropertyRowMapper<>(TipoPagamentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("TIPIPAGAMENTO_S05"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'elenco tipi pagamento per combo", e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzaPagamentoDto> getScadenze(long idTipoPagamento) throws SQLException {
        try {
            BeanPropertyRowMapper<ScadenzaPagamentoDto> rowMapper = new BeanPropertyRowMapper<>(ScadenzaPagamentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("TIPIPAGAMENTO_S04"), rowMapper, idTipoPagamento);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero delle scadenze per il tipo pagamento {}", idTipoPagamento, e);
            throw new SQLException(e);
        }
    }

    public Long insert(TipoPagamentoDto dto, Long userId) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("TIPIPAGAMENTO_I01"), Long.class, 
                dto.getDescrizione(), 
                dto.getModalita(), 
                dto.getSaldaSubito(), 
                dto.getGiornoPagamentoMeseSuccessivo(), 
                dto.getSpostaScadenze(), 
                dto.getIdSpeseIncasso(), 
                dto.getPredefinito(), 
                userId);
        } catch (DataAccessException e) {
            _log.error("Errore nell'inserimento del tipo pagamento", e);
            throw new SQLException(e);
        }
    }

    public void insertScadenza(ScadenzaPagamentoDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("TIPIPAGAMENTO_I02"), 
                dto.getIdTipoPagamento(), 
                dto.getGiorni(), 
                dto.getPercTotale(), 
                dto.getFineMese());
        } catch (DataAccessException e) {
            _log.error("Errore nell'inserimento della scadenza per il tipo pagamento {}", dto.getIdTipoPagamento(), e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String descrizione, long id) throws SQLException {
        try {
            Long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("TIPIPAGAMENTO_S03"), Long.class, descrizione, id);
            return l != null && l > 0;
        } catch (DataAccessException e) {
            _log.error("Errore nella verifica dell'esistenza del tipo pagamento con descrizione {}", descrizione, e);
            throw new SQLException(e);
        }
    }

    public void resetPredefinite(Long idUser) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("TIPIPAGAMENTO_U02"), idUser);
        } catch (DataAccessException e) {
            _log.error("Errore nel reset del tipo pagamento predefinito", e);
            throw new SQLException(e);
        }
    }

    public void update(TipoPagamentoDto dto, Long userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("TIPIPAGAMENTO_U01"), 
                dto.getDescrizione(), 
                dto.getModalita(), 
                dto.getSaldaSubito(), 
                dto.getGiornoPagamentoMeseSuccessivo(), 
                dto.getSpostaScadenze(), 
                dto.getIdSpeseIncasso(), 
                dto.getPredefinito(), 
                userId, 
                dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'aggiornamento del tipo pagamento {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }
}
