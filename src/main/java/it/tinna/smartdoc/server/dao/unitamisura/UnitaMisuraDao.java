package it.tinna.smartdoc.server.dao.unitamisura;

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
import it.tinna.smartdoc.shared.dto.unitamisura.UnitaMisuraDto;

public class UnitaMisuraDao extends BaseDao
{

    public UnitaMisuraDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(long idUser,
                       long idUnitaMisura) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("UNITAMISURA_D01"), idUser, idUnitaMisura);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione dell'unità di misura {}", idUnitaMisura, e);
            throw new SQLException(e);
        }
    }

    public UnitaMisuraDto getById(Integer id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<UnitaMisuraDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(UnitaMisuraDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("UNITAMISURA_S02"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna unità di misura trovata con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'unità di misura con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<UnitaMisuraDto> getList(String strToSearch,
                                        Integer length,
                                        Integer start,
                                        Integer orderColumn,
                                        String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("UNITAMISURA_S01");
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
            BeanPropertyRowMapper<UnitaMisuraDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(UnitaMisuraDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco unità di misura", e);
            throw new SQLException(e);
        }
    }

    public List<UnitaMisuraDto> getListForCombo() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<UnitaMisuraDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(UnitaMisuraDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("UNITAMISURA_S05"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco unità misura per il popolamento della combo", e);
            throw new SQLException(e);
        }
    }

    public void insert(UnitaMisuraDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("UNITAMISURA_I01"), dto.getDescrizione(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dell'unità di misura", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("UNITAMISURA_S03"), Long.class, descrizione, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel controllo dell'esistenza dell'unità di misura con descrizione {}", descrizione, e);
            throw new SQLException(e);
        }
    }

    /**
     * Usato in fase di salvataggio della configurazione per verificare se ci sono articoli con una differente unità di misura del peso già presenti nel db
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public UnitaMisuraDto isExistentDifferentUm(Integer id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<UnitaMisuraDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(UnitaMisuraDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("UNITAMISURA_S04"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella verifica dell'esistenza di articoli con unità di misura {}", id, e);
            throw new SQLException(e);
        }
    }

    public void update(UnitaMisuraDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("UNITAMISURA_U01"), dto.getDescrizione(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dell'unità di misura {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

