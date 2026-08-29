package it.tinna.smartdoc.server.dao.cassa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.shared.dto.cassa.PrevisioneCassaContoDto;

public class PrevisioneCassaDao {

    /**
     * Riga grezza di movimento previsto: una rata di scadenza (incasso o pagamento), con
     * importo gia' segnato (positivo = incasso/entrata, negativo = pagamento/uscita) secondo
     * la stessa convenzione usata in STATISTICHE_S07 (nota credito riduce l'incasso, nota
     * credito fornitore riduce l'uscita).
     */
    public static class MovimentoScadenza {
        public Integer idRisorsa;
        public String data; // dd/MM/yyyy
        public BigDecimal importo; // gia' segnato
        public boolean saldato;
        public String tipo; // INCASSO | PAGAMENTO
        public String tipoDocumento;
        public String numeroDocumento;
        public String soggetto;
    }

    private final JdbcTemplate jdbcTemplate;

    public PrevisioneCassaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PrevisioneCassaContoDto> getContiConSaldoIniziale() throws SQLException {
        try {
            return jdbcTemplate.query(
                "SELECT k_d_e_risorse AS id, descrizione, COALESCE(saldo_iniziale, 0) AS saldo_iniziale " +
                "FROM d_e_risorse WHERE tipologia = 'BA' AND fl_deleted = 0 ORDER BY descrizione",
                (rs, rowNum) -> {
                    PrevisioneCassaContoDto dto = new PrevisioneCassaContoDto();
                    dto.setIdRisorsa(rs.getInt("id"));
                    dto.setDescrizioneConto(rs.getString("descrizione"));
                    dto.setSaldoIniziale(rs.getBigDecimal("saldo_iniziale"));
                    return dto;
                });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public List<MovimentoScadenza> getMovimenti() throws SQLException {
        String sql =
            "SELECT sp.k_d_e_risorse AS idRisorsa, TO_CHAR(sp.data, 'DD/MM/YYYY') AS data, " +
            "       sp.importo AS importo, sp.saldato AS saldato, 'INCASSO' AS tipo, " +
            "       'Fattura' AS tipoDocumento, f.num_fattura AS numeroDocumento, c.denominazione AS soggetto " +
            "  FROM d_e_scadenzepagamentifatture sp " +
            "  JOIN d_e_fatture f ON f.k_d_e_fatture = sp.k_d_e_fatture AND f.fl_deleted = 0 " +
            "  LEFT JOIN d_e_clienti c ON c.k_d_e_clienti = f.k_d_e_clienti " +
            "UNION ALL " +
            "SELECT sp.k_d_e_risorse, TO_CHAR(sp.data, 'DD/MM/YYYY'), " +
            "       -sp.importo, sp.saldato, 'INCASSO', " +
            "       'Nota Credito', nc.num_notacredito, c.denominazione " +
            "  FROM d_e_scadenzepagamentinotecredito sp " +
            "  JOIN d_e_notecredito nc ON nc.k_d_e_notecredito = sp.k_d_e_notecredito AND nc.fl_deleted = 0 " +
            "  LEFT JOIN d_e_clienti c ON c.k_d_e_clienti = nc.k_d_e_clienti " +
            "UNION ALL " +
            "SELECT sp.k_d_e_risorse, TO_CHAR(sp.data, 'DD/MM/YYYY'), " +
            "       -sp.importo, sp.saldato, 'PAGAMENTO', " +
            "       'Fattura Fornitore', ff.num_fattura, fo.denominazione " +
            "  FROM d_e_scadenzepagamentifatturefornitore sp " +
            "  JOIN d_e_fatturefornitore ff ON ff.k_d_e_fatturefornitore = sp.k_d_e_fatturefornitore AND ff.fl_deleted = 0 " +
            "  LEFT JOIN d_e_fornitori fo ON fo.k_d_e_fornitori = ff.k_d_e_fornitori " +
            "UNION ALL " +
            "SELECT sp.k_d_e_risorse, TO_CHAR(sp.data, 'DD/MM/YYYY'), " +
            "       sp.importo, sp.saldato, 'PAGAMENTO', " +
            "       'Nota Credito Fornitore', ncf.num_notacredito, fo.denominazione " +
            "  FROM d_e_scadenzepagamentinotecreditofornitore sp " +
            "  JOIN d_e_notecreditofornitore ncf ON ncf.k_d_e_notecreditofornitore = sp.k_d_e_notecreditofornitore AND ncf.fl_deleted = 0 " +
            "  LEFT JOIN d_e_fornitori fo ON fo.k_d_e_fornitori = ncf.k_d_e_fornitori";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                MovimentoScadenza m = new MovimentoScadenza();
                int idRisorsa = rs.getInt("idRisorsa");
                m.idRisorsa = rs.wasNull() ? null : idRisorsa;
                m.data = rs.getString("data");
                m.importo = rs.getBigDecimal("importo");
                m.saldato = rs.getInt("saldato") == 1;
                m.tipo = rs.getString("tipo");
                m.tipoDocumento = rs.getString("tipoDocumento");
                m.numeroDocumento = rs.getString("numeroDocumento");
                m.soggetto = rs.getString("soggetto");
                return m;
            });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
