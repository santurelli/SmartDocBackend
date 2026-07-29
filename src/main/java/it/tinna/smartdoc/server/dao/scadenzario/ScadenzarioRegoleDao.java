package it.tinna.smartdoc.server.dao.scadenzario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.scadenzario.ScadenzaPromemoriaDto;
import it.tinna.smartdoc.shared.dto.scadenzario.ScadenzarioInvioDto;
import it.tinna.smartdoc.shared.dto.scadenzario.ScadenzarioRegolaDto;

public class ScadenzarioRegoleDao extends BaseDao
{

    public ScadenzarioRegoleDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public List<ScadenzarioRegolaDto> getList(String tipo) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzarioRegolaDto> rowMapper = new BeanPropertyRowMapper<>(ScadenzarioRegolaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("SCADENZARIOREGOLE_S01"), rowMapper, tipo);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle regole di scadenzario", e);
            throw new SQLException(e);
        }
    }

    public ScadenzarioRegolaDto getById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzarioRegolaDto> rowMapper = new BeanPropertyRowMapper<>(ScadenzarioRegolaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("SCADENZARIOREGOLE_S02"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della regola di scadenzario {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzarioRegolaDto> getAttiveByTipo(String tipo) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzarioRegolaDto> rowMapper = new BeanPropertyRowMapper<>(ScadenzarioRegolaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("SCADENZARIOREGOLE_S03"), rowMapper, tipo);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle regole attive di scadenzario per tipo {}", tipo, e);
            throw new SQLException(e);
        }
    }

    public long insert(ScadenzarioRegolaDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("SCADENZARIOREGOLE_I01"), Long.class,
                dto.getTipo(), dto.getGiorniOffset(), dto.getOggetto(), dto.getCorpo(),
                dto.getAttivo(), dto.getOrdine(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento della regola di scadenzario", e);
            throw new SQLException(e);
        }
    }

    public void update(ScadenzarioRegolaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("SCADENZARIOREGOLE_U01"),
                dto.getTipo(), dto.getGiorniOffset(), dto.getOggetto(), dto.getCorpo(),
                dto.getAttivo(), dto.getOrdine(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento della regola di scadenzario {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void delete(long id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("SCADENZARIOREGOLE_D01"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione della regola di scadenzario {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzaPromemoriaDto> getScadenzeIncasso(String dataTarget) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPromemoriaDto> rowMapper = new BeanPropertyRowMapper<>(ScadenzaPromemoriaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("SCADENZARIOPROMEMORIA_INCASSO_S01"), rowMapper, dataTarget);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle scadenze incasso per la data {}", dataTarget, e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzaPromemoriaDto> getScadenzePagamento(String dataTarget) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPromemoriaDto> rowMapper = new BeanPropertyRowMapper<>(ScadenzaPromemoriaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("SCADENZARIOPROMEMORIA_PAGAMENTO_S01"), rowMapper, dataTarget);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle scadenze pagamento per la data {}", dataTarget, e);
            throw new SQLException(e);
        }
    }

    public boolean isGiaInviato(long idRegola,
                                String tipo,
                                long idScadenza) throws SQLException
    {
        try
        {
            Integer count = jdbcTemplate.queryForObject(FileQueryReader.getQuery("SCADENZARIOINVIO_EXISTS_S01"), Integer.class, idRegola, tipo, idScadenza);
            return count != null && count > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella verifica invio gia' effettuato per la regola {} / scadenza {}", idRegola, idScadenza, e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzarioInvioDto> getInviiByCliente(long idCliente) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzarioInvioDto> rowMapper = new BeanPropertyRowMapper<>(ScadenzarioInvioDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("SCADENZARIOINVIO_S01"), rowMapper, idCliente);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle comunicazioni inviate per il cliente {}", idCliente, e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzarioInvioDto> getInviiByFattura(long idFattura) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzarioInvioDto> rowMapper = new BeanPropertyRowMapper<>(ScadenzarioInvioDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("SCADENZARIOINVIO_S02"), rowMapper, idFattura);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle comunicazioni inviate per la fattura {}", idFattura, e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzarioInvioDto> getInviiByFatturaFornitore(long idFatturaFornitore) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzarioInvioDto> rowMapper = new BeanPropertyRowMapper<>(ScadenzarioInvioDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("SCADENZARIOINVIO_S03"), rowMapper, idFatturaFornitore);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle comunicazioni inviate per la fattura fornitore {}", idFatturaFornitore, e);
            throw new SQLException(e);
        }
    }

    public void logInvio(long idRegola,
                         String tipo,
                         long idScadenza,
                         String destinatario,
                         String esito,
                         String dettaglioErrore,
                         String oggetto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("SCADENZARIOINVIO_I01"), idRegola, tipo, idScadenza, destinatario, esito, dettaglioErrore, oggetto);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella registrazione dell'invio per la regola {} / scadenza {}", idRegola, idScadenza, e);
            throw new SQLException(e);
        }
    }

}
