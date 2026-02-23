package it.tinna.smartdoc.server.delegate.utenti;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.utenti.UtentiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.constants.IErrorCodes;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;

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

}

