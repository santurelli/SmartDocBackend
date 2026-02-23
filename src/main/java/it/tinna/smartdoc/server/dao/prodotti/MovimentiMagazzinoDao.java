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
                dto.getIdAliquotaIva(), // Can be null if not provided
                dto.getDataMovimento(),
                dto.getIdScelta() != null && dto.getIdScelta() != 0 ? dto.getIdScelta() : null, // Handle possibly 0 ids
                dto.getIdTono() != null && dto.getIdTono() != 0 ? dto.getIdTono() : null,
                dto.getIdTaglia() != null && dto.getIdTaglia() != 0 ? dto.getIdTaglia() : null,
                dto.getIdColore() != null && dto.getIdColore() != 0 ? dto.getIdColore() : null,
                dto.getIdMagazzino(),
                dto.getIdFornitore(),
                dto.getDescrCausale(),
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
                dto.getIdAliquotaIva(),
                dto.getDataMovimento(),
                dto.getIdScelta() != null && dto.getIdScelta() != 0 ? dto.getIdScelta() : null,
                dto.getIdTono() != null && dto.getIdTono() != 0 ? dto.getIdTono() : null,
                dto.getIdTaglia() != null && dto.getIdTaglia() != 0 ? dto.getIdTaglia() : null,
                dto.getIdColore() != null && dto.getIdColore() != 0 ? dto.getIdColore() : null,
                dto.getIdMagazzino(),
                dto.getIdCliente(), // Changed from IdFornitore
                dto.getDescrCausale(),
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

}

