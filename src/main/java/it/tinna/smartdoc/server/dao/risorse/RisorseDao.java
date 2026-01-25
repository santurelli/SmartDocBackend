package it.tinna.smartdoc.server.dao.risorse;

import java.sql.SQLException;
import java.util.List;

import it.tinna.smartdoc.server.database.FileQueryReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.risorse.RisorsaDto;

@Repository
public class RisorseDao extends BaseDao {

    @Autowired
    public RisorseDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private final RowMapper<RisorsaDto> rowMapper = (rs, rowNum) -> {
        RisorsaDto dto = new RisorsaDto();
        dto.setId(rs.getInt("id"));
        dto.setTipologia(rs.getString("tipologia"));
        dto.setDescrizione(rs.getString("descrizione"));
        dto.setSaldoIniziale(rs.getDouble("saldoIniziale"));
        dto.setCodSia(rs.getString("codSia"));
        dto.setDescBanca(rs.getString("descBanca"));
        dto.setIban(rs.getString("iban"));
        dto.setCin(rs.getString("cin"));
        dto.setAbi(rs.getString("abi"));
        dto.setCab(rs.getString("cab"));
        dto.setConto(rs.getString("conto"));
        dto.setBic(rs.getString("bic"));
        dto.setNote(rs.getString("note"));
        dto.setPredefinita(rs.getInt("predefinita"));
        try {
            dto.setTotal(rs.getLong("total"));
        } catch (Exception e) {
            // total column check
        }
        return dto;
    };

    public List<RisorsaDto> getList(String tipologia, String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        String sql = FileQueryReader.getQuery("RISORSE_S01");
        
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
        return jdbcTemplate.query(sql, rowMapper, tipologia, searchTerm);
    }

    public List<RisorsaDto> getListForCombo(String tipologia) throws SQLException {
        String sql = FileQueryReader.getQuery("RISORSE_S06");
        return jdbcTemplate.query(sql, rowMapper, tipologia);
    }
    
    public RisorsaDto getById(Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("RISORSE_S02");
        List<RisorsaDto> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void insert(RisorsaDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("RISORSE_I01");
        jdbcTemplate.update(sql, 
            dto.getTipologia(),
            dto.getDescrizione(),
            dto.getSaldoIniziale(),
            dto.getCodSia(),
            dto.getDescBanca(),
            dto.getIban(),
            dto.getCin(),
            dto.getAbi(),
            dto.getCab(),
            dto.getConto(),
            dto.getBic(),
            dto.getNote(),
            dto.getPredefinita(),
            userId
        );
    }

    public void update(RisorsaDto dto, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("RISORSE_U01");
        jdbcTemplate.update(sql, 
            dto.getTipologia(),
            dto.getDescrizione(),
            dto.getSaldoIniziale(),
            dto.getCodSia(),
            dto.getDescBanca(),
            dto.getIban(),
            dto.getCin(),
            dto.getAbi(),
            dto.getCab(),
            dto.getConto(),
            dto.getBic(),
            dto.getNote(),
            dto.getPredefinita(),
            userId,
            dto.getId()
        );
    }
    
    public void resetPredefinita(String tipologia, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("RISORSE_U02");
        jdbcTemplate.update(sql, userId, tipologia);
    }

    public void delete(Integer id, Integer userId) throws SQLException {
        String sql = FileQueryReader.getQuery("RISORSE_D01");
        jdbcTemplate.update(sql, userId, id);
    }
    
    public boolean checkUniqueness(String tipologia, String descrizione, Integer id) throws SQLException {
        String sql = FileQueryReader.getQuery("RISORSE_S03");
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tipologia, descrizione, id);
        return count != null && count > 0;
    }
}
