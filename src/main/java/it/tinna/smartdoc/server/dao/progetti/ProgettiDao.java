package it.tinna.smartdoc.server.dao.progetti;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.progetti.ProgettoDto;

import it.tinna.smartdoc.server.database.FileQueryReader;

public class ProgettiDao extends BaseDao {

    public ProgettiDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<ProgettoDto> getSuggestion(String q) throws SQLException {
        String query = FileQueryReader.getQuery("PROGETTI_SUGGESTION");
        if (query == null) {
            query = "SELECT k_d_e_progetti as id, descrizione FROM d_e_progetti WHERE fl_deleted = 0 AND LOWER(descrizione) LIKE LOWER(?) ORDER BY descrizione";
        }
        String search = "%" + q + "%";
        return jdbcTemplate.query(query, new ProgettoMapper(), search);
    }

    public ProgettoDto getById(Long id) throws SQLException {
        String query = "SELECT k_d_e_progetti as id, descrizione FROM d_e_progetti WHERE k_d_e_progetti = ?";
        List<ProgettoDto> list = jdbcTemplate.query(query, new ProgettoMapper(), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Integer insert(ProgettoDto dto) throws SQLException {
        String query = "INSERT INTO d_e_progetti (descrizione, note, fl_deleted, dt_created) VALUES (?, ?, 0, CURRENT_TIMESTAMP) RETURNING k_d_e_progetti";
        return jdbcTemplate.queryForObject(query, Integer.class, dto.getDescrizione(), dto.getNote());
    }

    public List<ProgettoDto> getListForCombo() throws SQLException {
        String query = "SELECT k_d_e_progetti as id, descrizione FROM d_e_progetti WHERE fl_deleted = 0 ORDER BY descrizione";
        return jdbcTemplate.query(query, new ProgettoMapper());
    }

    private class ProgettoMapper implements RowMapper<ProgettoDto> {
        @Override
        public ProgettoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProgettoDto dto = new ProgettoDto();
            dto.setId(rs.getLong("id"));
            dto.setDescrizione(rs.getString("descrizione"));
            return dto;
        }
    }
}

