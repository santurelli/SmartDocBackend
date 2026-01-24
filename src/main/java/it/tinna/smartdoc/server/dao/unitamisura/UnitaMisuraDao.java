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
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.unitamisura.UnitaMisuraDto;

@Repository
public class UnitaMisuraDao extends BaseDao {

    public UnitaMisuraDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<UnitaMisuraDto> getList(String search, Integer start, Integer length, Integer orderColumn, String orderDir) throws SQLException {
        try {
            BeanPropertyRowMapper<UnitaMisuraDto> rowMapper = new BeanPropertyRowMapper<>(UnitaMisuraDto.class);
            String query = FileQueryReader.getQuery("UNITAMISURA_S01");
            query = query.replace("${ORDER_BY}", getOrderBy(orderColumn, orderDir));
            query = query.replace("${LIMIT}", getLimit(start, length));
            String searchLike = StringUtility.formatForLikeHelper(search);
            return jdbcTemplate.query(query, rowMapper, searchLike);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<UnitaMisuraDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<UnitaMisuraDto> rowMapper = new BeanPropertyRowMapper<>(UnitaMisuraDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("UNITAMISURA_S05"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public UnitaMisuraDto getById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<UnitaMisuraDto> rowMapper = new BeanPropertyRowMapper<>(UnitaMisuraDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("UNITAMISURA_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public boolean checkUniqueness(String descrizione, Long id) throws SQLException {
        try {
            Integer count = jdbcTemplate.queryForObject(FileQueryReader.getQuery("UNITAMISURA_S03"), Integer.class, descrizione, id);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void insert(UnitaMisuraDto dto, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("UNITAMISURA_I01"), dto.getDescrizione(), userId);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void update(UnitaMisuraDto dto, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("UNITAMISURA_U01"), dto.getDescrizione(), userId, dto.getId());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void delete(long id, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("UNITAMISURA_D01"), userId, id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    private String getOrderBy(Integer orderColumn, String orderDir) {
        String col = "descrizione";
        if (orderColumn != null) {
            switch (orderColumn) {
                case 0:
                    col = "descrizione";
                    break;
                default:
                    col = "descrizione";
                    break;
            }
        }
        return col + " " + (orderDir != null ? orderDir : "ASC");
    }

    private String getLimit(Integer start, Integer length) {
        if (start != null && length != null) {
            return "LIMIT " + length + " OFFSET " + start;
        }
        return "";
    }
}
