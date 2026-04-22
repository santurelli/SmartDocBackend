package it.tinna.smartdoc.server.dao.login;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;

public class LoginDao extends BaseDao {

    public LoginDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public UtenteDto getUserByUsername(String username) throws SQLException {
        try {
            BeanPropertyRowMapper<UtenteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(UtenteDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("LOGIN_S03"), rowMapper, username);
        } catch (EmptyResultDataAccessException e) {
            _log.info("Login fallito");
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore durante il login", e);
            throw new SQLException(e);
        }
    }

    public UtenteDto getUserByUserPwd(String userid, String password) throws SQLException {
        try {
            BeanPropertyRowMapper<UtenteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(UtenteDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("LOGIN_S01"), rowMapper, userid, password);
        } catch (EmptyResultDataAccessException e) {
            _log.info("Login fallito");
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore durante il login", e);
            throw new SQLException(e);
        }
    }

    public void setDtLastLogin(UtenteDto utente) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("LOGIN_U01"), utente.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'impostazione della data di ultimo accesso per l'utente {}", utente.getId(), e);
            throw new SQLException(e);
        }
    }

    public void updatePassword(long userId, String newPasswordHash) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("LOGIN_U02"), newPasswordHash, userId);
        } catch (DataAccessException e) {
            _log.error("Errore nell'aggiornamento della password per l'utente {}", userId, e);
            throw new SQLException(e);
        }
    }

}

