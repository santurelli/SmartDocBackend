package it.tinna.smartdoc.server.delegate.login;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.login.LoginDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;

@Transactional(readOnly = true)
@Service(value = "loginDelegate")
public class LoginDelegate extends BaseDelegate {

    @Autowired
    private ConfigurazioneDelegate configurazioneDelegate;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UtenteDto getUserByUsername(String username) throws SQLException {
        LoginDao dao = new LoginDao(jdbcTemplate);
        UtenteDto user = dao.getUserByUsername(username);
        
        if (user != null) {
            try {
                String tipoStore = configurazioneDelegate.getByKey(ISharedConstants.CONFIG_DOMAIN_GLOBAL, ISharedConstants.CONFIG_KEY_TIPOSTORE);
                user.setTipoStore(StringUtils.defaultIfEmpty(tipoStore, StringUtils.EMPTY));
            } catch (Exception e) {
                // Ignore error, default to empty
                user.setTipoStore("");
            }
        }
        
        return user;
    }

    public UtenteDto getUserByUserPwd(String userid, String password) throws SQLException {
        LoginDao dao = new LoginDao(jdbcTemplate);
        return dao.getUserByUserPwd(userid, password);
    }

    public void setDtLastLogin(UtenteDto utente) throws SQLException {
        LoginDao dao = new LoginDao(jdbcTemplate);
        dao.setDtLastLogin(utente);
    }

    public void updatePassword(long userId, String newPassword) throws SQLException {
        LoginDao dao = new LoginDao(jdbcTemplate);
        String hashed = passwordEncoder.encode(newPassword);
        dao.updatePassword(userId, hashed);
    }

}

