package it.tinna.smartdoc.server.dao.toniarticolo;

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
import it.tinna.smartdoc.shared.dto.toniarticolo.TonoArticoloDto;

@Repository
public class ToniArticoloDao extends BaseDao {

    public ToniArticoloDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<TonoArticoloDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<TonoArticoloDto> rowMapper = new BeanPropertyRowMapper<>(TonoArticoloDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("TONIARTICOLO_S04"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
