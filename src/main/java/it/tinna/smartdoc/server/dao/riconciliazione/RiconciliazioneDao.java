package it.tinna.smartdoc.server.dao.riconciliazione;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.riconciliazione.MatchCandidatoDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.RiconciliazioneCsvMappingDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.RiconciliazioneImportDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.RiconciliazioneMovimentoDto;

public class RiconciliazioneDao extends BaseDao
{

    public RiconciliazioneDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public long insertImport(RiconciliazioneImportDto dto,
                             long userId) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("RICONCILIAZIONE_IMPORT_I01"), Long.class,
                dto.getNomeFile(), dto.getFormato(), dto.getIdRisorsa(), dto.getStato(), dto.getDettaglioErrore(),
                dto.getNumMovimenti(), dto.getNumAbbinatiAuto(), userId);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento del batch di import riconciliazione", e);
            throw new SQLException(e);
        }
    }

    public List<RiconciliazioneImportDto> getListImport() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<RiconciliazioneImportDto> rowMapper = new BeanPropertyRowMapper<>(RiconciliazioneImportDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("RICONCILIAZIONE_IMPORT_S01"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei batch di import riconciliazione", e);
            throw new SQLException(e);
        }
    }

    public void insertMovimento(long idImport,
                                it.tinna.smartdoc.shared.dto.riconciliazione.MovimentoEstrattoContoDto movimento,
                                String stato,
                                String tipoScadenza,
                                Integer idScadenza,
                                java.math.BigDecimal score) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("RICONCILIAZIONE_MOVIMENTO_I01"),
                idImport,
                java.sql.Date.valueOf(movimento.getDataValuta()),
                movimento.getDataContabile() != null ? java.sql.Date.valueOf(movimento.getDataContabile()) : null,
                movimento.getImporto(),
                movimento.getCausaleBanca(),
                movimento.getControparte(),
                movimento.getIbanControparte(),
                stato,
                tipoScadenza,
                idScadenza,
                score,
                (tipoScadenza != null) ? new java.sql.Timestamp(System.currentTimeMillis()) : null);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento del movimento riconciliazione per import {}", idImport, e);
            throw new SQLException(e);
        }
    }

    public List<RiconciliazioneMovimentoDto> getMovimentiByImport(long idImport) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<RiconciliazioneMovimentoDto> rowMapper = new BeanPropertyRowMapper<>(RiconciliazioneMovimentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("RICONCILIAZIONE_MOVIMENTO_S01"), rowMapper, idImport);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei movimenti per l'import {}", idImport, e);
            throw new SQLException(e);
        }
    }

    public void updateMovimentoAbbinamento(long idMovimento,
                                           String stato,
                                           String tipoScadenza,
                                           Integer idScadenza,
                                           java.math.BigDecimal score,
                                           Long userId) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("RICONCILIAZIONE_MOVIMENTO_U01"),
                stato, tipoScadenza, idScadenza, score, userId, idMovimento);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dell'abbinamento del movimento {}", idMovimento, e);
            throw new SQLException(e);
        }
    }

    public List<MatchCandidatoDto> getScadenzeAperteIncasso(String dataCentro) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<MatchCandidatoDto> rowMapper = new BeanPropertyRowMapper<>(MatchCandidatoDto.class);
            List<MatchCandidatoDto> list = jdbcTemplate.query(
                FileQueryReader.getQuery("RICONCILIAZIONE_SCADENZE_APERTE_INCASSO_S01"), rowMapper, dataCentro, dataCentro);
            list.forEach(c -> c.setTipo("INCASSO"));
            return list;
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle scadenze aperte incasso", e);
            throw new SQLException(e);
        }
    }

    public List<MatchCandidatoDto> getScadenzeApertePagamento(String dataCentro) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<MatchCandidatoDto> rowMapper = new BeanPropertyRowMapper<>(MatchCandidatoDto.class);
            List<MatchCandidatoDto> list = jdbcTemplate.query(
                FileQueryReader.getQuery("RICONCILIAZIONE_SCADENZE_APERTE_PAGAMENTO_S01"), rowMapper, dataCentro, dataCentro);
            list.forEach(c -> c.setTipo("PAGAMENTO"));
            return list;
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle scadenze aperte pagamento", e);
            throw new SQLException(e);
        }
    }

    public void saldaScadenzaIncasso(long idScadenza,
                                     String dtPagamento) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("RICONCILIAZIONE_SALDA_INCASSO_U01"), dtPagamento, idScadenza);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella chiusura della scadenza incasso {}", idScadenza, e);
            throw new SQLException(e);
        }
    }

    public void saldaScadenzaPagamento(long idScadenza,
                                       String dtPagamento) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("RICONCILIAZIONE_SALDA_PAGAMENTO_U01"), dtPagamento, idScadenza);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella chiusura della scadenza pagamento {}", idScadenza, e);
            throw new SQLException(e);
        }
    }

    public RiconciliazioneCsvMappingDto getCsvMapping(long idRisorsa) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<RiconciliazioneCsvMappingDto> rowMapper = new BeanPropertyRowMapper<>(RiconciliazioneCsvMappingDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("RICONCILIAZIONE_CSVMAPPING_S01"), rowMapper, idRisorsa);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del mapping CSV per la risorsa {}", idRisorsa, e);
            throw new SQLException(e);
        }
    }

    public void saveCsvMapping(RiconciliazioneCsvMappingDto dto,
                               long userId) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("RICONCILIAZIONE_CSVMAPPING_I01"),
                dto.getIdRisorsa(), dto.getDelimitatore(), dto.getFlHaIntestazione(), dto.getFormatoData(),
                dto.getColData(), dto.getColImporto(), dto.getColCausale(), dto.getColControparte(),
                dto.getColIbanControparte(), dto.getFlImportoUnicoConSegno(), dto.getColImportoEntrata(),
                dto.getColImportoUscita(), userId);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio del mapping CSV per la risorsa {}", dto.getIdRisorsa(), e);
            throw new SQLException(e);
        }
    }

}
