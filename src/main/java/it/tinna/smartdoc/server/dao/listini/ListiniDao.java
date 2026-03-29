package it.tinna.smartdoc.server.dao.listini;

import java.sql.SQLException;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.listini.ListinoDto;
import org.springframework.stereotype.Repository;

@Repository
public class ListiniDao extends BaseDao {

    public ListiniDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<ListinoDto> getListForCombo() throws SQLException {
        BeanPropertyRowMapper<ListinoDto> rowMapper = new BeanPropertyRowMapper<>(ListinoDto.class);
        return jdbcTemplate.query(FileQueryReader.getQuery("LISTINI_S04"), rowMapper);
    }

    public List<ListinoDto> getAll() throws SQLException {
        BeanPropertyRowMapper<ListinoDto> rowMapper = new BeanPropertyRowMapper<>(ListinoDto.class);
        return jdbcTemplate.query(FileQueryReader.getQuery("LISTINI_S01"), rowMapper);
    }

    public ListinoDto getById(Long id) throws SQLException {
        try {
            BeanPropertyRowMapper<ListinoDto> rowMapper = new BeanPropertyRowMapper<>(ListinoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("LISTINI_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public Long insert(ListinoDto dto, Integer user) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("LISTINI_I01"), Long.class,
                dto.getDescrizione(),
                dto.getIdParent(),
                dto.getDerivationSource(),
                dto.getDerivationType(),
                dto.getDerivationValue(),
                dto.getRoundingRule(),
                dto.getFlDefault(),
                user
            );
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void update(ListinoDto dto, Integer user) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("LISTINI_U01"),
                dto.getDescrizione(),
                dto.getIdParent(),
                dto.getDerivationSource(),
                dto.getDerivationType(),
                dto.getDerivationValue(),
                dto.getRoundingRule(),
                dto.getFlDefault(),
                user,
                dto.getId()
            );
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void delete(Long id, Integer user) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("LISTINI_D01"), user, id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}

