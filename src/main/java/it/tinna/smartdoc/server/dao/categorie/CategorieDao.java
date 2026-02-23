package it.tinna.smartdoc.server.dao.categorie;

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
import it.tinna.smartdoc.shared.dto.categorie.CategoriaDto;

@Repository
public class CategorieDao extends BaseDao {

    public CategorieDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<CategoriaDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        try {
            String query = FileQueryReader.getQuery("CATEGORIE_S01");
            
            String orderBy = "1";
            if (orderCol != null) {
                orderBy = (orderCol + 1) + " " + (orderDir != null ? orderDir : "ASC");
            }
            query = query.replace("${ORDER_BY}", orderBy);
            
            String limit = "";
            if (length != null && length != -1) {
                limit = "LIMIT " + length + " OFFSET " + (start != null ? start : 0);
            }
            query = query.replace("${LIMIT}", limit);
            
            BeanPropertyRowMapper<CategoriaDto> rowMapper = new BeanPropertyRowMapper<>(CategoriaDto.class);
            return jdbcTemplate.query(query, rowMapper, search != null ? "%" + search + "%" : null);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<CategoriaDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<CategoriaDto> rowMapper = new BeanPropertyRowMapper<>(CategoriaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("CATEGORIE_S04"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public CategoriaDto getById(Integer id) throws SQLException {
        try {
            BeanPropertyRowMapper<CategoriaDto> rowMapper = new BeanPropertyRowMapper<>(CategoriaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CATEGORIE_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void insert(CategoriaDto dto, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CATEGORIE_I01"), dto.getDescrizione(), userId);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void update(CategoriaDto dto, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CATEGORIE_U01"), dto.getDescrizione(), userId, dto.getId());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void delete(Integer id, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CATEGORIE_D01"), userId, id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public boolean checkUniqueness(String descrizione, Integer id) throws SQLException {
        try {
            Integer count = jdbcTemplate.queryForObject(FileQueryReader.getQuery("CATEGORIE_S03"), Integer.class, descrizione, id);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}

