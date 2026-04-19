package it.tinna.smartdoc.server.dao.utenti;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;
import it.tinna.smartdoc.shared.dto.login.GruppoDto;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.jdbc.core.PreparedStatementCreator;

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

    public List<UtenteDto> getUtenti(String searchParam, String orderBy, String limit) throws SQLException
    {
        try
        {
            String query = FileQueryReader.getQuery("UTENTI_S03");
            query = query.replace("${ORDER_BY}", orderBy != null ? orderBy : "nome, cognome");
            query = query.replace("${LIMIT}", limit != null ? limit : "");
            
            BeanPropertyRowMapper<UtenteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(UtenteDto.class);
            
            String searchStr = searchParam != null && !searchParam.trim().isEmpty() ? "%" + searchParam.trim() + "%" : null;
            return jdbcTemplate.query(query, rowMapper, searchStr, searchStr, searchStr);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero degli utenti", e);
            throw new SQLException(e);
        }
    }

    public boolean existsByUsername(String username, long idUtente) throws SQLException
    {
        try
        {
            long count = jdbcTemplate.queryForObject(FileQueryReader.getQuery("UTENTI_S04"), Long.class, username, idUtente);
            return count > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel controllo dell'esistenza dell'username {}", username, e);
            throw new SQLException(e);
        }
    }

    public long insertUtente(UtenteDto dto) throws SQLException
    {
        try
        {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator()
            {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException
                {
                    PreparedStatement ps = con.prepareStatement(FileQueryReader.getQuery("UTENTI_I01"), Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, dto.getNome());
                    ps.setString(2, dto.getCognome());
                    ps.setString(3, dto.getUsername());
                    ps.setString(4, dto.getPassword());
                    ps.setString(5, dto.getEmail());
                    if (dto.getGruppo() != null) {
                        ps.setInt(6, dto.getGruppo());
                    } else {
                        ps.setNull(6, java.sql.Types.INTEGER);
                    }
                    return ps;
                }
            }, keyHolder);
            
            if (keyHolder.getKeyList().isEmpty()) {
                 return 0; // Or whatever fallback
            }
            return ((Number) keyHolder.getKeyList().get(0).get("k_d_e_utenti")).longValue();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dell'utente {}", dto.getUsername(), e);
            throw new SQLException(e);
        }
    }

    public void updateUtente(UtenteDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("UTENTI_U02"), dto.getNome(), dto.getCognome(), dto.getUsername(), dto.getEmail(), dto.getGruppo(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dell'utente {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void deleteUtente(long idUtente) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("UTENTI_D01"), idUtente);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione dell'utente {}", idUtente, e);
            throw new SQLException(e);
        }
    }

    public List<GruppoDto> getGruppi() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<GruppoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(GruppoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("GRUPPI_S01"), rowMapper);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei gruppi", e);
            throw new SQLException(e);
        }
    }

}

