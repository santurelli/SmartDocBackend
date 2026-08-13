package it.tinna.smartdoc.server.dao.sottocategorie;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.sottocategorie.SottoCategoriaDto;

@Repository
public class SottoCategorieDao extends BaseDao {

    public SottoCategorieDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private final RowMapper<SottoCategoriaDto> rowMapper = new BeanPropertyRowMapper<>(SottoCategoriaDto.class);

    public List<SottoCategoriaDto> getList(Integer idCategoria, String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        try {
            String sql = FileQueryReader.getQuery("SOTTOCATEGORIE_S01");
            
            String orderBy = "d_e_sottocategorie.descrizione " + (orderDir != null ? orderDir : "asc");
            if (orderCol != null && orderCol == 1) { 
                 orderBy = "d_e_sottocategorie.descrizione " + (orderDir != null ? orderDir : "asc");
            } else if (orderCol != null && orderCol == 2) {
                 orderBy = "d_e_categorie.descrizione " + (orderDir != null ? orderDir : "asc");
            }
            
            String limit = "";
            if (length != null && start != null) {
                limit = "LIMIT " + length + " OFFSET " + start;
            }

            sql = sql.replace("${ORDER_BY}", orderBy);
            sql = sql.replace("${LIMIT}", limit);

            String searchTerm = (search != null && !search.isEmpty()) ? "%" + search + "%" : null;

            return jdbcTemplate.query(sql, rowMapper, idCategoria, searchTerm);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<SottoCategoriaDto> getListForCombo(Integer idCategoria) throws SQLException {
        try {
            return jdbcTemplate.query(FileQueryReader.getQuery("SOTTOCATEGORIE_S04"), rowMapper, idCategoria);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public SottoCategoriaDto getById(Integer id) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("SOTTOCATEGORIE_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void insert(SottoCategoriaDto dto, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("SOTTOCATEGORIE_I01"),
                    dto.getParentId(), dto.getDescrizione(), dto.getIdContoRicavo(), dto.getIdContoCosto(), userId);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void update(SottoCategoriaDto dto, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("SOTTOCATEGORIE_U01"),
                    dto.getDescrizione(), dto.getIdContoRicavo(), dto.getIdContoCosto(), userId, dto.getId());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void delete(Integer id, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("SOTTOCATEGORIE_D01"), userId, id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public boolean checkUniqueness(Integer parentId, String descrizione, Integer id) throws SQLException {
        try {
            Integer count = jdbcTemplate.queryForObject(FileQueryReader.getQuery("SOTTOCATEGORIE_S03"), 
                    Integer.class, parentId, descrizione, id);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}

