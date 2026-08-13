package it.tinna.smartdoc.server.dao.pianoconti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.pianoconti.PianoContoDto;

@Repository
public class PianoDeiContiDao extends BaseDao {

    public PianoDeiContiDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void delete(long idUser, long id) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("PIANODEICONTI_D01"), idUser, id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public PianoContoDto getById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<PianoContoDto> rowMapper = new BeanPropertyRowMapper<>(PianoContoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("PIANODEICONTI_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<PianoContoDto> getList(String strToSearch) throws SQLException {
        try {
            String like = StringUtility.formatForLikeHelper(strToSearch);
            BeanPropertyRowMapper<PianoContoDto> rowMapper = new BeanPropertyRowMapper<>(PianoContoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("PIANODEICONTI_S01"), rowMapper, like, like);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public long insert(PianoContoDto dto) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("PIANODEICONTI_I01"), Long.class,
                    dto.getCodice(), dto.getDescrizione(), dto.getIdPadre(), dto.getTipo(), dto.getRuoloDefault(),
                    dto.getPredefinito(), dto.getBloccato(), dto.getNote(), dto.getUserCreated());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public boolean isExistentCodice(String codice, long id) throws SQLException {
        try {
            Long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("PIANODEICONTI_S03"), Long.class, codice, id);
            return l != null && l > 0;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public long count() throws SQLException {
        try {
            Long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("PIANODEICONTI_S04"), Long.class);
            return l == null ? 0 : l;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void update(PianoContoDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("PIANODEICONTI_U01"), dto.getCodice(), dto.getDescrizione(),
                    dto.getIdPadre(), dto.getTipo(), dto.getRuoloDefault(), dto.getNote(), dto.getUserLastUpdate(), dto.getId());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}
