package it.tinna.smartdoc.server.dao.dipendenti;

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
import it.tinna.smartdoc.shared.dto.avvisi.AvvisoDto;
import it.tinna.smartdoc.shared.dto.dipendenti.DipendenteDto;

public class DipendentiDao extends BaseDao
{

    public DipendentiDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(long idUser,
                       long idDipendente) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("DIPENDENTI_D01"), idUser, idDipendente);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione del dipendente {}", idDipendente);
            throw new SQLException(e);
        }
    }

    public DipendenteDto getById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<DipendenteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DipendenteDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("DIPENDENTI_S02"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessun dipendente trovato con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del dipendente {}", id);
            throw new SQLException(e);
        }
    }

    public List<DipendenteDto> getList(String strToSearch,
                                       int length,
                                       Integer start,
                                       Integer orderColumn,
                                       String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("DIPENDENTI_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        Map<String, String> valuesMap = new HashMap<>();
        if ( orderColumn == 0 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("cognome ").append(orderDir).toString());
        }
        else if ( orderColumn == 1 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("nome ").append(orderDir).toString());
        }
        else
        {
            valuesMap.put("ORDER_BY", new StringBuilder("cognome ").append(orderDir).toString());
        }
        if ( length != -1 && start != null )
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
            BeanPropertyRowMapper<DipendenteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DipendenteDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco dipendenti", e);
            throw new SQLException(e);
        }
    }

    public List<AvvisoDto> getListForCombo() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<AvvisoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(AvvisoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("AVVISI_S04"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco avvisi per il popolamento della combo", e);
            throw new SQLException(e);
        }
    }

    public void insert(DipendenteDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("DIPENDENTI_I01"), dto.getCognome(), dto.getNome(), StringUtils.defaultIfBlank(dto.getDtNascita(), null), StringUtils.defaultIfBlank(dto.getCodiceFiscale(), null), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio del dipendente", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String cognome,
                              String nome,
                              Long id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("DIPENDENTI_S03"), Long.class, cognome, nome, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione dell'esistenza del dipendente con cognome {} e nome {}", cognome, nome, e);
            throw new SQLException(e);
        }
    }

    public void update(DipendenteDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("DIPENDENTI_U01"), dto.getCognome(), dto.getNome(), StringUtils.defaultIfBlank(dto.getDtNascita(), null), StringUtils.defaultIfBlank(dto.getCodiceFiscale(), null), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento del dipendente {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

