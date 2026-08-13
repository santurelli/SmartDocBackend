package it.tinna.smartdoc.server.dao.documenti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.documenti.FatturaFornitoreDto;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoFornitoreDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.StatoPagamentoFattura;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;

public class NoteCreditoFornitoreDao extends BaseDao
{

    public NoteCreditoFornitoreDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(NotaCreditoFornitoreDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITOFORNITORE_D04"), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione della nota di credito fornitore {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void deleteProdottiById(long id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITOFORNITORE_D01"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione dei prodotti della nota credito fornitore {}", id, e);
            throw new SQLException(e);
        }
    }

    public void deleteScadenzaPagamento(Integer id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITOFORNITORE_D05"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione della scadenza di pagamento (nota credito fornitore) con id {}", id, e);
            throw new SQLException(e);
        }
    }

    /**
     * Elimina le scadenza di pagamento di una nota credito fornitore
     * 
     * @param idNotaCreditoFornitore
     * @throws SQLException
     */
    public void deleteScadenzePagamento(long idNotaCreditoFornitore) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITOFORNITORE_D03"), idNotaCreditoFornitore);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione delle scadenza di pagamento relative alla nota credito fornitore {}", idNotaCreditoFornitore, e);
            throw new SQLException(e);
        }
    }

    /**
     * Elimina le spese incasso di una nota credito fornitore
     * 
     * @param idNotaCreditoFornitore
     * @throws SQLException
     */
    public void deleteSpeseIncassoById(long idNotaCreditoFornitore) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITOFORNITORE_D02"), idNotaCreditoFornitore);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione delle spese di incasso della nota credito fornitore {}", idNotaCreditoFornitore, e);
            throw new SQLException(e);
        }
    }

    public NotaCreditoFornitoreDto getById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<NotaCreditoFornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(NotaCreditoFornitoreDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITOFORNITORE_S03"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna nota credito fornitore trovata con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della nota credito fornitore con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public NotaCreditoFornitoreDto getByNumeroEFornitore(String numeroDocumentoFornitore,
                                                         long idFornitore) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<NotaCreditoFornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(NotaCreditoFornitoreDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITOFORNITORE_S13"), rowMapper, numeroDocumentoFornitore, idFornitore);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna nota credito fornitore trovata con numero documento {} e id fornitore {}", numeroDocumentoFornitore, idFornitore);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della nota credito fornitore con numero documento {} e fornitore", numeroDocumentoFornitore, idFornitore, e);
            throw new SQLException(e);
        }
    }

    public List<FatturaFornitoreDto> getFattureAssociabili(long idFornitore,
                                                           String dataNotaCredito) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<FatturaFornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(FatturaFornitoreDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTUREFORNITORE_S13"), rowMapper, idFornitore, dataNotaCredito);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle fatture associabili alla nota credito fornitore per il fornitore {} e la data {}", idFornitore, dataNotaCredito, e);
            throw new SQLException(e);
        }
    }

    public List<NotaCreditoFornitoreDto> getList(Integer idFornitore,
                                                 String dtDocumentoFrom,
                                                 String dtDocumentoTo,
                                                 String numeroDocumento,
                                                 String dtRegistrazioneFrom,
                                                 String dtRegistrazioneTo,
                                                 Integer numeroRegistrazione,
                                                 String stato,
                                                 Integer length,
                                                 Integer start,
                                                 Integer orderColumn,
                                                 String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("NOTECREDITOFORNITORE_S11");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(dtDocumentoFrom) ? null : dtDocumentoFrom);
        params.add(StringUtils.isEmpty(dtDocumentoTo) ? null : dtDocumentoTo);
        params.add(StringUtils.isEmpty(numeroDocumento) ? null : StringUtility.formatForLike(numeroDocumento));
        params.add(StringUtils.isEmpty(dtRegistrazioneFrom) ? null : dtRegistrazioneFrom);
        params.add(StringUtils.isEmpty(dtRegistrazioneTo) ? null : dtRegistrazioneTo);
        params.add(numeroRegistrazione == null ? null : numeroRegistrazione);
        params.add(idFornitore);
        Map<String, String> valuesMap = new HashMap<>();
        if ( StringUtils.isNotBlank(stato) )
        {
            if ( stato.equals(StatoPagamentoFattura.NON_PAGATA.getValue()) )
            {
                valuesMap.put("STATO", "AND totaleDaPagare > 0 AND totalePagato = 0");
            }
            else if ( stato.equals(StatoPagamentoFattura.PARZIALMENTE_PAGATA.getValue()) )
            {
                valuesMap.put("STATO", "AND totaleDaPagare > 0 AND totalePagato > 0");
            }
            else
            {
                valuesMap.put("STATO", "AND totaleDaPagare = 0");
            }
        }
        else
        {
            valuesMap.put("STATO", "");
        }
        if ( orderColumn == 0 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("data_notacredito ").append(orderDir).toString());
        }
        else if ( orderColumn == 1 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("numDocumento ").append(orderDir).toString());
        }
        else if ( orderColumn == 2 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("data_doc_fornitore ").append(orderDir).toString());
        }
        else if ( orderColumn == 3 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("numeroDocumentoFornitore ").append(orderDir).toString());
        }
        else if ( orderColumn == 4 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("descFornitore ").append(orderDir).toString());
        }
        else
        {
            valuesMap.put("ORDER_BY", new StringBuilder("data_notacredito ").append(orderDir).toString());
        }
        if ( length != null && start != null )
        {
            valuesMap.put("LIMIT", "LIMIT ? OFFSET ?");
            params.add(length);
            params.add(start);
        }
        else
        {
            valuesMap.put("LIMIT", "");
        }
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            BeanPropertyRowMapper<NotaCreditoFornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(NotaCreditoFornitoreDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella ricerca delle note credito fornitore", e);
            throw new SQLException(e);
        }
    }

    public List<ProdottoDocumentoDto> getListProdottiById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ProdottoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ProdottoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("NOTECREDITOFORNITORE_S04"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco articoli nella nota credito fornitore con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzaPagamentoDocumentoDto> getListScadenzePagamentoById(Integer idFatturaFornitore) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("NOTECREDITOFORNITORE_S07"), rowMapper, idFatturaFornitore);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle scadenze di pagamento per la nota credito fornitore con id {}", idFatturaFornitore, e);
            throw new SQLException(e);
        }
    }

    public List<SpesaIncassoDocumentoDto> getListSpeseIncassoById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<SpesaIncassoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(SpesaIncassoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("NOTECREDITOFORNITORE_S05"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle spese di incasso nella nota credito fornitore con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public Integer getNextNum(String data) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITOFORNITORE_S06"), Integer.class, StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null));
        }
        catch ( EmptyResultDataAccessException e )
        {
            return 1;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del prossimo numero di nota credito fornitore", e);
            throw new SQLException(e);
        }
    }

    public ScadenzaPagamentoDocumentoDto getScadenzaPagamento(Integer idScadenza) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDocumentoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITOFORNITORE_S08"), rowMapper, idScadenza);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna scadenza di pagamento trovata con id {}", idScadenza);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle scadenza di pagamento con id {}", idScadenza, e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzaPagamentoDocumentoDto> getScadenzePagamento(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("NOTECREDITOFORNITORE_S12"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco scadenze pagamento per la nota credito fornitore {}", id);
            throw new SQLException(e);
        }
    }

    // /**
    // * Calcola l'importo totale delle fatture fornitore ricevute nel mese corrente
    // *
    // * @return
    // * @throws Exception
    // */
    // public BigDecimal getTotaleMeseCorrente() throws SQLException
    // {
    // try
    // {
    // return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTUREFORNITORE_S10"), BigDecimal.class);
    // }
    // catch ( DataAccessException e )
    // {
    // _log.error("Errore nel recupero del totale delle fatture fornitore ricevute nel mese corrente", e);
    // throw new SQLException(e);
    // }
    // }

    public Integer insert(NotaCreditoFornitoreDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITOFORNITORE_I01"), Integer.class, dto.getNumeroDocumentoFornitore(), dto.getDataDocumentoFornitore(), dto.getNumDocumento(), StringUtils.defaultIfEmpty(dto.getParticella(), null), StringUtils.defaultIfEmpty(dto.getDataDocumento(), null), dto.getIdProgetto(), dto.getIdFornitore(), dto.getIdTipoPagamento(), dto.getDescrizioneBanca(), dto.getIban(), dto.getCin(), dto.getAbi(), dto.getCab(), dto.getConto(), dto.getBic(), dto.getIndirizzoIntestazione(), dto.getCapIntestazione(), dto.getCittaIntestazione(), dto.getProvinciaIntestazione(), dto.getNazioneIntestazione(), dto.getIndirizzoDestinazione(), dto.getCapDestinazione(), dto.getCittaDestinazione(), dto.getProvinciaDestinazione(), dto.getNazioneDestinazione(), dto.getEsigibilitaDifferita(), dto.getIdCausaleEsigibilitaDifferita(), dto.getTipoComunicazione(), dto.getIdMagazzino(), dto.getIdRitenutaPrevidenziale(), dto.getPercRitenutaPrevidenziale(), dto.getIdContropartitaRitenutaPrevidenziale(), dto.getImportoRitenutaAcconto(), dto.getIdFatturaFornitore(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio della fattura fornitore", e);
            throw new SQLException(e);
        }
    }

    public void insertProdotto(ProdottoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITOFORNITORE_I02"), dto.getIdDocumento(), dto.getIdProdotto(), dto.getQuantita(), dto.getIdUnitaMisura(), dto.getPrezzo(), dto.getSconto(), dto.getPrezzoImponibile(), dto.getIdAliquotaIva(), dto.getScarica(), dto.getNota(), dto.getIdColore(), dto.getIdTaglia(), dto.getIdScelta(), dto.getIdTono(), dto.getIdConto(), dto.getFmCodice(), dto.getFmDescrizione(), dto.getFmUnitaMisura(), dto.getFmTono(), dto.getFmScelta(), dto.getFmTaglia(), dto.getFmColore(), dto.getIdDivisione(), dto.getIdContoOverride());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio dell'articolo nella nota credito fornitore con id {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public Integer insertScadenzaPagamento(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITOFORNITORE_I04"), Integer.class, dto.getIdDocumento(), dto.getDtScadenza(), dto.getImporto(), dto.getIdRisorsa(), dto.getModalitaPagamento(), dto.getImportoSpeseIncasso(), dto.getIvaSpeseIncasso(), dto.getRifPagamento(), dto.getNote(), dto.getSaldato(), dto.getAcconto(), StringUtils.isEmpty(dto.getDtPagamento()) ? null : dto.getDtPagamento());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio della scadenza di pagamento nella nota credito fornitore con id {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public void insertSpesaIncasso(SpesaIncassoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITOFORNITORE_I03"), dto.getIdFattura(), dto.getIdSpesaIncasso(), dto.getIdAliquotaIva(), dto.getImporto());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio della spesa di incasso nella nota credito fornitore con id {}", dto.getIdFattura(), e);
            throw new SQLException(e);
        }
    }

    public boolean isExistentNumero(Integer numero,
                                    String particella,
                                    String data,
                                    Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITOFORNITORE_S02"), Long.class, numero, StringUtils.defaultIfEmpty(particella, null), StringUtils.defaultIfEmpty(particella, null), id, StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null));
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione dell'esistenza della nota credito fornitore con numero {}, particella {} e data {}", numero, StringUtils.defaultIfEmpty(particella, "<vuota>"), data, e);
            throw new SQLException(e);
        }
    }

    public void update(NotaCreditoFornitoreDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITOFORNITORE_U01"), dto.getNumeroDocumentoFornitore(), dto.getDataDocumentoFornitore(), dto.getNumDocumento(), StringUtils.defaultIfEmpty(dto.getParticella(), null), StringUtils.defaultIfEmpty(dto.getDataDocumento(), null), dto.getIdProgetto(), dto.getIdFornitore(), dto.getIdTipoPagamento(), dto.getDescrizioneBanca(), dto.getIban(), dto.getCin(), dto.getAbi(), dto.getCab(), dto.getConto(), dto.getBic(), dto.getIndirizzoIntestazione(), dto.getCapIntestazione(), dto.getCittaIntestazione(), dto.getProvinciaIntestazione(), dto.getNazioneIntestazione(), dto.getIndirizzoDestinazione(), dto.getCapDestinazione(), dto.getCittaDestinazione(), dto.getProvinciaDestinazione(), dto.getNazioneDestinazione(), dto.getEsigibilitaDifferita(), dto.getIdCausaleEsigibilitaDifferita(), dto.getTipoComunicazione(), dto.getIdMagazzino(), dto.getIdRitenutaPrevidenziale(), dto.getPercRitenutaPrevidenziale(), dto.getIdContropartitaRitenutaPrevidenziale(), dto.getImportoRitenutaAcconto(), dto.getIdFatturaFornitore(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento della nota credito fornitore con id {}", dto.getId());
            throw new SQLException(e);
        }
    }

    public void updateScadenzaPagamento(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITOFORNITORE_U02"), dto.getDtScadenza(), dto.getImporto(), dto.getIdRisorsa(), dto.getModalitaPagamento(), dto.getImportoSpeseIncasso(), dto.getIvaSpeseIncasso(), dto.getRifPagamento(), dto.getNote(), dto.getSaldato(), dto.getAcconto(), StringUtils.isEmpty(dto.getDtPagamento()) ? null : dto.getDtPagamento(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento della scadenza di pagamento (nota credito fornitore) {} ", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

