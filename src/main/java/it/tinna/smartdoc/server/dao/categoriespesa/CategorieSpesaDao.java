package it.tinna.smartdoc.server.dao.categoriespesa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.categoriespesa.CategoriaSpesaDto;

@Repository
public class CategorieSpesaDao extends BaseDao {

    private static final Log _log = LogFactory.getLog(CategorieSpesaDao.class);

    public CategorieSpesaDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void delete(Integer idUser, Integer idCategoria) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CATEGORIESPESA_D01"), idUser, idCategoria);
        } catch (DataAccessException e) {
            _log.error("Errore nella cancellazione della categoria di spesa con id " + idCategoria, e);
            throw new SQLException(e);
        }
    }

    public CategoriaSpesaDto getById(Integer id) throws SQLException {
        try {
            BeanPropertyRowMapper<CategoriaSpesaDto> rowMapper = new BeanPropertyRowMapper<>(CategoriaSpesaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CATEGORIESPESA_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            _log.error("Nessuna categoria di spesa trovata con id " + id);
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero della categoria di spesa con id " + id, e);
            throw new SQLException(e);
        }
    }

    public List<CategoriaSpesaDto> getList(String descrizione) throws SQLException {
        try {
            BeanPropertyRowMapper<CategoriaSpesaDto> rowMapper = new BeanPropertyRowMapper<>(CategoriaSpesaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("CATEGORIESPESA_S01"), rowMapper, descrizione);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'elenco categorie di spesa", e);
            throw new SQLException(e);
        }
    }

    public void insert(CategoriaSpesaDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CATEGORIESPESA_I01"), dto.getDescrizione(),
                    dto.getPredefinita(), dto.getUserCreated());
        } catch (DataAccessException e) {
            _log.error("Errore nell'inserimento della categoria di spesa", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        try {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("CATEGORIESPESA_S03"), Long.class,
                    descrizione, id);
            return l > 0;
        } catch (DataAccessException e) {
            _log.error("Errore nella determinazione dell'esistenza della categoria di spesa con descrizione "
                    + descrizione, e);
            throw new SQLException(e);
        }
    }

    public void resetPredefinite(long idUser) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CATEGORIESPESA_U02"), idUser);
        } catch (DataAccessException e) {
            _log.error("Errore nel reset della categoria di spesa predefinita", e);
            throw new SQLException(e);
        }
    }

    public void update(CategoriaSpesaDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CATEGORIESPESA_U01"), dto.getDescrizione(),
                    dto.getPredefinita(), dto.getUserLastUpdate(), dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'aggiornamento della categoria di spesa con id " + dto.getId(), e);
            throw new SQLException(e);
        }
    }

}
