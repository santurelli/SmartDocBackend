package it.tinna.smartdoc.server.dao.contabilita;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.contabilita.MastrinoRigaDto;
import it.tinna.smartdoc.shared.dto.contabilita.MovimentoContabileRigaDto;
import it.tinna.smartdoc.shared.dto.contabilita.RegistrazioneContabileDto;

public class RegistrazioneContabileDao extends BaseDao {

    public RegistrazioneContabileDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public long insertTestata(RegistrazioneContabileDto dto) throws SQLException {
        try {
            String sql = "INSERT INTO d_e_registrazioni_contabili " +
                    "(data_registrazione, descrizione, tipo_documento, id_documento, numero_documento, totale_dare, totale_avere, user_created, dt_created) " +
                    "VALUES (?::date, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) RETURNING k_d_e_registrazioni_contabili";
            return jdbcTemplate.queryForObject(sql, Long.class,
                    dto.getDataRegistrazione(), dto.getDescrizione(), dto.getTipoDocumento(), dto.getIdDocumento(),
                    dto.getNumeroDocumento(), dto.getTotaleDare(), dto.getTotaleAvere(), dto.getUserCreated());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Elimina la registrazione (e le righe collegate, via ON DELETE CASCADE) precedentemente generata
     * per un documento, cosi' da poterla rigenerare da zero in caso di modifica del documento.
     */
    public void deleteByDocumento(String tipoDocumento, long idDocumento) throws SQLException {
        try {
            jdbcTemplate.update("DELETE FROM d_e_registrazioni_contabili WHERE tipo_documento = ?::varchar AND id_documento = ?",
                    tipoDocumento, idDocumento);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void insertRiga(long idRegistrazione, MovimentoContabileRigaDto riga) throws SQLException {
        try {
            String sql = "INSERT INTO d_e_movimenti_contabili " +
                    "(k_registrazione, k_conto, importo_dare, importo_avere, descrizione, n_progr) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, idRegistrazione, riga.getIdConto(), riga.getImportoDare(), riga.getImportoAvere(), riga.getDescrizione(), riga.getnProgr());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<RegistrazioneContabileDto> getList(String tipoDocumento, String search) throws SQLException {
        return getList(tipoDocumento, search, null, null);
    }

    /**
     * dataDa/dataA (formato ISO yyyy-MM-dd) sono opzionali: null = nessun limite su quel lato.
     */
    public List<RegistrazioneContabileDto> getList(String tipoDocumento, String search, String dataDa, String dataA) throws SQLException {
        try {
            String sql = "SELECT k_d_e_registrazioni_contabili AS id, TO_CHAR(data_registrazione, 'YYYY-MM-DD') AS dataRegistrazione, descrizione, " +
                    "tipo_documento AS tipoDocumento, id_documento AS idDocumento, numero_documento AS numeroDocumento, " +
                    "totale_dare AS totaleDare, totale_avere AS totaleAvere " +
                    "FROM d_e_registrazioni_contabili " +
                    "WHERE fl_deleted = 0 " +
                    "AND (?::varchar IS NULL OR tipo_documento = ?::varchar) " +
                    "AND (?::varchar IS NULL OR LOWER(numero_documento) LIKE LOWER(?::varchar) OR LOWER(descrizione) LIKE LOWER(?::varchar)) " +
                    "AND (?::date IS NULL OR data_registrazione >= ?::date) " +
                    "AND (?::date IS NULL OR data_registrazione <= ?::date) " +
                    "ORDER BY data_registrazione DESC, k_d_e_registrazioni_contabili DESC";
            String like = search != null && !search.isEmpty() ? "%" + search + "%" : null;
            BeanPropertyRowMapper<RegistrazioneContabileDto> rowMapper = new BeanPropertyRowMapper<>(RegistrazioneContabileDto.class);
            return jdbcTemplate.query(sql, rowMapper, tipoDocumento, tipoDocumento, like, like, like, dataDa, dataDa, dataA, dataA);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Movimenti di un singolo conto, in ordine cronologico, per costruire il mastrino.
     * dataDa/dataA (formato ISO yyyy-MM-dd) sono opzionali: null = nessun limite su quel lato.
     */
    public List<MastrinoRigaDto> getMovimentiPerConto(long idConto, String dataDa, String dataA) throws SQLException {
        try {
            String sql = "SELECT TO_CHAR(r.data_registrazione, 'YYYY-MM-DD') AS dataRegistrazione, m.descrizione, " +
                    "r.tipo_documento AS tipoDocumento, r.numero_documento AS numeroDocumento, " +
                    "m.importo_dare AS importoDare, m.importo_avere AS importoAvere " +
                    "FROM d_e_movimenti_contabili m " +
                    "JOIN d_e_registrazioni_contabili r ON r.k_d_e_registrazioni_contabili = m.k_registrazione " +
                    "WHERE m.k_conto = ? AND r.fl_deleted = 0 " +
                    "AND (?::date IS NULL OR r.data_registrazione >= ?::date) " +
                    "AND (?::date IS NULL OR r.data_registrazione <= ?::date) " +
                    "ORDER BY r.data_registrazione, r.k_d_e_registrazioni_contabili, m.n_progr";
            BeanPropertyRowMapper<MastrinoRigaDto> rowMapper = new BeanPropertyRowMapper<>(MastrinoRigaDto.class);
            return jdbcTemplate.query(sql, rowMapper, idConto, dataDa, dataDa, dataA, dataA);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public RegistrazioneContabileDto getById(long id) throws SQLException {
        try {
            String sql = "SELECT k_d_e_registrazioni_contabili AS id, TO_CHAR(data_registrazione, 'YYYY-MM-DD') AS dataRegistrazione, descrizione, " +
                    "tipo_documento AS tipoDocumento, id_documento AS idDocumento, numero_documento AS numeroDocumento, " +
                    "totale_dare AS totaleDare, totale_avere AS totaleAvere " +
                    "FROM d_e_registrazioni_contabili WHERE k_d_e_registrazioni_contabili = ? AND fl_deleted = 0";
            BeanPropertyRowMapper<RegistrazioneContabileDto> rowMapper = new BeanPropertyRowMapper<>(RegistrazioneContabileDto.class);
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void updateTestata(long id, RegistrazioneContabileDto dto) throws SQLException {
        try {
            String sql = "UPDATE d_e_registrazioni_contabili SET data_registrazione = ?::date, descrizione = ?, " +
                    "numero_documento = ?, totale_dare = ?, totale_avere = ? WHERE k_d_e_registrazioni_contabili = ?";
            jdbcTemplate.update(sql, dto.getDataRegistrazione(), dto.getDescrizione(), dto.getNumeroDocumento(),
                    dto.getTotaleDare(), dto.getTotaleAvere(), id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void deleteRighe(long idRegistrazione) throws SQLException {
        try {
            jdbcTemplate.update("DELETE FROM d_e_movimenti_contabili WHERE k_registrazione = ?", idRegistrazione);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Soft delete: a differenza di deleteByDocumento (usato per rigenerare le scritture automatiche),
     * le scritture manuali cancellate restano in tabella con fl_deleted = 1 per mantenere una traccia.
     */
    public void softDelete(long id) throws SQLException {
        try {
            jdbcTemplate.update("UPDATE d_e_registrazioni_contabili SET fl_deleted = 1 WHERE k_d_e_registrazioni_contabili = ?", id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<MovimentoContabileRigaDto> getRighe(long idRegistrazione) throws SQLException {
        try {
            String sql = "SELECT m.k_d_e_movimenti_contabili AS id, m.k_registrazione AS registrazioneId, m.k_conto AS idConto, " +
                    "pc.codice AS codiceConto, pc.descrizione AS descrizioneConto, " +
                    "m.importo_dare AS importoDare, m.importo_avere AS importoAvere, m.descrizione, m.n_progr AS nProgr " +
                    "FROM d_e_movimenti_contabili m " +
                    "JOIN d_e_piano_conti pc ON pc.k_d_e_piano_conti = m.k_conto " +
                    "WHERE m.k_registrazione = ? ORDER BY m.n_progr";
            BeanPropertyRowMapper<MovimentoContabileRigaDto> rowMapper = new BeanPropertyRowMapper<>(MovimentoContabileRigaDto.class);
            return jdbcTemplate.query(sql, rowMapper, idRegistrazione);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
