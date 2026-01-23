package it.tinna.smartdoc.server.dao.vettori;

import java.sql.SQLException;
import java.util.List;

import it.tinna.smartdoc.server.database.FileQueryReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.vettori.VettoreDto;

@Repository
public class VettoriDao extends BaseDao {

    @Autowired
    public VettoriDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private final RowMapper<VettoreDto> rowMapper = (rs, rowNum) -> {
        VettoreDto dto = new VettoreDto();
        dto.setId(rs.getInt("id"));
        dto.setDescrizione(rs.getString("descrizione"));
        try {
            dto.setTotal(rs.getLong("total"));
        } catch (Exception e) {
            // total column might not be present in all queries
        }
        return dto;
    };

    public List<VettoreDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        // VETTORI_S04 is the paginated query with total count
        String sql = FileQueryReader.getQuery("VETTORI_S04");
        
        String orderBy = "descrizione " + (orderDir != null ? orderDir : "asc");
        if (orderCol != null && orderCol == 1) { 
             orderBy = "descrizione " + (orderDir != null ? orderDir : "asc");
        }
        
        String limit = "";
        if (length != null && start != null) {
            limit = "LIMIT " + length + " OFFSET " + start;
        }

        sql = sql.replace("${ORDER_BY}", orderBy);
        sql = sql.replace("${LIMIT}", limit);
        
        String searchTerm = (search != null && !search.isEmpty()) ? "%" + search + "%" : null;
        return jdbcTemplate.query(sql, rowMapper, searchTerm);
    }

    public List<VettoreDto> getListForCombo() throws SQLException {
        // VETTORI_S01 is simple list ordered by description (no pagination info in XML provided)
        String sql = FileQueryReader.getQuery("VETTORI_S01");
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    public VettoreDto getById(Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("VETTORI_S02");
        List<VettoreDto> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void insert(VettoreDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("VETTORI_I01");
        jdbcTemplate.update(sql, dto.getDescrizione(), userId);
    }

    public void update(VettoreDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("VETTORI_U01");
        jdbcTemplate.update(sql, dto.getDescrizione(), userId, dto.getId());
    }

    public void delete(Integer id, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("VETTORI_D01");
        jdbcTemplate.update(sql, userId, id);
    }
    
    public boolean checkUniqueness(String descrizione, Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("VETTORI_S03");
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, descrizione, id);
        return count != null && count > 0;
    }
}
