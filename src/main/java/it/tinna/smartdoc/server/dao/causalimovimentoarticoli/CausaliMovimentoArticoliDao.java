package it.tinna.smartdoc.server.dao.causalimovimentoarticoli;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.causalimovimenti.CausaleMovimentoDto;

@Repository
public class CausaliMovimentoArticoliDao extends BaseDao {

    public CausaliMovimentoArticoliDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void delete(long idUser, long idCausale) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CAUSALIMOVIMENTOARTICOLI_D01"), idUser, idCausale);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public CausaleMovimentoDto getById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<CausaleMovimentoDto> rowMapper = new BeanPropertyRowMapper<>(CausaleMovimentoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CAUSALIMOVIMENTOARTICOLI_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<CausaleMovimentoDto> getList(String strToSearch, int length, int start, int orderColumn, String orderDir) throws SQLException {
        String query = FileQueryReader.getQuery("CAUSALIMOVIMENTOARTICOLI_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtility.formatForLikeHelper(strToSearch)); // Using Helper to handle nulls if needed, or manual check

        String orderBy = "descrizione";
        if (orderColumn == 0) {
            orderBy = "descrizione";
        }
        
        query = query.replace("${ORDER_BY}", orderBy + " " + orderDir);
        
        if (length > 0 && start >= 0) {
            query = query.replace("${LIMIT}", "OFFSET " + start + " LIMIT " + length);
        } else {
             query = query.replace("${LIMIT}", "");
        }

        try {
            BeanPropertyRowMapper<CausaleMovimentoDto> rowMapper = new BeanPropertyRowMapper<>(CausaleMovimentoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<CausaleMovimentoDto> getSuggestion(String q) throws SQLException {
        try {
            BeanPropertyRowMapper<CausaleMovimentoDto> rowMapper = new BeanPropertyRowMapper<>(CausaleMovimentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("CAUSALIMOVIMENTOARTICOLI_S04"), rowMapper, StringUtility.formatForLikeHelper(q));
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void insert(CausaleMovimentoDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CAUSALIMOVIMENTOARTICOLI_I01"), dto.getDescrizione(), dto.getUserCreated());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String descrizione, long id) throws SQLException {
        try {
            Long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("CAUSALIMOVIMENTOARTICOLI_S03"), Long.class, descrizione, id);
            return l != null && l > 0;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void update(CausaleMovimentoDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CAUSALIMOVIMENTOARTICOLI_U01"), dto.getDescrizione(), dto.getUserLastUpdate(), dto.getId());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
