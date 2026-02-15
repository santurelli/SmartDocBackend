package it.tinna.smartdoc.server.dao.aspettobeni;

import java.sql.SQLException;
import java.util.List;

import it.tinna.smartdoc.server.database.FileQueryReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.aspettobeni.AspettoBeniDto;

@Repository
public class AspettoBeniDao extends BaseDao {

    @Autowired
    public AspettoBeniDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private final RowMapper<AspettoBeniDto> rowMapper = (rs, rowNum) -> {
        AspettoBeniDto dto = new AspettoBeniDto();
        dto.setId(rs.getInt("id"));
        dto.setDescrizione(rs.getString("descrizione"));
        try {
            dto.setTotal(rs.getLong("total"));
        } catch (Exception e) {
            // total column might not be present in all queries
        }
        return dto;
    };

    public List<AspettoBeniDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        String sql = FileQueryReader.getQuery("ASPETTIBENI_S04");
        
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

    public List<AspettoBeniDto> getListForCombo() throws SQLException {
        String sql = FileQueryReader.getQuery("ASPETTIBENI_S01");
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    public AspettoBeniDto getById(Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("ASPETTIBENI_S02");
        List<AspettoBeniDto> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void insert(AspettoBeniDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("ASPETTIBENI_I01");
        jdbcTemplate.update(sql, dto.getDescrizione(), userId);
    }

    public void update(AspettoBeniDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("ASPETTIBENI_U01");
        jdbcTemplate.update(sql, dto.getDescrizione(), userId, dto.getId());
    }

    public void delete(Integer id, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("ASPETTIBENI_D01");
        jdbcTemplate.update(sql, userId, id);
    }
    
    public boolean checkUniqueness(String descrizione, Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("ASPETTIBENI_S03");
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, descrizione, id);
        return count != null && count > 0;
    }
}
