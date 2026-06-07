package it.tinna.smartdoc.server.delegate.utenti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.utenti.UtentiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.constants.IErrorCodes;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;
import it.tinna.smartdoc.shared.dto.login.GruppoDto;

@Transactional(readOnly = true)
@Service(value = "utentiDelegate")
public class UtentiDelegate extends BaseDelegate
{

    // public UtentiDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }
    @Autowired
    private PasswordEncoder passwordEncoder;

    public long aggiornaPassword(long idUtente,
                                 String vecchiaPassword,
                                 String password) throws SQLException
    {
        UtentiDao dao = new UtentiDao(jdbcTemplate);
        UtenteDto utenteDto = dao.getById(idUtente);
        if ( utenteDto != null )
        {
            if ( passwordEncoder.matches(vecchiaPassword, utenteDto.getPassword()) )
            {
                dao.aggiornaPassword(idUtente, passwordEncoder.encode(password));
                return IErrorCodes.NOERROR;
            }
            else
            {
                return IErrorCodes.CAMBIOPASSWORD_VECCHIAPASSWORDERRATA;
            }
        }
        else
        {
            return IErrorCodes.CAMBIOPASSWORD_UTENTENONTROVATO;
        }
    }

    public UtenteDto getByUsername(String username) throws SQLException
    {
        UtentiDao dao = new UtentiDao(jdbcTemplate);
        return dao.getByUsername(username);
    }
    
    public UtenteDto getById(long idUtente) throws SQLException
    {
        UtentiDao dao = new UtentiDao(jdbcTemplate);
        return dao.getById(idUtente);
    }

    public List<UtenteDto> getUtenti(String searchParam, String orderBy, String limit) throws SQLException
    {
        UtentiDao dao = new UtentiDao(jdbcTemplate);
        return dao.getUtenti(searchParam, orderBy, limit);
    }

    public long insertUtente(UtenteDto dto) throws SQLException
    {
        UtentiDao dao = new UtentiDao(jdbcTemplate);
        if (dao.existsByUsername(dto.getUsername(), 0)) {
            throw new SQLException("Username gi\u00E0 presente");
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return dao.insertUtente(dto);
    }

    public void updateUtente(UtenteDto dto) throws SQLException
    {
        UtentiDao dao = new UtentiDao(jdbcTemplate);
        if (dao.existsByUsername(dto.getUsername(), dto.getId())) {
             throw new SQLException("Username gi\u00E0 presente");
        }
        dao.updateUtente(dto);
    }

    public void deleteUtente(long idUtente) throws SQLException
    {
        UtentiDao dao = new UtentiDao(jdbcTemplate);
        dao.deleteUtente(idUtente);
    }

    public List<GruppoDto> getGruppi() throws SQLException
    {
        UtentiDao dao = new UtentiDao(jdbcTemplate);
        return dao.getGruppi();
    }

}

