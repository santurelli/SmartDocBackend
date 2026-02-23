package it.tinna.smartdoc.server.dao.causalitrasporto;

import java.sql.SQLException;
import java.util.List;

import it.tinna.smartdoc.server.database.FileQueryReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.documenti.CausaleTrasportoDto;

@Repository
public class CausaliTrasportoDao extends BaseDao {

    @Autowired
    public CausaliTrasportoDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private final RowMapper<CausaleTrasportoDto> rowMapper = (rs, rowNum) -> {
        CausaleTrasportoDto dto = new CausaleTrasportoDto();
        dto.setId(rs.getInt("id"));
        dto.setDescrizione(rs.getString("descrizione"));
        dto.setPredefinita(rs.getInt("predefinita"));
        try {
            dto.setTotal(rs.getLong("total"));
        } catch (Exception e) {
            // total column might not be present in all queries
        }
        return dto;
    };

    public List<CausaleTrasportoDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        String sql = FileQueryReader.getQuery("CAUSALITRASPORTO_S01");
        
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

    public List<CausaleTrasportoDto> getListForCombo() throws SQLException {
        String sql = FileQueryReader.getQuery("CAUSALITRASPORTO_S04");
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    public CausaleTrasportoDto getById(Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("CAUSALITRASPORTO_S02");
        List<CausaleTrasportoDto> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void insert(CausaleTrasportoDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("CAUSALITRASPORTO_I01");
        jdbcTemplate.update(sql, dto.getDescrizione(), dto.getPredefinita(), userId);
    }

    public void update(CausaleTrasportoDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("CAUSALITRASPORTO_U01");
        jdbcTemplate.update(sql, dto.getDescrizione(), dto.getPredefinita(), userId, dto.getId());
    }

    public void resetPredefinita(Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("CAUSALITRASPORTO_U02");
        jdbcTemplate.update(sql, userId);
    }

    public void delete(Integer id, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("CAUSALITRASPORTO_D01");
        jdbcTemplate.update(sql, userId, id);
    }
    
    public boolean checkUniqueness(String descrizione, Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("CAUSALITRASPORTO_S03");
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, descrizione, id);
        return count != null && count > 0;
    }
}

