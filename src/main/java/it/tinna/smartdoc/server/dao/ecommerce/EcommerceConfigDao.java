package it.tinna.smartdoc.server.dao.ecommerce;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import it.tinna.smartdoc.shared.dto.ecommerce.EcommerceConfigDto;
import it.tinna.smartdoc.shared.dto.ecommerce.EcommerceOrdineImportatoDto;

/**
 * Accesso diretto via JdbcTemplate (nessuna voce in query.xml): la RLS su tenant_id
 * si occupa da sola dello scoping per tenant, come nel resto del modulo di integrazione esterna.
 */
public class EcommerceConfigDao {

    private final JdbcTemplate jdbcTemplate;

    public EcommerceConfigDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<EcommerceConfigDto> CONFIG_MAPPER = (ResultSet rs, int rowNum) -> {
        EcommerceConfigDto dto = new EcommerceConfigDto();
        dto.setId(rs.getLong("k_d_e_ecommerce_config"));
        dto.setPiattaforma(rs.getString("piattaforma"));
        dto.setStoreUrl(rs.getString("store_url"));
        dto.setConsumerKey(rs.getString("consumer_key"));
        dto.setConsumerSecret(rs.getString("consumer_secret"));
        dto.setStatoOrdineWoo(rs.getString("stato_ordine_woo"));
        dto.setFlAbilitato(rs.getInt("fl_abilitato"));
        dto.setFlCreaDdt(rs.getInt("fl_crea_ddt"));
        dto.setFlCreaFattura(rs.getInt("fl_crea_fattura"));
        dto.setIntervalloMinuti(rs.getInt("intervallo_minuti"));
        java.sql.Timestamp ultimoSync = rs.getTimestamp("dt_ultimo_sync");
        dto.setDtUltimoSync(ultimoSync != null ? ultimoSync.toString() : null);
        dto.setUltimoEsito(rs.getString("ultimo_esito"));
        return dto;
    };

    public EcommerceConfigDto getForCurrentTenant() {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM d_e_ecommerce_config LIMIT 1", CONFIG_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<EcommerceConfigDto> getAbilitatiPerScheduler() {
        return jdbcTemplate.query(
            "SELECT * FROM d_e_ecommerce_config WHERE fl_abilitato = 1", CONFIG_MAPPER);
    }

    public void upsert(EcommerceConfigDto dto) {
        EcommerceConfigDto existing = getForCurrentTenant();
        if (existing == null) {
            jdbcTemplate.update(
                "INSERT INTO d_e_ecommerce_config " +
                "(piattaforma, store_url, consumer_key, consumer_secret, stato_ordine_woo, fl_abilitato, fl_crea_ddt, fl_crea_fattura, intervallo_minuti) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                dto.getPiattaforma(), dto.getStoreUrl(), dto.getConsumerKey(), dto.getConsumerSecret(),
                dto.getStatoOrdineWoo(), dto.getFlAbilitato(), dto.getFlCreaDdt(), dto.getFlCreaFattura(),
                dto.getIntervalloMinuti());
        } else {
            jdbcTemplate.update(
                "UPDATE d_e_ecommerce_config SET store_url = ?, consumer_key = ?, consumer_secret = ?, " +
                "stato_ordine_woo = ?, fl_abilitato = ?, fl_crea_ddt = ?, fl_crea_fattura = ?, intervallo_minuti = ? " +
                "WHERE k_d_e_ecommerce_config = ?",
                dto.getStoreUrl(), dto.getConsumerKey(), dto.getConsumerSecret(), dto.getStatoOrdineWoo(),
                dto.getFlAbilitato(), dto.getFlCreaDdt(), dto.getFlCreaFattura(), dto.getIntervalloMinuti(),
                existing.getId());
        }
    }

    public void aggiornaEsitoSync(long idConfig, String esito) {
        jdbcTemplate.update(
            "UPDATE d_e_ecommerce_config SET dt_ultimo_sync = now(), ultimo_esito = ? WHERE k_d_e_ecommerce_config = ?",
            esito, idConfig);
    }

    public boolean isOrdineGiaImportato(String idOrdineEsterno) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM d_e_ecommerce_ordini_importati WHERE id_ordine_esterno = ?",
            Integer.class, idOrdineEsterno);
        return count != null && count > 0;
    }

    public void registraOrdineImportato(String idOrdineEsterno, String numeroOrdineEsterno, Integer idDdt, Integer idFattura, String esito, String dettaglioEsito) {
        jdbcTemplate.update(
            "INSERT INTO d_e_ecommerce_ordini_importati " +
            "(id_ordine_esterno, numero_ordine_esterno, k_d_e_ddt, k_d_e_fatture, esito, dettaglio_esito) " +
            "VALUES (?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (id_ordine_esterno, tenant_id) DO UPDATE SET " +
            "k_d_e_ddt = EXCLUDED.k_d_e_ddt, k_d_e_fatture = EXCLUDED.k_d_e_fatture, " +
            "esito = EXCLUDED.esito, dettaglio_esito = EXCLUDED.dettaglio_esito, dt_importazione = now()",
            idOrdineEsterno, numeroOrdineEsterno, idDdt, idFattura, esito, dettaglioEsito);
    }

    public List<EcommerceOrdineImportatoDto> getLog(int limit) {
        return jdbcTemplate.query(
            "SELECT * FROM d_e_ecommerce_ordini_importati ORDER BY dt_importazione DESC LIMIT ?",
            (ResultSet rs, int rowNum) -> {
                EcommerceOrdineImportatoDto dto = new EcommerceOrdineImportatoDto();
                dto.setIdOrdineEsterno(rs.getString("id_ordine_esterno"));
                dto.setNumeroOrdineEsterno(rs.getString("numero_ordine_esterno"));
                dto.setIdDdt((Integer) rs.getObject("k_d_e_ddt"));
                dto.setIdFattura((Integer) rs.getObject("k_d_e_fatture"));
                java.sql.Timestamp dt = rs.getTimestamp("dt_importazione");
                dto.setDtImportazione(dt != null ? dt.toString() : null);
                dto.setEsito(rs.getString("esito"));
                dto.setDettaglioEsito(rs.getString("dettaglio_esito"));
                return dto;
            }, limit);
    }

    public LocalDateTime getUltimoSync(EcommerceConfigDto config) {
        if (config == null || config.getDtUltimoSync() == null) {
            return null;
        }
        try {
            return java.sql.Timestamp.valueOf(config.getDtUltimoSync()).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }
}
