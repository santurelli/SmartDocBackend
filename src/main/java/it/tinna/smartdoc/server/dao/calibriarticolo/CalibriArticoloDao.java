package it.tinna.smartdoc.server.dao.calibriarticolo;

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
import it.tinna.smartdoc.shared.dto.calibriarticolo.CalibroArticoloDto;

@Repository
public class CalibriArticoloDao extends BaseDao {

    public CalibriArticoloDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<CalibroArticoloDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<CalibroArticoloDto> rowMapper = new BeanPropertyRowMapper<>(CalibroArticoloDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("CALIBRIARTICOLO_S04"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
