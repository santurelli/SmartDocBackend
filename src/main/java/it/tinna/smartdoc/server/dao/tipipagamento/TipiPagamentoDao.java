package it.tinna.smartdoc.server.dao.tipipagamento;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;

public class TipiPagamentoDao extends BaseDao
{

    public TipiPagamentoDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(long idUser,
                       long idTipoPagamento) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("TIPIPAGAMENTO_D01"), idUser, idTipoPagamento);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione del tipo pagamento {}", idTipoPagamento, e);
            throw new SQLException(e);
        }
    }

    public void deleteScadenze(long idTipoPagamento) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("TIPIPAGAMENTO_D02"), idTipoPagamento);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione delle scadenza per il pagamento {}", idTipoPagamento, e);
            throw new SQLException(e);
        }
    }

    public TipoPagamentoDto getById(Integer id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<TipoPagamentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(TipoPagamentoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("TIPIPAGAMENTO_S02"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessun tipo pagamento trovato con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del tipo pagamento con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<TipoPagamentoDto> getList(String strToSearch,
                                          Integer length,
                                          Integer start,
                                          Integer orderColumn,
                                          String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("TIPIPAGAMENTO_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        Map<String, String> valuesMap = new HashMap<>();
        String dir = StringUtils.isEmpty(orderDir) ? "asc" : orderDir;
        if ( orderColumn != null && orderColumn == 0 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("descrizione ").append(dir).toString());
        }
        else
        {
            valuesMap.put("ORDER_BY", new StringBuilder("descrizione ").append(dir).toString());
        }
        if ( length != null && start != null )
        {
            valuesMap.put("LIMIT", "LIMIT ? OFFSET ?");
            params.add(length);
            params.add(start);
        }
        else
        {
            valuesMap.put("LIMIT", "");
        }
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            BeanPropertyRowMapper<TipoPagamentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(TipoPagamentoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco tipi pagamento", e);
            throw new SQLException(e);
        }
    }

    public List<TipoPagamentoDto> getListForCombo() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<TipoPagamentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(TipoPagamentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("TIPIPAGAMENTO_S05"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco tipi pagamento per il popolamento della combo", e);
            throw new SQLException(e);
        }
    }

    /**
     * Restituisce l'elenco dei tipi pagamento per il popolamento della combo in prima nota. L'elenco restituito sarà quello dei soli tipi con una sola scadenza pari al 100%
     * 
     * @return
     * @throws SQLException
     */
    public List<TipoPagamentoDto> getListForPrimaNota() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<TipoPagamentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(TipoPagamentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("TIPIPAGAMENTO_S06"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco tipi pagamento per il popolamento della combo in prima nota", e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzaPagamentoDto> getScadenze(long idTipoPagamento) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("TIPIPAGAMENTO_S04"), rowMapper, idTipoPagamento);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle scadenze per il tipo pagamento {}", idTipoPagamento, e);
            throw new SQLException(e);
        }
    }

    public Integer insert(TipoPagamentoDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("TIPIPAGAMENTO_I01"), Integer.class, dto.getDescrizione(), dto.getModalita(), dto.getSaldaSubito(), dto.getGiornoPagamentoMeseSuccessivo(), dto.getSpostaScadenze(), dto.getIdSpeseIncasso(), dto.getPredefinito(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento del tipo pagamento", e);
            throw new SQLException(e);
        }
    }

    public void insertScadenza(ScadenzaPagamentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("TIPIPAGAMENTO_I02"), dto.getIdTipoPagamento(), dto.getGiorni(), dto.getPercTotale(), dto.getFineMese());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento della scadenza per il tipo pagamento {}", dto.getIdTipoPagamento(), e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("TIPIPAGAMENTO_S03"), Long.class, descrizione, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella verifica dell'esistenza del tipo pagamento con descrizione {}", descrizione, e);
            throw new SQLException(e);
        }
    }

    public void resetPredefinite(long idUser) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("TIPIPAGAMENTO_U02"), idUser);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel reset del tipo pagamento predefinito", e);
            throw new SQLException(e);
        }
    }

    public void update(TipoPagamentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("TIPIPAGAMENTO_U01"), dto.getDescrizione(), dto.getModalita(), dto.getSaldaSubito(), dto.getGiornoPagamentoMeseSuccessivo(), dto.getSpostaScadenze(), dto.getIdSpeseIncasso(), dto.getPredefinito(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento del tipo pagamento {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

