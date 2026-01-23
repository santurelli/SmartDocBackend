package it.tinna.smartdoc.server.dao.avvisi;

import java.sql.SQLException;
import java.util.List;

import it.tinna.smartdoc.server.database.FileQueryReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.avvisi.AvvisoDto;

@Repository
public class AvvisiDao extends BaseDao {

    @Autowired
    public AvvisiDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private final RowMapper<AvvisoDto> rowMapper = (rs, rowNum) -> {
        AvvisoDto dto = new AvvisoDto();
        dto.setId(rs.getInt("id"));
        dto.setDescrizione(rs.getString("descrizione"));
        return dto;
    };

    public List<AvvisoDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        String sql = FileQueryReader.getQuery("AVVISI_S01");
        
        String orderBy = "descrizione " + (orderDir != null ? orderDir : "asc");
        if (orderCol != null && orderCol == 1) { 
             orderBy = "descrizione " + orderDir;
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

    public List<AvvisoDto> getListForCombo() throws SQLException {
        String sql = FileQueryReader.getQuery("AVVISI_S04");
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    public AvvisoDto getById(Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("AVVISI_S02");
        List<AvvisoDto> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void insert(AvvisoDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("AVVISI_I01");
        // params: descrizione, user_created
        jdbcTemplate.update(sql, dto.getDescrizione(), userId);
    }

    public void update(AvvisoDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("AVVISI_U01");
        // params: descrizione, user_last_update, id
        jdbcTemplate.update(sql, dto.getDescrizione(), userId, dto.getId());
    }

    public void delete(Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("AVVISI_D01");
        jdbcTemplate.update(sql, id);
    }
    
    public boolean checkUniqueness(String descrizione, Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("AVVISI_S03");
        // Params: descrizione, id (for exclude)
        // XML: lower(descrizione) = lower(?) AND k_d_e_avvisi != COALESCE(?, 0)
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, descrizione, id);
        return count != null && count > 0;
    }
}
