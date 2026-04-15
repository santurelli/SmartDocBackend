package it.tinna.smartdoc.server.dao.nazioni;

import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.nazioni.NazioneDto;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class NazioniDao extends BaseDao {

    public NazioniDao(@org.springframework.beans.factory.annotation.Qualifier("serviceJdbcTemplate") JdbcTemplate serviceJdbcTemplate) {
        super(serviceJdbcTemplate);
    }

    public List<NazioneDto> getAll() throws SQLException {
        String sql = "SELECT k_d_e_nazioni as id, nome, codice_iso FROM d_e_nazioni ORDER BY nome";
        try {
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(NazioneDto.class));
        } catch (Exception e) {
            log.error("Errore nel recupero delle nazioni", e);
            throw new SQLException(e);
        }
    }

    public String getCodiceIsoByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) return "IT";
        
        String sql = "SELECT codice_iso FROM d_e_nazioni WHERE UPPER(nome) = UPPER(?) OR UPPER(codice_iso) = UPPER(?) LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, nome.trim(), nome.trim());
        } catch (Exception e) {
            log.warn("Nazione '{}' non trovata in tabella d_e_nazioni, ritorno IT di default", nome);
            return "IT";
        }
    }
}
