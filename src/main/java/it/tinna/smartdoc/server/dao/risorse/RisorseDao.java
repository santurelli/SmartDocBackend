package it.tinna.smartdoc.server.dao.risorse;

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
import it.tinna.smartdoc.shared.dto.risorse.RisorsaDto;

public class RisorseDao extends BaseDao
{

    public RisorseDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(long idUser,
                       long idRisorsa) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("RISORSE_D01"), idUser, idRisorsa);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione della risorsa {}", idRisorsa, e);
            throw new SQLException(e);
        }
    }

    public RisorsaDto getByDenominazione(String tipologia,
                                         String descrizione) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<RisorsaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(RisorsaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("RISORSE_S05"), rowMapper, tipologia, descrizione);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della risorsa con descrizione {} e tipo {}", descrizione, tipologia, e);
            throw new SQLException(e);
        }
    }

    public RisorsaDto getById(Integer id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<RisorsaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(RisorsaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("RISORSE_S02"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna risorsa trovata con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della risorsa con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<RisorsaDto> getList(String tipologia,
                                    String strToSearch,
                                    Integer length,
                                    Integer start,
                                    Integer orderColumn,
                                    String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("RISORSE_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(tipologia) ? null : tipologia);
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        Map<String, String> valuesMap = new HashMap<>();
        String safeOrderDir = StringUtils.defaultIfEmpty(orderDir, "asc");
        if ( orderColumn != null && orderColumn == 0 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("tipologia ").append(safeOrderDir).toString());
        }
        else if ( orderColumn != null && orderColumn == 1 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("descrizione ").append(safeOrderDir).toString());
        }
        else
        {
            valuesMap.put("ORDER_BY", new StringBuilder("descrizione ").append(safeOrderDir).toString()); // Default sorting
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
            BeanPropertyRowMapper<RisorsaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(RisorsaDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco risorse", e);
            throw new SQLException(e);
        }
    }

    public List<RisorsaDto> getListForCombo(String tipologia) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<RisorsaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(RisorsaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("RISORSE_S06"), rowMapper, StringUtils.isBlank(tipologia) ? null : tipologia);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco risorse per il popolamento della combo", e);
            throw new SQLException(e);
        }
    }

    // public List<ItemSuggestion> getSuggestionBanche(String query) throws
    // Exception {
    // QueryRunner qRunner = new QueryRunner();
    // return qRunner.query( conn,
    // FileQueryReader.getQuery("RISORSE_S04"),
    // new TrimmedBeanListHandler<ItemSuggestion>(ItemSuggestion.class),
    // new Object[] { StringUtility.formatForLike(query.toLowerCase()) });
    // }

    public void insert(RisorsaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("RISORSE_I01"), dto.getTipologia(), dto.getDescrizione(), dto.getSaldoIniziale(), StringUtils.isEmpty(dto.getCodSia()) ? null : dto.getCodSia(), StringUtils.isEmpty(dto.getDescBanca()) ? null : dto.getDescBanca(), StringUtils.isEmpty(dto.getIban()) ? null : dto.getIban(), StringUtils.isEmpty(dto.getCin()) ? null : dto.getCin(), StringUtils.isEmpty(dto.getAbi()) ? null : dto.getAbi(), StringUtils.isEmpty(dto.getCab()) ? null : dto.getCab(), StringUtils.isEmpty(dto.getConto()) ? null : dto.getConto(), StringUtils.isEmpty(dto.getBic()) ? null : dto.getBic(), dto.getNote(), dto.getPredefinita(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio della risorsa", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String tipologia,
                              String descrizione,
                              Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("RISORSE_S03"), Long.class, tipologia, descrizione, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione dell'esistenza della risorsa con descrizione {} e tipologia {}", descrizione, tipologia, e);
            throw new SQLException(e);
        }
    }

    public void resetPredefinite(String tipologia,
                                 long idUser) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("RISORSE_U02"), idUser, tipologia);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel reset della risorsa predefinita di tipo {}", tipologia, e);
            throw new SQLException(e);
        }
    }

    public void update(RisorsaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("RISORSE_U01"), dto.getTipologia(), dto.getDescrizione(), dto.getSaldoIniziale(), StringUtils.isEmpty(dto.getCodSia()) ? null : dto.getCodSia(), StringUtils.isEmpty(dto.getDescBanca()) ? null : dto.getDescBanca(), StringUtils.isEmpty(dto.getIban()) ? null : dto.getIban(), StringUtils.isEmpty(dto.getCin()) ? null : dto.getCin(), StringUtils.isEmpty(dto.getAbi()) ? null : dto.getAbi(), StringUtils.isEmpty(dto.getCab()) ? null : dto.getCab(), StringUtils.isEmpty(dto.getConto()) ? null : dto.getConto(), StringUtils.isEmpty(dto.getBic()) ? null : dto.getBic(), dto.getNote(), dto.getPredefinita(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento della risorsa con id {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

