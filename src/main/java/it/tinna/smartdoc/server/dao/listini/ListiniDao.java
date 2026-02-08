package it.tinna.smartdoc.server.dao.listini;

import java.sql.SQLException;
import java.util.List;
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
}
