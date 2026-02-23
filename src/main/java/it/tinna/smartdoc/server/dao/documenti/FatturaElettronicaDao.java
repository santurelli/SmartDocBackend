package it.tinna.smartdoc.server.dao.documenti;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import it.tinna.smartdoc.batch.dto.NotificaFatturaDto;
import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;
import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass.EsitoFTPType;
import it.tinna.smartdoc.shared.dto.documenti.EsitoSdiDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.StatoFatturaElettronica;

public class FatturaElettronicaDao extends BaseDao
{

    public FatturaElettronicaDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    /**
     * Metodo chiamato quando abbiamo una ricevuta di consegna
     * 
     * @param identificativoSdi
     * @param messageId
     * @param dataRicezioneEsito
     * @param descrizioneEsitoSdi
     * @param percorsoFileEsito
     * @param progressivoFile
     * @throws SQLException
     */
    public void aggiornaDatiNotificaMancataConsegna(String identificativoSdi,
                                                    String messageId,
                                                    XMLGregorianCalendar dataRicezioneEsito,
                                                    String descrizioneEsitoSdi,
                                                    String percorsoFileEsito,
                                                    String progressivoFile) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURAELETTRONICA_U03"), identificativoSdi, messageId, dataRicezioneEsito.toGregorianCalendar().getTime(), "MC", descrizioneEsitoSdi, percorsoFileEsito, progressivoFile);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella memorizzazione della notifica di mancata consegna per la fattura con progressivo {}", progressivoFile, e);
            throw new SQLException(e);
        }
    }

    /**
     * Metodo chiamato quando abbiamo una notifica di scarto
     * 
     * @param identificativoSdi
     * @param messageId
     * @param dataRicezioneEsito
     * @param descrizioneEsitoSdi
     * @param percorsoFileEsito
     * @param progressivoFile
     * @throws SQLException
     */
    public void aggiornaDatiNotificaScarto(String identificativoSdi,
                                           String messageId,
                                           XMLGregorianCalendar dataRicezioneEsito,
                                           String errori,
                                           String percorsoFileEsito,
                                           String progressivoFile) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURAELETTRONICA_U04"), identificativoSdi, messageId, dataRicezioneEsito.toGregorianCalendar().getTime(), "NS", errori, percorsoFileEsito, progressivoFile);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella memorizzazione della notifica di scarto per la fattura con progressivo {}", progressivoFile, e);
            throw new SQLException(e);
        }
    }

    /**
     * Metodo chiamato quando abbiamo una ricevuta di consegna
     * 
     * @param identificativoSdi
     * @param messageId
     * @param dataRicezioneEsito
     * @param descrizioneEsitoSdi
     * @param percorsoFileEsito
     * @param progressivoFile
     * @throws SQLException
     */
    public void aggiornaDatiRicevutaConsegna(String identificativoSdi,
                                             String messageId,
                                             XMLGregorianCalendar dataRicezioneEsito,
                                             String descrizioneEsitoSdi,
                                             String percorsoFileEsito,
                                             String progressivoFile) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURAELETTRONICA_U03"), identificativoSdi, messageId, dataRicezioneEsito.toGregorianCalendar().getTime(), "RC", descrizioneEsitoSdi, percorsoFileEsito, progressivoFile);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella memorizzazione della ricevuta di consegna per la fattura con progressivo {}", progressivoFile, e);
            throw new SQLException(e);
        }
    }

    /*
     * Il batch non usa questo metodo ma il metodo memorizzaEsitoSdi che contiene la chiamata ad una stored procedure con db_link per la connessione al database finale Questo metodo è usato dal front end per una fattura scartata come "Da inviare"
     */
    public void aggiornaStatoFattura(long idFattura,
                                     StatoFatturaElettronica statoFattura) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURAELETTRONICA_U01"), statoFattura.name(), idFattura);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento della fattura {} con lo stato {}", idFattura, statoFattura.name(), e);
            throw new SQLException(e);
        }
    }

    public void aggiornaDatiInvioSupporto(EsitoFTPType esitoInvio) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURAELETTRONICA_U02"), esitoInvio.getDataOraRicezione(), esitoInvio.getDataOraEsito(), esitoInvio.getEsito().name(), esitoInvio.getNomeSupporto());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dei dati di invio del supporto {}", esitoInvio.getNomeSupporto(), e);
            throw new SQLException(e);
        }
    }

    public void impostaFattureNotificate(List<Object[]> params) throws SQLException
    {
        try
        {
            jdbcTemplate.batchUpdate(FileQueryReader.getQuery("FATTURAELETTRONICA_U06"), params);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'impostazione come notificate delle fatture", e);
            throw new SQLException(e);
        }
    }

    public void impostaFattureNonNotificate(List<Object[]> params) throws SQLException
    {
        try
        {
            jdbcTemplate.batchUpdate(FileQueryReader.getQuery("FATTURAELETTRONICA_U07"), params);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'impostazione come non notificate delle fatture", e);
            throw new SQLException(e);
        }
    }

    public boolean isFatturaInviabile(String dbKey,
                                      long idFattura) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURAELETTRONICA_S05"), Long.class, dbKey, idFattura);
            return l == 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella verifica della inviabilità della fattura {} con dbKey {}", idFattura, dbKey, e);
            throw new SQLException(e);
        }
    }

    public EsitoSdiDto getEsitoInvioSdi(long idFattura,
                                        String nomeStore) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<EsitoSdiDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(EsitoSdiDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURAELETTRONICA_S06"), rowMapper, idFattura, nomeStore);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new EsitoSdiDto();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'esito sdi per la fattura {} e lo store {}", idFattura, nomeStore, e);
            throw new SQLException(e);
        }
    }

    public long getIdByProgressivoFile(String progressivoFile) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURAELETTRONICA_S04"), Long.class, progressivoFile);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'id fattura con progressivo invio {}", progressivoFile);
            throw new SQLException(e);
        }
    }

    public List<NotificaFatturaDto> getNotificheJustDesign() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<NotificaFatturaDto> rowMapper = new BeanPropertyRowMapper<NotificaFatturaDto>();
            rowMapper.setMappedClass(NotificaFatturaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURAELETTRONICA_S08"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<NotificaFatturaDto>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero degli esiti da notificare per JustDesign", e);
            throw new SQLException(e);
        }
    }

    public List<NotificaFatturaDto> getNotificheFastOrder() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<NotificaFatturaDto> rowMapper = new BeanPropertyRowMapper<NotificaFatturaDto>();
            rowMapper.setMappedClass(NotificaFatturaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURAELETTRONICA_S07"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<NotificaFatturaDto>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero degli esiti da notificare per FastOrder", e);
            throw new SQLException(e);
        }
    }

    /**
     * Restituisce l'ultimo progressivo invio utilizzato
     * 
     * @return
     * @throws SQLException
     */
    public synchronized String getProgressivoInvio() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURAELETTRONICA_S02"), String.class);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'ultimo progressivo invio per la fattura elettronica", e);
            throw new SQLException(e);
        }
    }

    public long getSupportiInviati() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURAELETTRONICA_S03"), Long.class);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del numero di supporti inviati allo SDI", e);
            throw new SQLException(e);
        }
    }

    public void impostaInviataSdi(String dbKey,
                                  long idFattura) throws SQLException
    {
        try
        {
            SimpleJdbcCall sjc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("impostaFatturaInviataSdi");
            sjc.execute(dbKey, idFattura);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'impostazione della fattura {} per il db come inviata allo SdI", idFattura, dbKey, e);
            throw new SQLException(e);
        }
    }

    public void impostaNotaCreditoInviataSdi(String dbKey,
                                             long idFattura) throws SQLException
    {
        try
        {
            SimpleJdbcCall sjc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("impostaNotaCreditoInviataSdi");
            sjc.execute(dbKey, idFattura);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'impostazione della nota di credito {} per il db come inviata allo SdI", idFattura, dbKey, e);
            throw new SQLException(e);
        }
    }

    public void memorizzaEsitoSdi(String progressivoFile) throws SQLException
    {
        try
        {
            _log.info("Chiamo memorizzaEsitoSdi per progressivo file {}", progressivoFile);
            SimpleJdbcCall sjc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("memorizzaEsitoSdi");
            sjc.execute(progressivoFile);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella memorizzazione dell'esito sdi per la fattura relativa al progressivo file {}", progressivoFile, e);
            throw new SQLException(e);
        }
    }

    public long memorizzaFatturaElettronica(String dbKey,
                                            FatturaElettronicaWrapperDto dto) throws SQLException
    {
        try
        {
            StringBuilder numeroDocumento = new StringBuilder(StringUtils.EMPTY);
            numeroDocumento.append(dto.getFattura().getNumDocumento());
            if ( StringUtils.isNotEmpty(dto.getFattura().getParticella()) )
            {
                if ( dto.getFattura().getParticella().startsWith(" ") || dto.getFattura().getParticella().startsWith("/") || dto.getFattura().getParticella().startsWith("\\") )
                {
                    numeroDocumento.append(dto.getFattura().getParticella());
                }
                else
                {
                    numeroDocumento.append("\\").append(dto.getFattura().getParticella());
                }
            }
            String tipoDocumento = dto.getFattura() instanceof FatturaDto ? TipoDocumentoEnum.FATTURA.name() : TipoDocumentoEnum.NOTA_CREDITO.name();
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURAELETTRONICA_I03"), Long.class, dbKey, dto.getFattura().getId(), tipoDocumento, StringUtils.defaultIfBlank(dto.getFattura().getXmlFattura(), null), StringUtils.defaultIfBlank(dto.getFattura().getErroreValidazioneXml(), null), StringUtils.defaultIfBlank(dto.getFattura().getXmlNonValido(), null), numeroDocumento.toString(), dto.getFattura().getDataDocumento(), dto.getFattura().getClienteDto().getDenominazione());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio della fattura elettronica nel service_db per idFattura {} e dbKey {}", dto.getFattura().getId(), dbKey, e);
            throw new SQLException(e);
        }
    }

    public void memorizzaInvioSdi(long idFatturaElettronica,
                                  String progressivoFile,
                                  long idSupporto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURAELETTRONICA_U05"), progressivoFile, idSupporto, idFatturaElettronica);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella memorizzazione dell'invio della fattura {}", e);
            throw new SQLException(e);
        }
    }

    public long memorizzaSupporto(File fileSupporto,
                                  int numeroFatture) throws SQLException
    {
        try
        {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator()
            {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException
                {
                    PreparedStatement ps = con.prepareStatement(FileQueryReader.getQuery("FATTURAELETTRONICA_I02"), Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, FilenameUtils.getName(fileSupporto.getAbsolutePath()));
                    ps.setInt(2, numeroFatture);
                    ps.setString(3, fileSupporto.getAbsolutePath());
                    return ps;
                }
            }, keyHolder);
            // jdbcTemplate.update(FileQueryReader.getQuery("FATTURAELETTRONICA_I02"), FilenameUtils.getName(fileSupporto.getAbsolutePath()), numeroFatture, fileSupporto.getAbsolutePath(), keyHolder);
            return new Long(keyHolder.getKeyList().get(0).get("k_d_e_supporti_inviati").toString()).longValue();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella memorizzazione del supporto inviato {}", fileSupporto.getAbsolutePath(), e);
            throw new SQLException(e);
        }
    }

}

