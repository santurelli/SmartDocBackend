package it.tinna.smartdoc.server.dao.documenti;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void aggiornaStatoFattura(long idFattura,
                                     StatoFatturaElettronica statoFattura) throws SQLException
    {
        String sql = FileQueryReader.getQuery("FATTURAELETTRONICA_U01");
        if (org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive())
        {
            try
            {
                int updated = jdbcTemplate.update(sql, statoFattura.name(), idFattura);
                _log.info("aggiornaStatoFattura (transazionale): idFattura={}, stato={}, righe modificate={}", idFattura, statoFattura.name(), updated);
            }
            catch ( org.springframework.dao.DataAccessException e )
            {
                _log.error("Errore nell'aggiornamento transazionale della fattura {} con lo stato {}", idFattura, statoFattura.name(), e);
                throw new SQLException(e);
            }
        }
        else
        {
            try (Connection conn = jdbcTemplate.getDataSource().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql))
            {
                pstmt.setString(1, statoFattura.name());
                pstmt.setLong(2, idFattura);
                int updated = pstmt.executeUpdate();
                _log.info("aggiornaStatoFattura (connessione diretta): idFattura={}, stato={}, righe modificate={}", idFattura, statoFattura.name(), updated);
            }
            catch ( SQLException e )
            {
                _log.error("Errore nell'aggiornamento diretto della fattura {} con lo stato {}", idFattura, statoFattura.name(), e);
                throw e;
            }
        }
    }

    public void aggiornaStatoNotaCredito(long idNotaCredito,
                                         StatoFatturaElettronica statoFattura) throws SQLException
    {
        String sql = FileQueryReader.getQuery("FATTURAELETTRONICA_U01B");
        if (org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive())
        {
            try
            {
                int updated = jdbcTemplate.update(sql, statoFattura.name(), idNotaCredito);
                _log.info("aggiornaStatoNotaCredito (transazionale): idNotaCredito={}, stato={}, righe modificate={}", idNotaCredito, statoFattura.name(), updated);
            }
            catch ( org.springframework.dao.DataAccessException e )
            {
                _log.error("Errore nell'aggiornamento transazionale della nota credito {} con lo stato {}", idNotaCredito, statoFattura.name(), e);
                throw new SQLException(e);
            }
        }
        else
        {
            try (Connection conn = jdbcTemplate.getDataSource().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql))
            {
                pstmt.setString(1, statoFattura.name());
                pstmt.setLong(2, idNotaCredito);
                int updated = pstmt.executeUpdate();
                _log.info("aggiornaStatoNotaCredito (connessione diretta): idNotaCredito={}, stato={}, righe modificate={}", idNotaCredito, statoFattura.name(), updated);
            }
            catch ( SQLException e )
            {
                _log.error("Errore nell'aggiornamento diretto della nota credito {} con lo stato {}", idNotaCredito, statoFattura.name(), e);
                throw e;
            }
        }
    }

    public void aggiornaStatoAutofattura(long idFatturaFornitore,
                                         StatoFatturaElettronica statoFattura) throws SQLException
    {
        String sql = FileQueryReader.getQuery("FATTURAELETTRONICA_U01C");
        if (org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive())
        {
            try
            {
                int updated = jdbcTemplate.update(sql, statoFattura.name(), idFatturaFornitore);
                _log.info("aggiornaStatoAutofattura (transazionale): idFatturaFornitore={}, stato={}, righe modificate={}", idFatturaFornitore, statoFattura.name(), updated);
            }
            catch ( org.springframework.dao.DataAccessException e )
            {
                _log.error("Errore nell'aggiornamento transazionale dell'autofattura {} con lo stato {}", idFatturaFornitore, statoFattura.name(), e);
                throw new SQLException(e);
            }
        }
        else
        {
            try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection();
                 java.sql.PreparedStatement pstmt = conn.prepareStatement(sql))
            {
                pstmt.setString(1, statoFattura.name());
                pstmt.setLong(2, idFatturaFornitore);
                int updated = pstmt.executeUpdate();
                _log.info("aggiornaStatoAutofattura (connessione diretta): idFatturaFornitore={}, stato={}, righe modificate={}", idFatturaFornitore, statoFattura.name(), updated);
            }
            catch ( java.sql.SQLException e )
            {
                _log.error("Errore nell'aggiornamento diretto dell'autofattura {} con lo stato {}", idFatturaFornitore, statoFattura.name(), e);
                throw e;
            }
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

    public void resetStatoInvioFattura(String dbKey, long idFattura) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURAELETTRONICA_U08"), dbKey, idFattura);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel reset dello stato invio per la fattura {} con dbKey {}", idFattura, dbKey, e);
            throw new SQLException(e);
        }
    }

    public List<EsitoSdiDto> getEsitiInvioSdi(List<Long> idFatture,
                                              String dbKey) throws SQLException
    {
        if ( idFatture == null || idFatture.isEmpty() )
        {
            return new ArrayList<>();
        }
        try
        {
            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < idFatture.size(); i++ )
            {
                if ( i > 0 )
                {
                    sb.append(",");
                }
                sb.append("?");
            }
            String query = FileQueryReader.getQuery("FATTURAELETTRONICA_S10").replace(":ids", sb.toString());
            List<Object> params = new ArrayList<>();
            params.add(dbKey);
            params.addAll(idFatture);

            BeanPropertyRowMapper<EsitoSdiDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(EsitoSdiDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero degli esiti sdi per le fatture {} e il dbKey {}", idFatture, dbKey, e);
            throw new SQLException(e);
        }
    }

    public EsitoSdiDto getEsitoInvioSdi(long idFattura,
                                        String dbKey) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<EsitoSdiDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(EsitoSdiDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURAELETTRONICA_S06"), rowMapper, idFattura, dbKey);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new EsitoSdiDto();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'esito sdi per la fattura {} e il dbKey {}", idFattura, dbKey, e);
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

    public List<NotificaFatturaDto> getNotificheGenericheSdi() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<NotificaFatturaDto> rowMapper = new BeanPropertyRowMapper<NotificaFatturaDto>();
            rowMapper.setMappedClass(NotificaFatturaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURAELETTRONICA_S09"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<NotificaFatturaDto>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero degli esiti da notificare generici", e);
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

    public Map<String, Object> getEsitoByProgressivoFile(String progressivoFile) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForMap(FileQueryReader.getQuery("FATTURAELETTRONICA_S04B"), progressivoFile);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.warn("Nessuna fattura elettronica trovata per progressivo file {}", progressivoFile);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero esito SDI per progressivo file {}", progressivoFile, e);
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

            // Check if record already exists in d_e_fatture_elettroniche
            String selectSql = "SELECT k_d_e_fatture_elettroniche FROM d_e_fatture_elettroniche WHERE db_key = ? AND k_d_e_fatture = ? AND tipo_documento = ? ORDER BY k_d_e_fatture_elettroniche DESC LIMIT 1";
            List<Long> ids = jdbcTemplate.queryForList(selectSql, Long.class, dbKey, dto.getFattura().getId(), tipoDocumento);
            if (ids != null && !ids.isEmpty()) {
                long existingId = ids.get(0);
                String updateSql = "UPDATE d_e_fatture_elettroniche SET file_inviato = ?, errore_validazione_xml = ?, xml_non_valido = ?, numero_documento = ?, data_documento = ?, soggetto = ? WHERE k_d_e_fatture_elettroniche = ?";
                jdbcTemplate.update(updateSql, 
                    StringUtils.defaultIfBlank(dto.getFattura().getXmlFattura(), null), 
                    StringUtils.defaultIfBlank(dto.getFattura().getErroreValidazioneXml(), null), 
                    StringUtils.defaultIfBlank(dto.getFattura().getXmlNonValido(), null), 
                    numeroDocumento.toString(), 
                    dto.getFattura().getDataDocumento(), 
                    dto.getFattura().getClienteDto().getDenominazione(),
                    existingId
                );
                _log.info("Aggiornato record esistente in d_e_fatture_elettroniche con id {}", existingId);
                return existingId;
            }

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

    public void cancellaFatturaElettronicaCentrale(String dbKey, long idFattura) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURAELETTRONICA_D01"), dbKey, idFattura);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione della fattura elettronica centrale per idFattura {} e dbKey {}", idFattura, dbKey, e);
            throw new SQLException(e);
        }
    }

}

