package it.tinna.smartdoc.server.dao.causaliesigibilitadifferita;

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
import it.tinna.smartdoc.shared.dto.causaliesigibilitadifferita.CausaleEsigibilitaDifferitaDto;

public class CausaliEsigibilitaDifferitaDao extends BaseDao
{

    public CausaliEsigibilitaDifferitaDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(long idUser,
                       long idCausale) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ESIGIBILITADIFFERITA_D01"), idUser, idCausale);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'eliminazione della causale di esigibilità differita con id {}", idCausale, e);
            throw new SQLException(e);
        }
    }

    public CausaleEsigibilitaDifferitaDto getById(int id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<CausaleEsigibilitaDifferitaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(CausaleEsigibilitaDifferitaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("ESIGIBILITADIFFERITA_S02"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna causale di esigibilità differita con id {} trovata", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della causale di esigibilità differita con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<CausaleEsigibilitaDifferitaDto> getList(String strToSearch,
                                                        Integer length,
                                                        Integer start,
                                                        Integer orderColumn,
                                                        String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("ESIGIBILITADIFFERITA_S01");
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
            BeanPropertyRowMapper<CausaleEsigibilitaDifferitaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(CausaleEsigibilitaDifferitaDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco causali di esigibilità differita", e);
            throw new SQLException(e);
        }
    }

    public List<CausaleEsigibilitaDifferitaDto> getListForCombo() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<CausaleEsigibilitaDifferitaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(CausaleEsigibilitaDifferitaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("ESIGIBILITADIFFERITA_S04"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco causali di esigibilità differita per il popolamento della combo", e);
            throw new SQLException(e);
        }
    }

    public void insert(CausaleEsigibilitaDifferitaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ESIGIBILITADIFFERITA_I01"), dto.getDescrizione(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento della causale di esigibilità differita", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("ESIGIBILITADIFFERITA_S03"), Long.class, descrizione, id);
            return l > 0;

        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella verifica dell'esistenza della causale di esigibilità differita con descrizione {}", descrizione, e);
            throw new SQLException(e);
        }
    }

    public void update(CausaleEsigibilitaDifferitaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ESIGIBILITADIFFERITA_U01"), dto.getDescrizione(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento della causale di esigibilità differita con id {}", dto.getId());
            throw new SQLException(e);
        }
    }

}

