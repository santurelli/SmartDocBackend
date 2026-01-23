package it.tinna.smartdoc.server.dao.formatiarticolo;

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
import it.tinna.smartdoc.shared.dto.formatiarticolo.FormatoArticoloDto;

@Repository
public class FormatiArticoloDao extends BaseDao {

    public FormatiArticoloDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<FormatoArticoloDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<FormatoArticoloDto> rowMapper = new BeanPropertyRowMapper<>(FormatoArticoloDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FORMATIARTICOLO_S04"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
