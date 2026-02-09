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

    public List<TonoArticoloDto> getList(String search, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        try {
            BeanPropertyRowMapper<TonoArticoloDto> rowMapper = new BeanPropertyRowMapper<>(TonoArticoloDto.class);
            String query = FileQueryReader.getQuery("TONIARTICOLO_S01");
            
            String orderBy = "descrizione";
            if (orderColumn != null) {
                switch (orderColumn) {
                    case 0: orderBy = "descrizione"; break;
                }
            }
            query = query.replace("${ORDER_BY}", orderBy + " " + orderDir);
            query = query.replace("${LIMIT}", length != null ? "LIMIT " + length + " OFFSET " + start : "");

            return jdbcTemplate.query(query, rowMapper, 
                search != null ? "%" + search + "%" : null);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
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

    public TonoArticoloDto getById(Integer id) throws SQLException {
        try {
            BeanPropertyRowMapper<TonoArticoloDto> rowMapper = new BeanPropertyRowMapper<>(TonoArticoloDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("TONIARTICOLO_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void insert(TonoArticoloDto dto, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("TONIARTICOLO_I01"), dto.getDescrizione(), userId);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void update(TonoArticoloDto dto, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("TONIARTICOLO_U01"), dto.getDescrizione(), userId, dto.getId());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void delete(Integer id, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("TONIARTICOLO_D01"), userId, id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        try {
            Long count = jdbcTemplate.queryForObject(FileQueryReader.getQuery("TONIARTICOLO_S03"), Long.class, descrizione, id);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
