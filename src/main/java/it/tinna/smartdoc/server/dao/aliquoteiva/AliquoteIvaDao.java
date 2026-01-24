package it.tinna.smartdoc.server.dao.aliquoteiva;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;

@Repository
public class AliquoteIvaDao extends BaseDao {

    public AliquoteIvaDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<AliquotaIvaDto> getList(String search, Integer start, Integer length, Integer orderColumn, String orderDir) throws SQLException {
        try {
            BeanPropertyRowMapper<AliquotaIvaDto> rowMapper = new BeanPropertyRowMapper<>(AliquotaIvaDto.class);
            String query = FileQueryReader.getQuery("ALIQUOTEIVA_S01");
            query = query.replace("${ORDER_BY}", getOrderBy(orderColumn, orderDir));
            query = query.replace("${LIMIT}", getLimit(start, length));
            String searchLike = StringUtility.formatForLikeHelper(search);
            // S01 expects: LOWER(codice) LIKE ? OR LOWER(descrizione) LIKE ?
            return jdbcTemplate.query(query, rowMapper, searchLike, searchLike);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<AliquotaIvaDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<AliquotaIvaDto> rowMapper = new BeanPropertyRowMapper<>(AliquotaIvaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("ALIQUOTEIVA_S04"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public AliquotaIvaDto getById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<AliquotaIvaDto> rowMapper = new BeanPropertyRowMapper<>(AliquotaIvaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("ALIQUOTEIVA_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public boolean checkUniqueness(String descrizione, Long id) throws SQLException {
        try {
            Integer count = jdbcTemplate.queryForObject(FileQueryReader.getQuery("ALIQUOTEIVA_S03"), Integer.class, descrizione, id);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void insert(AliquotaIvaDto dto, Integer userId) throws SQLException {
        try {
            // I01: codice, imposta, indetraibilita, classe, descrizione, note, fl_predefinita, user_created
            jdbcTemplate.update(FileQueryReader.getQuery("ALIQUOTEIVA_I01"), 
                dto.getCodice(), 
                dto.getImposta(), 
                dto.getIndetraibilita() != null ? dto.getIndetraibilita() : 0.0, 
                dto.getClasse(), 
                dto.getDescrizione(), 
                dto.getNote(), 
                dto.getPredefinita() != null ? dto.getPredefinita() : 0, 
                userId);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void update(AliquotaIvaDto dto, Integer userId) throws SQLException {
        try {
            // U01: codice, imposta, indetraibilita, classe, descrizione, note, fl_predefinita, user_last_update, id
            jdbcTemplate.update(FileQueryReader.getQuery("ALIQUOTEIVA_U01"), 
                dto.getCodice(), 
                dto.getImposta(), 
                dto.getIndetraibilita() != null ? dto.getIndetraibilita() : 0.0, 
                dto.getClasse(), 
                dto.getDescrizione(), 
                dto.getNote(), 
                dto.getPredefinita() != null ? dto.getPredefinita() : 0, 
                userId, 
                dto.getId());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void delete(long id, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("ALIQUOTEIVA_D01"), userId, id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void resetPredefinita(Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("ALIQUOTEIVA_U02"), userId);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    private String getOrderBy(Integer orderColumn, String orderDir) {
        String col = "codice"; // Default to codice
        if (orderColumn != null) {
            switch (orderColumn) {
                case 0:
                    col = "codice";
                    break;
                case 1:
                    col = "descrizione";
                    break;
                case 2:
                    col = "imposta";
                    break;
                default:
                    col = "codice";
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
