package it.tinna.smartdoc.server.dao.sceltearticolo;

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
import it.tinna.smartdoc.shared.dto.sceltearticolo.SceltaArticoloDto;

@Repository
public class ScelteArticoloDao extends BaseDao {

    public ScelteArticoloDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<SceltaArticoloDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<SceltaArticoloDto> rowMapper = new BeanPropertyRowMapper<>(SceltaArticoloDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("SCELTEARTICOLO_S04"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
