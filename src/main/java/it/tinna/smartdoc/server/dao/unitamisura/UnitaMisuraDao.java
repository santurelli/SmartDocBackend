package it.tinna.smartdoc.server.dao.unitamisura;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.unitamisura.UnitaMisuraDto;

@Repository
public class UnitaMisuraDao extends BaseDao {

    public UnitaMisuraDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<UnitaMisuraDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<UnitaMisuraDto> rowMapper = new BeanPropertyRowMapper<>(UnitaMisuraDto.class);
            // Uses UNITAMISURA_S05 as confirmed
            return jdbcTemplate.query(FileQueryReader.getQuery("UNITAMISURA_S05"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
