package it.tinna.smartdoc.server.dao.ubicazioni;

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
import it.tinna.smartdoc.shared.dto.ubicazioni.UbicazioneDto;
import it.tinna.smartdoc.shared.dto.ubicazioni.UbicazioneProdottoDto;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UbicazioniDao extends BaseDao {

    public UbicazioniDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<UbicazioneDto> getList(String search, int length, int start) throws SQLException {
        try {
            BeanPropertyRowMapper<UbicazioneDto> rowMapper = new BeanPropertyRowMapper<>(UbicazioneDto.class);
            String query = FileQueryReader.getQuery("UBICAZIONI_S01");
            query = query.replace("${LIMIT}", "OFFSET " + start + " LIMIT " + length);

            String searchLike = StringUtility.formatForLikeHelper(search);
            return jdbcTemplate.query(query, rowMapper, searchLike, searchLike, searchLike);
        } catch (DataAccessException e) {
            log.error("Errore nel recupero della lista ubicazioni", e);
            throw new SQLException(e);
        }
    }

    public UbicazioneDto getById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<UbicazioneDto> rowMapper = new BeanPropertyRowMapper<>(UbicazioneDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("UBICAZIONI_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            log.error("Errore nel recupero ubicazione con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public UbicazioneDto getByCodice(String codUbicazione) throws SQLException {
        try {
            BeanPropertyRowMapper<UbicazioneDto> rowMapper = new BeanPropertyRowMapper<>(UbicazioneDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("UBICAZIONI_S03"), rowMapper, codUbicazione);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            log.error("Errore nel recupero ubicazione con codice {}", codUbicazione, e);
            throw new SQLException(e);
        }
    }

    public long insert(UbicazioneDto dto) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(
                FileQueryReader.getQuery("UBICAZIONI_I01"),
                Long.class,
                dto.getCodUbicazione(),
                dto.getDescrizione(),
                dto.getCorsia(),
                dto.getRipiano(),
                dto.getLivello(),
                dto.getNote(),
                dto.getUserCreated()
            );
        } catch (DataAccessException e) {
            log.error("Errore nell'inserimento dell'ubicazione", e);
            throw new SQLException(e);
        }
    }

    public void update(UbicazioneDto dto) throws SQLException {
        try {
            jdbcTemplate.update(
                FileQueryReader.getQuery("UBICAZIONI_U01"),
                dto.getCodUbicazione(),
                dto.getDescrizione(),
                dto.getCorsia(),
                dto.getRipiano(),
                dto.getLivello(),
                dto.getNote(),
                dto.getUserLastUpdate(),
                dto.getId()
            );
        } catch (DataAccessException e) {
            log.error("Errore nell'aggiornamento dell'ubicazione id {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void delete(long id, Long user) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("UBICAZIONI_D01"), user, id);
        } catch (DataAccessException e) {
            log.error("Errore nella cancellazione dell'ubicazione id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<UbicazioneProdottoDto> getUbicazioniByProdotto(long idProdotto) throws SQLException {
        try {
            BeanPropertyRowMapper<UbicazioneProdottoDto> rowMapper = new BeanPropertyRowMapper<>(UbicazioneProdottoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("UBICAZIONI_PRODOTTI_S01"), rowMapper, idProdotto);
        } catch (DataAccessException e) {
            log.error("Errore nel recupero ubicazioni per prodotto {}", idProdotto, e);
            throw new SQLException(e);
        }
    }

    public List<UbicazioneProdottoDto> getProdottiByUbicazione(long idUbicazione) throws SQLException {
        try {
            BeanPropertyRowMapper<UbicazioneProdottoDto> rowMapper = new BeanPropertyRowMapper<>(UbicazioneProdottoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("UBICAZIONI_PRODOTTI_S02"), rowMapper, idUbicazione);
        } catch (DataAccessException e) {
            log.error("Errore nel recupero prodotti per ubicazione {}", idUbicazione, e);
            throw new SQLException(e);
        }
    }

    public long insertProdottoUbicazione(UbicazioneProdottoDto dto) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(
                FileQueryReader.getQuery("UBICAZIONI_PRODOTTI_I01"),
                Long.class,
                dto.getIdProdotto(),
                dto.getIdUbicazione(),
                dto.getDot(),
                dto.getQuantita() != null ? dto.getQuantita() : 0.0,
                dto.getNote(),
                dto.getUserCreated()
            );
        } catch (DataAccessException e) {
            log.error("Errore nell'assegnazione ubicazione al prodotto", e);
            throw new SQLException(e);
        }
    }

    public void updateProdottoUbicazione(UbicazioneProdottoDto dto) throws SQLException {
        try {
            jdbcTemplate.update(
                FileQueryReader.getQuery("UBICAZIONI_PRODOTTI_U01"),
                dto.getIdUbicazione(),
                dto.getDot(),
                dto.getQuantita() != null ? dto.getQuantita() : 0.0,
                dto.getNote(),
                dto.getUserLastUpdate(),
                dto.getId()
            );
        } catch (DataAccessException e) {
            log.error("Errore nell'aggiornamento ubicazione prodotto id {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void deleteProdottoUbicazione(long id, Long user) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("UBICAZIONI_PRODOTTI_D01"), user, id);
        } catch (DataAccessException e) {
            log.error("Errore nella rimozione ubicazione prodotto id {}", id, e);
            throw new SQLException(e);
        }
    }
}
