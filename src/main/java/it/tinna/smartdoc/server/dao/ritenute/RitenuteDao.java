package it.tinna.smartdoc.server.dao.ritenute;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.ritenute.RitenutaFornitoreDto;

public class RitenuteDao extends BaseDao {

    public RitenuteDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<RitenutaFornitoreDto> get770(int anno, Integer idFornitore) throws SQLException {
        try {
            BeanPropertyRowMapper<RitenutaFornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(RitenutaFornitoreDto.class);
            return jdbcTemplate.query(
                FileQueryReader.getQuery("RITENUTE_770_S01"),
                rowMapper,
                anno, idFornitore, idFornitore
            );
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero del prospetto 770 per l'anno {}", anno, e);
            throw new SQLException(e);
        }
    }
}
