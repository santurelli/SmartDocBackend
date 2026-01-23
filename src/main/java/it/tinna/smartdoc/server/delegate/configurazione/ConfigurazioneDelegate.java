package it.tinna.smartdoc.server.delegate.configurazione;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.configurazione.ConfigurazioneDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.configurazione.ConfigurazioneDto;

@Service(value = "configurazioneDelegate")
public class ConfigurazioneDelegate extends BaseDelegate {

    public String[] getAsArray(String dominio, String chiave) {
        ConfigurazioneDao dao = new ConfigurazioneDao(jdbcTemplate);
        return dao.getAsArray(dominio, chiave);
    }

    public String getByKey(String dominio, String chiave) throws SQLException {
        ConfigurazioneDao dao = new ConfigurazioneDao(jdbcTemplate);
        return dao.getByKey(dominio, chiave);
    }

    public Map<String, ConfigurazioneDto> getByDomain(String domain) throws SQLException {
        ConfigurazioneDao dao = new ConfigurazioneDao(jdbcTemplate);
        return dao.getByDomain(domain);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void save(ConfigurazioneDto dto) throws SQLException {
        ConfigurazioneDao dao = new ConfigurazioneDao(jdbcTemplate);
        dao.delete(dto.getDominio(), dto.getChiave());
        dao.save(dto);
    }

    public java.util.List<ConfigurazioneDto> getAll() throws SQLException {
        ConfigurazioneDao dao = new ConfigurazioneDao(jdbcTemplate);
        return dao.getAll();
    }

}
