package it.tinna.smartdoc.server.dao.aliquoteiva;

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
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;

public class AliquoteIvaDao extends BaseDao
{

    public AliquoteIvaDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(long idUser,
                       Long idAliquota) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ALIQUOTEIVA_D01"), idUser, idAliquota);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione dell'aliquota iva {}", idAliquota, e);
        }
    }

    public List<AliquotaIvaDto> getByAliquota(double aliquota) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<AliquotaIvaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(AliquotaIvaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("ALIQUOTEIVA_S05"), rowMapper, aliquota);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna aliquota iva trovata con aliquota {}", aliquota);
            return new ArrayList<AliquotaIvaDto>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'aliquota iva con aliquota {}", aliquota, e);
            throw new SQLException(e);
        }
    }

    public AliquotaIvaDto getById(Integer id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<AliquotaIvaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(AliquotaIvaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("ALIQUOTEIVA_S02"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna aliquota iva trovata con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'aliquota iva {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<AliquotaIvaDto> getList(String strToSearch,
                                        Integer length,
                                        Integer start,
                                        Integer orderColumn,
                                        String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("ALIQUOTEIVA_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        Map<String, String> valuesMap = new HashMap<>();
        if ( orderColumn == 1 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("codice ").append(orderDir).toString());
        }
        else if ( orderColumn == 2 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("imposta ").append(orderDir).toString());
        }
        else if ( orderColumn == 3 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("indetraibilita ").append(orderDir).toString());
        }
        else
        {
            valuesMap.put("ORDER_BY", new StringBuilder("codice ").append(orderDir).toString());
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
            BeanPropertyRowMapper<AliquotaIvaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(AliquotaIvaDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco aliquote iva", e);
            throw new SQLException(e);
        }
    }

    public List<AliquotaIvaDto> getListForCombo() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<AliquotaIvaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(AliquotaIvaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("ALIQUOTEIVA_S04"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco aliquote iva per il popolamento della combo", e);
            throw new SQLException(e);
        }
    }

    public void insert(AliquotaIvaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ALIQUOTEIVA_I01"), dto.getCodice(), dto.getImposta(), dto.getIndetraibilita(), dto.getClasse(), StringUtils.isEmpty(dto.getDescrizione()) ? null : dto.getDescrizione(), StringUtils.isEmpty(dto.getNote()) ? null : dto.getNote(), dto.getPredefinita(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dell'aliquota iva", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String codice,
                              Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("ALIQUOTEIVA_S03"), Long.class, codice, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione dell'esistenza dell'aliquota iva con codice {}", codice, e);
            throw new SQLException(e);
        }
    }

    public void resetPredefinite(long idUser) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ALIQUOTEIVA_U02"), idUser);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel reset dell'qliquota iva predefinita", e);
            throw new SQLException(e);
        }
    }

    public void update(AliquotaIvaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ALIQUOTEIVA_U01"), dto.getCodice(), dto.getImposta(), dto.getIndetraibilita(), dto.getClasse(), StringUtils.isEmpty(dto.getDescrizione()) ? null : dto.getDescrizione(), StringUtils.isEmpty(dto.getNote()) ? null : dto.getNote(), dto.getPredefinita(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dell'aliquota iva {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

