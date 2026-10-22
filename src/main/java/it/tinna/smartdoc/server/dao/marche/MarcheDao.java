package it.tinna.smartdoc.server.dao.marche;

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
import it.tinna.smartdoc.shared.dto.marche.MarcaDto;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MarcheDao extends BaseDao {

    public MarcheDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<MarcaDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        try {
            String query = FileQueryReader.getQuery("MARCHE_S01");

            String orderBy = "descrizione ASC";
            if (orderCol != null) {
                switch (orderCol) {
                    case 0: orderBy = "codice " + (orderDir != null ? orderDir : "ASC"); break;
                    case 1: orderBy = "descrizione " + (orderDir != null ? orderDir : "ASC"); break;
                    default: orderBy = "descrizione " + (orderDir != null ? orderDir : "ASC"); break;
                }
            }
            query = query.replace("${ORDER_BY}", orderBy);

            String limit = "";
            if (length != null && length != -1) {
                limit = "LIMIT " + length + " OFFSET " + (start != null ? start : 0);
            }
            query = query.replace("${LIMIT}", limit);

            BeanPropertyRowMapper<MarcaDto> rowMapper = new BeanPropertyRowMapper<>(MarcaDto.class);
            List<Object> args = new ArrayList<>();
            String searchPattern = StringUtility.formatForLikeHelper(search);
            args.add(searchPattern);
            args.add(searchPattern);

            return jdbcTemplate.query(query, rowMapper, args.toArray());
        } catch (DataAccessException e) {
            log.error("Errore durante il recupero della lista marche", e);
            throw new SQLException(e);
        }
    }

    public List<MarcaDto> getListForCombo() throws SQLException {
        try {
            String query = FileQueryReader.getQuery("MARCHE_COMBO");
            BeanPropertyRowMapper<MarcaDto> rowMapper = new BeanPropertyRowMapper<>(MarcaDto.class);
            return jdbcTemplate.query(query, rowMapper);
        } catch (DataAccessException e) {
            log.error("Errore durante il recupero marche per combo", e);
            throw new SQLException(e);
        }
    }

    public MarcaDto getById(long id) throws SQLException {
        try {
            String query = FileQueryReader.getQuery("MARCHE_S02");
            BeanPropertyRowMapper<MarcaDto> rowMapper = new BeanPropertyRowMapper<>(MarcaDto.class);
            return jdbcTemplate.queryForObject(query, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            log.error("Errore durante il recupero della marca id {}", id, e);
            throw new SQLException(e);
        }
    }

    public long insert(MarcaDto dto) throws SQLException {
        try {
            String query = FileQueryReader.getQuery("MARCHE_I01");
            return jdbcTemplate.queryForObject(query, Long.class,
                dto.getCodice(),
                dto.getDescrizione(),
                dto.getNote(),
                dto.getUserCreated()
            );
        } catch (DataAccessException e) {
            log.error("Errore durante l'inserimento della marca {}", dto.getDescrizione(), e);
            throw new SQLException(e);
        }
    }

    public void update(MarcaDto dto) throws SQLException {
        try {
            String query = FileQueryReader.getQuery("MARCHE_U01");
            jdbcTemplate.update(query,
                dto.getCodice(),
                dto.getDescrizione(),
                dto.getNote(),
                dto.getUserLastUpdate(),
                dto.getId()
            );
        } catch (DataAccessException e) {
            log.error("Errore durante l'aggiornamento della marca id {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void delete(long id, long userId) throws SQLException {
        try {
            String query = FileQueryReader.getQuery("MARCHE_D01");
            jdbcTemplate.update(query, userId, id);
        } catch (DataAccessException e) {
            log.error("Errore durante l'eliminazione della marca id {}", id, e);
            throw new SQLException(e);
        }
    }
}
