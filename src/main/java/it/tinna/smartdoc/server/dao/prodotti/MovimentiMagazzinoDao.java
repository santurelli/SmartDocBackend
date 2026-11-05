package it.tinna.smartdoc.server.dao.prodotti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentoMagazzinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentiSearchCriteriaDto;

@Repository
public class MovimentiMagazzinoDao extends BaseDao {

    public MovimentiMagazzinoDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public Integer insertCarico(MovimentoMagazzinoDto dto) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("MOVIMENTIMAGAZZINO_I01"), Integer.class,
                dto.getIdProdotto(),
                dto.getTipoMovimento(),
                dto.getQuantita(),
                dto.getIdUnitaMisura() != null && dto.getIdUnitaMisura() != 0 ? dto.getIdUnitaMisura() : null,
                dto.getPrezzoUnitario(),
                dto.getIdAliquotaIva() != null && dto.getIdAliquotaIva() != 0 ? dto.getIdAliquotaIva() : null, // Can be null if not provided
                dto.getDataMovimento(),
                dto.getIdScelta() != null && dto.getIdScelta() != 0 ? dto.getIdScelta() : null, // Handle possibly 0 ids
                dto.getIdTono() != null && dto.getIdTono() != 0 ? dto.getIdTono() : null,
                dto.getIdTaglia() != null && dto.getIdTaglia() != 0 ? dto.getIdTaglia() : null,
                dto.getIdColore() != null && dto.getIdColore() != 0 ? dto.getIdColore() : null,
                dto.getIdMagazzino() != null && dto.getIdMagazzino() != 0 ? dto.getIdMagazzino() : null,
                dto.getIdFornitore() != null && dto.getIdFornitore() != 0 ? dto.getIdFornitore() : null,
                dto.getDescrCausale(),
                dto.getIdCausale() != null && dto.getIdCausale() != 0 ? dto.getIdCausale() : null,
                dto.getUserCreated()
            );
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public Integer insertScarico(MovimentoMagazzinoDto dto) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("MOVIMENTIMAGAZZINO_I02"), Integer.class,
                dto.getIdProdotto(),
                dto.getTipoMovimento(),
                dto.getQuantita(),
                dto.getIdUnitaMisura() != null && dto.getIdUnitaMisura() != 0 ? dto.getIdUnitaMisura() : null,
                dto.getPrezzoUnitario(),
                dto.getIdAliquotaIva() != null && dto.getIdAliquotaIva() != 0 ? dto.getIdAliquotaIva() : null,
                dto.getDataMovimento(),
                dto.getIdScelta() != null && dto.getIdScelta() != 0 ? dto.getIdScelta() : null,
                dto.getIdTono() != null && dto.getIdTono() != 0 ? dto.getIdTono() : null,
                dto.getIdTaglia() != null && dto.getIdTaglia() != 0 ? dto.getIdTaglia() : null,
                dto.getIdColore() != null && dto.getIdColore() != 0 ? dto.getIdColore() : null,
                dto.getIdMagazzino() != null && dto.getIdMagazzino() != 0 ? dto.getIdMagazzino() : null,
                dto.getIdCliente() != null && dto.getIdCliente() != 0 ? dto.getIdCliente() : null, // Changed from IdFornitore
                dto.getDescrCausale(),
                dto.getIdCausale() != null && dto.getIdCausale() != 0 ? dto.getIdCausale() : null,
                dto.getUserCreated()
            );
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }

    }

    public List<MovimentoMagazzinoDto> list(MovimentiSearchCriteriaDto criteria) throws SQLException {
        try {
            return jdbcTemplate.query(FileQueryReader.getQuery("MOVIMENTIMAGAZZINO_S01"),
                    new BeanPropertyRowMapper<>(MovimentoMagazzinoDto.class),
                    criteria.getDtFrom(),
                    criteria.getDtTo(),
                    criteria.getIdProdotto(), criteria.getIdProdotto(),
                    criteria.getLength(),
                    criteria.getStart()
            );
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }


    public void updateMovimento(MovimentoMagazzinoDto dto) throws SQLException {
        try {
            String dataStr = dto.getDataMovimento();
            if (dataStr != null && dataStr.contains("-")) {
                String[] parts = dataStr.split("-");
                if (parts.length == 3 && parts[0].length() == 4) {
                    dataStr = parts[2] + "/" + parts[1] + "/" + parts[0];
                }
            }
            String sql = "UPDATE d_e_movimenti_magazzino SET " +
                         "quantita = ?, " +
                         "prezzo_unitario = ?, " +
                         "data = TO_DATE(?, 'DD/MM/YYYY'), " +
                         "desc_causale = ?, " +
                         "user_last_update = ?, " +
                         "dt_last_update = CURRENT_TIMESTAMP " +
                         "WHERE k_d_e_movimenti_magazzino = ?";
            jdbcTemplate.update(sql,
                dto.getQuantita(),
                dto.getPrezzoUnitario(),
                dataStr,
                dto.getDescrCausale() != null ? dto.getDescrCausale() : dto.getCausale(),
                dto.getUserLastUpdate() != null ? dto.getUserLastUpdate().intValue() : 1,
                dto.getId()
            );
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void deleteMovimento(Integer id, Long userId) throws SQLException {
        try {
            String sql = "UPDATE d_e_movimenti_magazzino SET fl_deleted = 1, user_last_update = ?, dt_last_update = CURRENT_TIMESTAMP WHERE k_d_e_movimenti_magazzino = ?";
            jdbcTemplate.update(sql, userId != null ? userId.intValue() : 1, id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}