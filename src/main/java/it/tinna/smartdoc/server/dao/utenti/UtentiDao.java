package it.tinna.smartdoc.server.dao.utenti;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;

public class UtentiDao extends BaseDao
{

    public UtentiDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void aggiornaPassword(long idUtente,
                                 String password) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("UTENTI_U01"), password, idUtente);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento della password per l'utente {}", idUtente, e);
            throw new SQLException(e);
        }
    }

    public UtenteDto getById(long idUtente) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<UtenteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(UtenteDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("UTENTI_S01"), rowMapper, idUtente);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessun utente trovato con id {}", idUtente);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'utente con id {}", idUtente, e);
            throw new SQLException(e);
        }
    }

    public UtenteDto getByUsername(String username) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<UtenteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(UtenteDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("UTENTI_S02"), rowMapper, username);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.info("Nessun utente trovato con username {}", username);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'utente con username {}", username, e);
            throw new SQLException(e);
        }
    }

}

