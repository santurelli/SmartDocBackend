package it.tinna.smartdoc.server.dao.documenti.annotazioniestese;

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
import it.tinna.smartdoc.shared.dto.documenti.annotazioniestese.AnnotazioneEstesaDto;

public class AnnotazioniEsteseDao extends BaseDao
{

    public AnnotazioniEsteseDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(long idUser,
                       long id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ANNOTAZIONIESTESE_D01"), idUser, id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Error enell'eliminazione dell'annotazione preventivo con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public AnnotazioneEstesaDto getById(Integer id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<AnnotazioneEstesaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(AnnotazioneEstesaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("ANNOTAZIONIEsTESE_S02"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna annotazione preventivo trovata con id {}", id, e);
            throw new SQLException(e);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'annotazione preventivo con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<AnnotazioneEstesaDto> getList(String strToSearch,
                                              Integer length,
                                              Integer start,
                                              Integer orderColumn,
                                              String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("ANNOTAZIONIESTESE_S04");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("ORDER_BY", new StringBuilder("descrizione ").append(orderDir).toString());
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
            BeanPropertyRowMapper<AnnotazioneEstesaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(AnnotazioneEstesaDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco annotazioni preventivo", e);
            throw new SQLException(e);
        }
    }

    public void insert(AnnotazioneEstesaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ANNOTAZIONIESTESE_I01"), dto.getValue(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dell'annotazione preventivo", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("ANNOTAZIONIESTESE_S03"), Long.class, descrizione, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella verifica dell'esistenza dell'annotazione preventivo con descrizione {}", descrizione, e);
            throw new SQLException(e);
        }
    }

    public void update(AnnotazioneEstesaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ANNOTAZIONIESTESE_U01"), dto.getValue(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dell'annotazione preventivo con id {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

