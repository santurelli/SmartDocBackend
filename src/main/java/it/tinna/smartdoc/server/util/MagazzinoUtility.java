package it.tinna.smartdoc.server.util;

import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.database.FileQueryReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MagazzinoUtility {

    private MagazzinoUtility() {
    }

    public static Integer getMagazzinoPredefinito(JdbcTemplate jdbcTemplate) {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("MAGAZZINI_S_PREDEFINITO"), Integer.class);
        } catch (Exception e) {
            log.warn("Magazzino predefinito non trovato, uso fallback MIN(k_d_e_magazzini)");
            try {
                return jdbcTemplate.queryForObject("SELECT MIN(k_d_e_magazzini) FROM d_e_magazzini WHERE fl_deleted = 0", Integer.class);
            } catch (Exception ex) {
                log.error("Impossibile determinare un magazzino predefinito", ex);
                return null;
            }
        }
    }
}
