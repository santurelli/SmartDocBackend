package it.tinna.smartdoc.server.dao.toniarticolo;

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
import it.tinna.smartdoc.shared.dto.toniarticolo.TonoArticoloDto;

public class ToniArticoloDao extends BaseDao
{

    public ToniArticoloDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(long idUser,
                       long idTonoArticolo) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("TONIARTICOLO_D01"), idUser, idTonoArticolo);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione dei toni articolo {}", idTonoArticolo, e);
            throw new SQLException(e);
        }
    }

    public TonoArticoloDto getById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<TonoArticoloDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(TonoArticoloDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("TONIARTICOLO_S02"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessun tono articolo trovato con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del tono articolo {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<TonoArticoloDto> getList(String strToSearch,
                                         Integer length,
                                         Integer start,
                                         Integer orderColumn,
                                         String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("TONIARTICOLO_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        Map<String, String> valuesMap = new HashMap<>();
        if ( orderColumn == 0 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("descrizione ").append(orderDir).toString());
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
            BeanPropertyRowMapper<TonoArticoloDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(TonoArticoloDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco toni articolo", e);
            throw new SQLException(e);
        }
    }

    public List<TonoArticoloDto> getListForCombo() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<TonoArticoloDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(TonoArticoloDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("TONIARTICOLO_S04"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco toni articolo per il popolamento delle combo", e);
            throw new SQLException(e);
        }
    }

    public void insert(TonoArticoloDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("TONIARTICOLO_I01"), dto.getDescrizione(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento del tono articolo", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("TONIARTICOLO_S03"), Long.class, descrizione, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione dell'esistenza del tono articolo con descrizione {}", descrizione, e);
            throw new SQLException(e);
        }
    }

    public void update(TonoArticoloDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("TONIARTICOLO_U01"), dto.getDescrizione(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento del tono articolo {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

