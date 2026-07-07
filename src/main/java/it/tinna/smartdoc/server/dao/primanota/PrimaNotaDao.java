package it.tinna.smartdoc.server.dao.primanota;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.clienti.BaseClienteDto;
import it.tinna.smartdoc.shared.dto.primanota.PagamentoPrimaNotaDto;
import it.tinna.smartdoc.shared.dto.primanota.PrimaNotaDto;

public class PrimaNotaDao extends BaseDao
{

    public PrimaNotaDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void deletePagamento(long id,
                                long idUtente) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("PRIMANOTA_D01"), idUtente, id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'eliminazione del pagamento in prima nota con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<PrimaNotaDto> getList(Integer tipoPagamento,
                                      String soggetto,
                                      String dtFrom,
                                      String dtTo,
                                      Integer risorsa,
                                      String tipologia,
                                      long idDivisione,
                                      Integer length,
                                      Integer start,
                                      Integer orderColumn,
                                      String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("PRIMANOTA_S01");
        List<Object> params = new ArrayList<>();
        Map<String, String> valuesMap = new HashMap<>();
        params.add(tipoPagamento);
        params.add(StringUtils.isEmpty(soggetto) ? null : soggetto);
        params.add(StringUtils.isEmpty(dtFrom) ? null : dtFrom);
        params.add(StringUtils.isEmpty(dtTo) ? null : dtTo);
        params.add(risorsa);
        if ( StringUtils.isNotBlank(tipologia) )
        {
            if ( tipologia.equalsIgnoreCase("E") )
            {
                valuesMap.put("TIPOLOGIA", "AND entrate IS NOT NULL");
            }
            else
            {
                valuesMap.put("TIPOLOGIA", "AND uscite IS NOT NULL");
            }
        }
        else
        {
            valuesMap.put("TIPOLOGIA", "");
        }
        params.add(idDivisione == 0l ? null : idDivisione);
        if ( orderColumn == 0 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("data ").append(orderDir).toString());
        }
        else
        {
            valuesMap.put("ORDER_BY", new StringBuilder("data ").append(orderDir).toString());
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
BeanPropertyRowMapper<PrimaNotaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(PrimaNotaDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della prima nota", e);
            throw new SQLException(e);
        }
    }

    /**
     * Restituisce l'elenco dei pagamenti ricevuti (tipo = 'E') o effettuati (tipo = 'U') su un dato progetto
     * 
     * @param idProgetto
     * @param tipo
     * @param length
     * @param start
     * @param orderColumn
     * @param orderDir
     * @return
     * @throws SQLException
     */
    public List<PrimaNotaDto> getPagamentiByProgetto(long idProgetto,
                                                     String tipo,
                                                     int length,
                                                     int start,
                                                     int orderColumn,
                                                     String orderDir) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<PrimaNotaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(PrimaNotaDto.class);
            String query = FileQueryReader.getQuery("PRIMANOTA_S03");
            List<Object> params = new ArrayList<>();
            params.add(idProgetto);
            params.add(tipo);
            Map<String, String> valuesMap = new HashMap<>();
            valuesMap.put("ORDER_BY", new StringBuilder("data ").append(orderDir).toString());
            if ( length != -1 && start != -1 )
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
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei pagamenti ricevuti associati al progetto {}", idProgetto, e);
            throw new SQLException(e);
        }
    }

    public List<BaseClienteDto> getSoggettoSuggestion(String query) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<BaseClienteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(BaseClienteDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("PRIMANOTA_S02"), rowMapper, StringUtility.formatForLike(query.toLowerCase()), StringUtility.formatForLike(query.toLowerCase()), StringUtility.formatForLike(query.toLowerCase()), StringUtility.formatForLike(query.toLowerCase()), StringUtility.formatForLike(query.toLowerCase()), StringUtility.formatForLike(query.toLowerCase()), StringUtility.formatForLike(query.toLowerCase()), StringUtility.formatForLike(query.toLowerCase()));
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei suggerimenti per i soggetti", e);
            throw new SQLException(e);
        }
    }

    public void insertPagamento(PagamentoPrimaNotaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("PRIMANOTA_I01"), dto.getDtPagamento(), dto.getIdRisorsa(), dto.getIdTipoPagamento(), dto.getIdSoggetto(), dto.getTipoSoggetto(), StringUtils.defaultIfEmpty(dto.getDescrizione(), null), StringUtils.defaultIfEmpty(dto.getModalita(), null), StringUtils.defaultIfEmpty(dto.getRiferimento(), null), dto.getIdProgetto(), dto.getIdDivisione(), dto.getEntrata(), dto.getUscita(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento del pagamento in prima nota", e);
            throw new SQLException(e);
        }
    }

    public void updatePagamento(PagamentoPrimaNotaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("PRIMANOTA_U01"), dto.getDtPagamento(), dto.getIdRisorsa(), dto.getIdTipoPagamento(), dto.getIdSoggetto(), dto.getTipoSoggetto(), StringUtils.defaultIfEmpty(dto.getDescrizione(), null), StringUtils.defaultIfEmpty(dto.getModalita(), null), StringUtils.defaultIfEmpty(dto.getRiferimento(), null), dto.getIdProgetto(), dto.getIdDivisione(), dto.getEntrata(), dto.getUscita(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento del pagamento in prima nota con id {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

