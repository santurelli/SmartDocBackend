package it.tinna.smartdoc.server.dao.tipiporto;

import java.sql.SQLException;
import java.util.List;

import it.tinna.smartdoc.server.database.FileQueryReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.tipiporto.TipoPortoDto;

@Repository
public class TipiPortoDao extends BaseDao {

    @Autowired
    public TipiPortoDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private final RowMapper<TipoPortoDto> rowMapper = (rs, rowNum) -> {
        TipoPortoDto dto = new TipoPortoDto();
        dto.setId(rs.getInt("id"));
        dto.setDescrizione(rs.getString("descrizione"));
        try {
            dto.setTotal(rs.getLong("total"));
        } catch (Exception e) {
            // total column might not be present in all queries
        }
        return dto;
    };

    public List<TipoPortoDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        String sql = FileQueryReader.getQuery("TIPIPORTO_S01");
        
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

    public List<TipoPortoDto> getListForCombo() throws SQLException {
        String sql = FileQueryReader.getQuery("TIPIPORTO_S04");
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    public TipoPortoDto getById(Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("TIPIPORTO_S02");
        List<TipoPortoDto> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void insert(TipoPortoDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("TIPIPORTO_I01");
        jdbcTemplate.update(sql, dto.getDescrizione(), userId);
    }

    public void update(TipoPortoDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("TIPIPORTO_U01");
        jdbcTemplate.update(sql, dto.getDescrizione(), userId, dto.getId());
    }

    public void delete(Integer id, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("TIPIPORTO_D01");
        jdbcTemplate.update(sql, userId, id);
    }
    
    public boolean checkUniqueness(String descrizione, Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("TIPIPORTO_S03");
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, descrizione, id);
        return count != null && count > 0;
    }
}

