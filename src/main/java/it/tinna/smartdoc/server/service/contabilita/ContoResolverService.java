package it.tinna.smartdoc.server.service.contabilita;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Risolve il conto contabile da usare per una riga di documento, seguendo la cascata:
 * conto specifico (articolo/cliente/fornitore) -> sottocategoria -> categoria -> conto generico di ruolo.
 * Usato dal motore di generazione automatica delle scritture contabili (Fase 3).
 */
public class ContoResolverService {

    private static final String SQL_CONTO_RICAVO_ARTICOLO =
            "SELECT COALESCE(p.k_conto_ricavo, sc.k_conto_ricavo, c.k_conto_ricavo) " +
            "  FROM d_e_prodotti p " +
            "  LEFT JOIN d_e_sottocategorie sc ON sc.k_d_e_sottocategorie = p.k_d_e_sottocategorie " +
            "  LEFT JOIN d_e_categorie c ON c.k_d_e_categorie = p.k_d_e_categorie " +
            " WHERE p.k_d_e_prodotti = ?";

    private static final String SQL_CONTO_COSTO_ARTICOLO =
            "SELECT COALESCE(p.k_conto_costo, sc.k_conto_costo, c.k_conto_costo) " +
            "  FROM d_e_prodotti p " +
            "  LEFT JOIN d_e_sottocategorie sc ON sc.k_d_e_sottocategorie = p.k_d_e_sottocategorie " +
            "  LEFT JOIN d_e_categorie c ON c.k_d_e_categorie = p.k_d_e_categorie " +
            " WHERE p.k_d_e_prodotti = ?";

    private static final String SQL_CONTO_CLIENTE =
            "SELECT k_conto_default FROM d_e_clienti WHERE k_d_e_clienti = ?";

    private static final String SQL_CONTO_FORNITORE =
            "SELECT k_conto_default FROM d_e_fornitori WHERE k_d_e_fornitori = ?";

    private static final String SQL_CONTO_RUOLO =
            "SELECT k_d_e_piano_conti FROM d_e_piano_conti WHERE ruolo_default = ? AND fl_deleted = 0 LIMIT 1";

    private static final String SQL_PERCENTUALE_IVA =
            "SELECT imposta FROM d_e_aliquoteiva WHERE k_d_e_aliquoteiva = ?";

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, Integer> cacheRuoli = new HashMap<>();
    private final Map<Long, Integer> cacheContoRicavoArticolo = new HashMap<>();
    private final Map<Long, Integer> cacheContoCostoArticolo = new HashMap<>();
    private final Map<Long, java.math.BigDecimal> cachePercentualeIva = new HashMap<>();

    public ContoResolverService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer resolveContoRuolo(String ruolo) throws SQLException {
        if (cacheRuoli.containsKey(ruolo)) {
            return cacheRuoli.get(ruolo);
        }
        Integer id = queryForNullableInt(SQL_CONTO_RUOLO, ruolo);
        cacheRuoli.put(ruolo, id);
        return id;
    }

    public Integer resolveContoRicavoArticolo(long idProdotto) throws SQLException {
        if (cacheContoRicavoArticolo.containsKey(idProdotto)) {
            return cacheContoRicavoArticolo.get(idProdotto);
        }
        Integer specifico = queryForNullableInt(SQL_CONTO_RICAVO_ARTICOLO, idProdotto);
        Integer resolved = specifico != null ? specifico : resolveContoRuolo("RICAVI_VENDITE");
        cacheContoRicavoArticolo.put(idProdotto, resolved);
        return resolved;
    }

    public Integer resolveContoCostoArticolo(long idProdotto) throws SQLException {
        if (cacheContoCostoArticolo.containsKey(idProdotto)) {
            return cacheContoCostoArticolo.get(idProdotto);
        }
        Integer specifico = queryForNullableInt(SQL_CONTO_COSTO_ARTICOLO, idProdotto);
        Integer resolved = specifico != null ? specifico : resolveContoRuolo("COSTI_ACQUISTI");
        cacheContoCostoArticolo.put(idProdotto, resolved);
        return resolved;
    }

    public Integer resolveContoCliente(long idCliente) throws SQLException {
        Integer specifico = queryForNullableInt(SQL_CONTO_CLIENTE, idCliente);
        return specifico != null ? specifico : resolveContoRuolo("CREDITI_CLIENTI");
    }

    public Integer resolveContoFornitore(long idFornitore) throws SQLException {
        Integer specifico = queryForNullableInt(SQL_CONTO_FORNITORE, idFornitore);
        return specifico != null ? specifico : resolveContoRuolo("DEBITI_FORNITORI");
    }

    public java.math.BigDecimal resolvePercentualeIva(long idAliquotaIva) throws SQLException {
        if (cachePercentualeIva.containsKey(idAliquotaIva)) {
            return cachePercentualeIva.get(idAliquotaIva);
        }
        try {
            java.math.BigDecimal val = jdbcTemplate.queryForObject(SQL_PERCENTUALE_IVA, java.math.BigDecimal.class, idAliquotaIva);
            java.math.BigDecimal resolved = val != null ? val : java.math.BigDecimal.ZERO;
            cachePercentualeIva.put(idAliquotaIva, resolved);
            return resolved;
        } catch (EmptyResultDataAccessException e) {
            cachePercentualeIva.put(idAliquotaIva, java.math.BigDecimal.ZERO);
            return java.math.BigDecimal.ZERO;
        }
    }

    private Integer queryForNullableInt(String sql, Object param) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, param);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
