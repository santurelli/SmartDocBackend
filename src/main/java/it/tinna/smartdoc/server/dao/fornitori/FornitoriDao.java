package it.tinna.smartdoc.server.dao.fornitori;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;

@Repository
public class FornitoriDao extends BaseDao {

    public FornitoriDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<FornitoreDto> getListForCombo() throws SQLException {
        try {
            BeanPropertyRowMapper<FornitoreDto> rowMapper = new BeanPropertyRowMapper<>(FornitoreDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FORNITORI_S09"), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
    
    public List<FornitoreDto> getSuggestion(String query) throws SQLException {
        try {
            BeanPropertyRowMapper<FornitoreDto> rowMapper = new BeanPropertyRowMapper<>(FornitoreDto.class);
            String search = StringUtils.defaultIfEmpty(StringUtility.formatForLike(query.toLowerCase()), null);
            return jdbcTemplate.query(FileQueryReader.getQuery("FORNITORI_S06"), rowMapper, search, search, search);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
