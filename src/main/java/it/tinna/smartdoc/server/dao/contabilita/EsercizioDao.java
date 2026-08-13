package it.tinna.smartdoc.server.dao.contabilita;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.contabilita.EsercizioDto;

/**
 * Stato di apertura/chiusura degli esercizi contabili e saldi di apertura riportati anno su anno
 * per i conti patrimoniali (ATTIVITA/PASSIVITA/PATRIMONIO_NETTO/IVA) - vedi ChiusuraEsercizioDelegate.
 */
public class EsercizioDao extends BaseDao {

    public EsercizioDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public EsercizioDto getEsercizio(int anno) throws SQLException {
        try {
            String sql = "SELECT k_d_e_esercizi AS id, anno, stato, TO_CHAR(dt_chiusura, 'YYYY-MM-DD HH24:MI') AS dtChiusura " +
                    "FROM d_e_esercizi WHERE anno = ?";
            BeanPropertyRowMapper<EsercizioDto> rowMapper = new BeanPropertyRowMapper<>(EsercizioDto.class);
            return jdbcTemplate.queryForObject(sql, rowMapper, anno);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Garantisce che esista una riga per l'esercizio (in stato APERTO se nuova): idempotente, non tocca
     * lo stato se l'esercizio esiste gia'.
     */
    public void assicuraEsercizioAperto(int anno) throws SQLException {
        try {
            jdbcTemplate.update("INSERT INTO d_e_esercizi (anno, stato) VALUES (?, 'APERTO') " +
                    "ON CONFLICT (anno, tenant_id) DO NOTHING", anno);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Upsert: se l'esercizio non aveva mai avuto una riga esplicita in d_e_esercizi (caso comune, dato
     * che finora "aperto" era solo lo stato implicito di default) la crea gia' chiusa, invece di fare
     * un UPDATE che silenziosamente non tocca nessuna riga.
     */
    public void chiudiEsercizio(int anno, Long userId) throws SQLException {
        try {
            jdbcTemplate.update("INSERT INTO d_e_esercizi (anno, stato, dt_chiusura, user_chiusura) VALUES (?, 'CHIUSO', CURRENT_TIMESTAMP, ?) " +
                    "ON CONFLICT (anno, tenant_id) DO UPDATE SET stato = 'CHIUSO', dt_chiusura = CURRENT_TIMESTAMP, user_chiusura = EXCLUDED.user_chiusura",
                    anno, userId);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public BigDecimal getSaldoApertura(long idConto, int anno) throws SQLException {
        try {
            BigDecimal saldo = jdbcTemplate.queryForObject(
                    "SELECT saldo FROM d_e_saldi_apertura WHERE k_conto = ? AND anno = ?", BigDecimal.class, idConto, anno);
            return saldo != null ? saldo : BigDecimal.ZERO;
        } catch (EmptyResultDataAccessException e) {
            return BigDecimal.ZERO;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void upsertSaldoApertura(long idConto, int anno, BigDecimal saldo) throws SQLException {
        try {
            jdbcTemplate.update("INSERT INTO d_e_saldi_apertura (k_conto, anno, saldo) VALUES (?, ?, ?) " +
                    "ON CONFLICT (k_conto, anno, tenant_id) DO UPDATE SET saldo = EXCLUDED.saldo",
                    idConto, anno, saldo);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Saldo (Dare - Avere) dei movimenti registrati nell'anno solare indicato, per ogni conto che ha
     * avuto almeno un movimento in quell'anno. I conti senza movimenti nell'anno non compaiono nella mappa.
     */
    public Map<Long, BigDecimal> getSaldiMovimentiAnno(int anno) throws SQLException {
        try {
            String sql = "SELECT m.k_conto AS idConto, SUM(m.importo_dare) - SUM(m.importo_avere) AS saldo " +
                    "FROM d_e_movimenti_contabili m " +
                    "JOIN d_e_registrazioni_contabili r ON r.k_d_e_registrazioni_contabili = m.k_registrazione " +
                    "WHERE r.fl_deleted = 0 AND EXTRACT(YEAR FROM r.data_registrazione) = ? " +
                    "GROUP BY m.k_conto";
            Map<Long, BigDecimal> result = new HashMap<>();
            jdbcTemplate.query(sql, rs -> {
                result.put(rs.getLong("idConto"), rs.getBigDecimal("saldo"));
            }, anno);
            return result;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
