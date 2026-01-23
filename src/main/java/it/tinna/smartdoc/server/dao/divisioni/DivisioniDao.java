package it.tinna.smartdoc.server.dao.divisioni;

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
import it.tinna.smartdoc.shared.dto.divisioni.DivisioneDto;

@Repository
public class DivisioniDao extends BaseDao {

    public DivisioniDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<DivisioneDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<DivisioneDto> rowMapper = new BeanPropertyRowMapper<>(DivisioneDto.class);
            // Uses DIVISIONI_S05 as confirmed
            return jdbcTemplate.query(FileQueryReader.getQuery("DIVISIONI_S05"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
