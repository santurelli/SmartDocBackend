package it.tinna.smartdoc.server.dao.documenti;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.DateUtility;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.RiepilogoFattureDto;
import it.tinna.smartdoc.shared.dto.documenti.StatoPagamentoFattura;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;

public class NoteCreditoDao extends BaseDao
{

    public NoteCreditoDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void aggiornaProgrUnivocFileFatturaElettronica(Integer idNotaCredito,
                                                          String progUnivoco) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_U03"), progUnivoco, idNotaCredito);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento del progressivo univico invio con il valore {} per la nota credito {}", progUnivoco, idNotaCredito, e);
            throw new SQLException(e);
        }
    }

    public void aggiornaTotaliNotaCredito(double totale,
                                          double totalePagato,
                                          long idNotaCredito) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_U05"), totale, totalePagato, idNotaCredito);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dei totali per la nota di credito {}", idNotaCredito, e);
        }
    }

    public void delete(NotaCreditoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_D03"), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione della nota credito {}", dto.getId());
            throw new SQLException(e);
        }
    }

    public void deleteProdottiById(long id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_D01"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'eliminazione degli articoli dalla nota credito {}", id, e);
            throw new SQLException(e);
        }
    }

    public void deleteScadenzaPagamento(long idScadenzaPagamento) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_D04"), idScadenzaPagamento);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione della scadenza di pagamento con id {} (note credito)", idScadenzaPagamento, e);
            throw new SQLException(e);
        }
    }

    public void deleteScadenzePagamento(long idNotaCredito) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_D02"), idNotaCredito);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione delle scadenze di pagamento per la nota credito {}", idNotaCredito, e);
            throw new SQLException(e);
        }
    }

    public NotaCreditoDto getById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<NotaCreditoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(NotaCreditoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S03"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna nota credito trovata con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della nota credito con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public NotaCreditoDto getByProgressivoInvio(Integer progressivoInvio) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<NotaCreditoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(NotaCreditoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S08"), rowMapper, progressivoInvio);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna nota credito trovata con progressivo invio {}", progressivoInvio);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della nota credito con progressivo invio {}", progressivoInvio, e);
            throw new SQLException(e);
        }
    }

    public List<MovimentiDocumentoDto> getFattureAssociabili(long idCliente,
                                                             String dataNotaCredito) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<MovimentiDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(MovimentiDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S10"), rowMapper, idCliente, dataNotaCredito);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle fatture associabili alla nota credito per il cliente {} e la data {}", idCliente, dataNotaCredito, e);
            throw new SQLException(e);
        }
    }

    public List<MovimentiDocumentoDto> getList(Integer idCliente,
                                               String dtFrom,
                                               String dtTo,
                                               Integer idAgente,
                                               String stato,
                                               Integer length,
                                               Integer start,
                                               Integer orderColumn,
                                               String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("NOTECREDITO_S10");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(dtFrom) ? null : dtFrom);
        params.add(StringUtils.isEmpty(dtTo) ? null : dtTo);
        params.add(idCliente);
        params.add(idAgente);
        Map<String, String> valuesMap = new HashMap<>();
        if ( StringUtils.isNotBlank(stato) )
        {
            if ( stato.equals(StatoPagamentoFattura.NON_PAGATA.getValue()) )
            {
                valuesMap.put("STATO", "AND (totale - totale_pagato) > 0 AND totale_pagato = 0");
            }
            else if ( stato.equals(StatoPagamentoFattura.PARZIALMENTE_PAGATA.getValue()) )
            {
                valuesMap.put("STATO", "AND (totale - totale_pagato) > 0 AND totale_pagato > 0");
            }
            else
            {
                valuesMap.put("STATO", "AND (totale - totale_pagato) = 0");
            }
        }
        else
        {
            valuesMap.put("STATO", "");
        }
        if ( orderColumn == 1 )
        {
            // data nota credito
            valuesMap.put("ORDER_BY", new StringBuilder("5 ").append(orderDir).toString());
        }
        else if ( orderColumn == 2 )
        {
            // numero e particella nota credito
            valuesMap.put("ORDER_BY", new StringBuilder("2 ").append(orderDir).append(", 7").append(orderDir).toString());
        }
        else if ( orderColumn == 3 )
        {
            // cliente
            valuesMap.put("ORDER_BY", new StringBuilder("8 ").append(orderDir).toString());
        }
        else
        {
            valuesMap.put("ORDER_BY", new StringBuilder("5 ").append(orderDir).toString());
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
            BeanPropertyRowMapper<MovimentiDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(MovimentiDocumentoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella ricerca delle fatture", e);
            throw new SQLException(e);
        }
    }

    public List<ProdottoDocumentoDto> getListProdottiById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ProdottoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ProdottoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("NOTECREDITO_S04"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero degli articoli nella nota credito {}", id, e);
            throw new SQLException(e);
        }
    }

    public Integer getNextNum(String data,
                               Integer flFatturaElettronica) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S05"), Integer.class, flFatturaElettronica, StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null), flFatturaElettronica, StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null));
        }
        catch ( EmptyResultDataAccessException e )
        {
            return 1;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione del prossimo numero nota credito", e);
            throw new SQLException(e);
        }
    }

    public List<Long> getNoteCreditoDaInviare(long[] idFatture) throws SQLException
    {
        try
        {
            return jdbcTemplate.query(FileQueryReader.getQuery("NOTECREDITO_S15"), new ResultSetExtractor<List<Long>>()
            {
                @Override
                public List<Long> extractData(ResultSet rs) throws SQLException, DataAccessException
                {
                    List<Long> l = null;
                    if ( idFatture != null && idFatture.length > 0 )
                    {
                        l = new ArrayList<Long>();
                        for (long id : idFatture) l.add(id);
                    }
                    List<Long> result = new ArrayList<>();
                    while (rs.next())
                    {
                        if ( l == null || (l != null && l.contains(rs.getLong(1))) )
                        {
                            result.add(rs.getLong(1));
                        }
                    }
                    return result;
                }
            });
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco delle note credito da inviare allo SDI", e);
            throw new SQLException(e);
        }
    }

    public RiepilogoFattureDto getRiepilogo(Integer idCliente,
                                            String dtFrom,
                                            String dtTo,
                                            Integer idAgente,
                                            String stato) throws SQLException
    {
        try
        {
            String query = FileQueryReader.getQuery("NOTECREDITO_S11");
            Map<String, String> valuesMap = new HashMap<>();
            if ( StringUtils.isNotBlank(stato) )
            {
                if ( stato.equals(StatoPagamentoFattura.NON_PAGATA.getValue()) )
                {
                    valuesMap.put("STATO", "AND totaleDaSaldare > 0 AND totaleSaldato = 0");
                }
                else if ( stato.equals(StatoPagamentoFattura.PARZIALMENTE_PAGATA.getValue()) )
                {
                    valuesMap.put("STATO", "AND totaleDaSaldare > 0 AND totaleSaldato > 0");
                }
                else
                {
                    valuesMap.put("STATO", "AND totaleDaSaldare = 0");
                }
            }
            else
            {
                valuesMap.put("STATO", "");
            }
            query = StrSubstitutor.replace(query, valuesMap);
            BeanPropertyRowMapper<RiepilogoFattureDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(RiepilogoFattureDto.class);
            return jdbcTemplate.queryForObject(query, rowMapper, StringUtils.isEmpty(dtFrom) ? null : dtFrom, StringUtils.isEmpty(dtTo) ? null : dtTo, idCliente, idAgente);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new RiepilogoFattureDto();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei dati globali della ricerca delle note credito", e);
            throw new SQLException(e);
        }
    }

    public ScadenzaPagamentoDocumentoDto getScadenzaPagamento(long idScadenzaPagamento) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDocumentoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S07"), rowMapper, idScadenzaPagamento);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna scadenza di pagamento trovata con id {}", idScadenzaPagamento);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della scadenza di pagamento con id {}", idScadenzaPagamento, e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzaPagamentoDocumentoDto> getScadenzePagamento(long idNotaCredito) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("NOTECREDITO_S06"), rowMapper, idNotaCredito);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco scadenze di pagamento per la nota credito {}", idNotaCredito, e);
            throw new SQLException(e);
        }
    }

    public double getTotale(long idNotaCredito) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S13"), Double.class, idNotaCredito);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessun valore restituito dal calcolo del totale della nota di credito {}", idNotaCredito);
            throw new SQLException();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel calcolo del totale della nota di credito {}", idNotaCredito, e);
            throw new SQLException(e);
        }

    }

    /**
     * Calcola l'importo totale delle note di credito emesse nel mese corrente
     * 
     * @return
     * @throws Exception
     */
    public BigDecimal getTotaleMeseCorrente() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S09"), BigDecimal.class);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel calcolo del totale note credito emesse nel mese corrente", e);
            throw new SQLException(e);
        }
    }

    public double getTotalePagato(long idNotaCredito) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S14"), Double.class, idNotaCredito);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessun valore restituito dal calcolo del totale pagato per la nota di credito {}", idNotaCredito);
            throw new SQLException();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel calcolo del totale pagato per la nota di credito {}", idNotaCredito, e);
            throw new SQLException(e);
        }

    }

    public String getXmlFatturaElettronica(long idNotaCredito) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S12"), String.class, idNotaCredito);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna nota credito trovata con id {}. Xml fattura elettronica impostato a null", idNotaCredito);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'xml della fattura elettronica per la nota credito {}", idNotaCredito, e);
            throw new SQLException(e);
        }
    }

    public long insert(NotaCreditoDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_I01"),
                                               Long.class,
                                               dto.getNumDocumento(),
                                               StringUtils.defaultIfEmpty(dto.getParticella(), null),
                                               StringUtils.defaultIfEmpty(dto.getDataDocumento(), null),
                                               dto.getIdProgetto(),
                                               dto.getIdCliente(),
                                               dto.getIdListino(),
                                               dto.getIdAgente(),
                                               dto.getIdTipoPagamento(),
                                               dto.getIdNsBanca(),
                                               dto.getDescrizioneBanca(),
                                               dto.getIban(),
                                               dto.getCin(),
                                               dto.getAbi(),
                                               dto.getCab(),
                                               dto.getConto(),
                                               dto.getBic(),
                                               dto.getIndirizzoIntestazione(),
                                               dto.getCapIntestazione(),
                                               dto.getCittaIntestazione(),
                                               dto.getProvinciaIntestazione(),
                                               dto.getNazioneIntestazione(),
                                               dto.getIndirizzoDestinazione(),
                                               dto.getCapDestinazione(),
                                               dto.getCittaDestinazione(),
                                               dto.getProvinciaDestinazione(),
                                               dto.getNazioneDestinazione(),
                                               dto.getCodiceFiscale(),
                                               dto.getPartitaIva(),
                                               StringUtils.isEmpty(dto.getDtLiquidazioneProvvigione()) ? null : DateUtility.toTimestamp(dto.getDtLiquidazioneProvvigione()),
                                               dto.getEsigibilitaDifferita(),
                                               dto.getIdCausaleEsigibilitaDifferita(),
                                               dto.getIdFattura(),
                                               dto.getTipoComunicazione(),
                                               dto.getCodiceUfficioDestinazione(),
                                               dto.getIdMagazzino(),
                                               dto.getSplitPayment(),
                                               dto.getFlFatturaElettronica(),
                                               StringUtils.defaultIfEmpty(dto.getCausale(), null),
                                               StringUtils.defaultIfEmpty(dto.getNumeroOrdineAcquisto(), null),
                                               StringUtils.defaultIfEmpty(dto.getDataOrdineAcquisto(), null),
                                               StringUtils.defaultIfEmpty(dto.getCig(), null),
                                               StringUtils.defaultIfEmpty(dto.getCup(), null),
                                               StringUtils.defaultIfEmpty(dto.getDatiCommessa(), null),
                                               dto.getStatoFatturaElettronica() == null ? null : dto.getStatoFatturaElettronica().name(),
                                               dto.getFlRivalsaInps(),
                                               dto.getPercRivalsaInps(),
                                               dto.getImportoRivalsaInps(),
                                               dto.getTipoCassaInps(),
                                               dto.getUserCreated());
        }
        catch ( DataAccessException | ParseException e )
        {
            _log.error("Errore nel salvataggio della nota credito", e);
            throw new SQLException(e);
        }
    }

    public void insertProdotto(ProdottoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_I02"), dto.getIdDocumento(), dto.getIdProdotto(), dto.getQuantita(), dto.getIdUnitaMisura(), dto.getPrezzo(), dto.getSconto(), dto.getPrezzoImponibile(), dto.getIdAliquotaIva(), dto.getNota(), dto.getIdColore(), dto.getIdTaglia(), dto.getIdScelta(), dto.getIdTono(), dto.getIdConto(), dto.getFmCodice(), dto.getFmDescrizione(), dto.getFmUnitaMisura(), dto.getFmTono(), dto.getFmScelta(), dto.getFmTaglia(), dto.getFmColore(), dto.getIdDivisione(), dto.getFlRitenuta());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dell'articolo nella nota credito {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public Integer insertScadenzaPagamento(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_I03"), Integer.class, dto.getIdDocumento(), dto.getDtScadenza(), dto.getImporto(), dto.getIdRisorsa(), dto.getModalitaPagamento(), dto.getImportoSpeseIncasso(), dto.getIvaSpeseIncasso(), dto.getRifPagamento(), dto.getNote(), dto.getSaldato(), dto.getAcconto(), StringUtils.isEmpty(dto.getDtPagamento()) ? null : dto.getDtPagamento());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento della scadenza di pagamento per la nota credito {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public boolean isExistentNumero(Integer numero,
                                    String particella,
                                    String data,
                                    Integer flFatturaElettronica,
                                    Long id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S02"), Long.class, numero, StringUtils.defaultIfEmpty(particella, null), StringUtils.defaultIfEmpty(particella, null), id, StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null), flFatturaElettronica);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione dell'esistenza del numero nota credito {}, particella {} e data {}", numero, StringUtils.defaultIfBlank(particella, "<vuoto>"), data, e);
            throw new SQLException(e);
        }
    }

    public void salvaXmlFatturaElettronica(long idFattura,
                                           String xml) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_U04"), xml, idFattura);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio dell'xml della fattura elettronica per la nota credito {}", idFattura, e);
            throw new SQLException(e);
        }
    }

    public void update(NotaCreditoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_U01"),
                                dto.getNumDocumento(),
                                StringUtils.defaultIfEmpty(dto.getParticella(), null),
                                StringUtils.defaultIfEmpty(dto.getDataDocumento(), null),
                                dto.getIdProgetto(),
                                dto.getIdListino(),
                                dto.getIdAgente(),
                                dto.getIdCliente(),
                                dto.getIdTipoPagamento(),
                                dto.getIdNsBanca(),
                                dto.getDescrizioneBanca(),
                                dto.getIban(),
                                dto.getCin(),
                                dto.getAbi(),
                                dto.getCab(),
                                dto.getConto(),
                                dto.getBic(),
                                dto.getIndirizzoIntestazione(),
                                dto.getCapIntestazione(),
                                dto.getCittaIntestazione(),
                                dto.getProvinciaIntestazione(),
                                dto.getNazioneIntestazione(),
                                dto.getIndirizzoDestinazione(),
                                dto.getCapDestinazione(),
                                dto.getCittaDestinazione(),
                                dto.getProvinciaDestinazione(),
                                dto.getNazioneDestinazione(),
                                dto.getCodiceFiscale(),
                                dto.getPartitaIva(),
                                StringUtils.isEmpty(dto.getDtLiquidazioneProvvigione()) ? null : DateUtility.toTimestamp(dto.getDtLiquidazioneProvvigione()),
                                dto.getEsigibilitaDifferita(),
                                dto.getIdCausaleEsigibilitaDifferita(),
                                dto.getIdFattura(),
                                dto.getTipoComunicazione(),
                                dto.getCodiceUfficioDestinazione(),
                                dto.getIdMagazzino(),
                                dto.getSplitPayment(),
                                dto.getFlFatturaElettronica(),
                                StringUtils.defaultIfEmpty(dto.getCausale(), null),
                                StringUtils.defaultIfEmpty(dto.getNumeroOrdineAcquisto(), null),
                                StringUtils.defaultIfEmpty(dto.getDataOrdineAcquisto(), null),
                                StringUtils.defaultIfEmpty(dto.getCig(), null),
                                StringUtils.defaultIfEmpty(dto.getCup(), null),
                                StringUtils.defaultIfEmpty(dto.getDatiCommessa(), null),
                                dto.getStatoFatturaElettronica() == null ? null : dto.getStatoFatturaElettronica().name(),
                                dto.getFlRivalsaInps(),
                                dto.getPercRivalsaInps(),
                                dto.getImportoRivalsaInps(),
                                dto.getTipoCassaInps(),
                                dto.getUserLastUpdate(),
                                dto.getId());
        }
        catch ( DataAccessException | ParseException e )
        {
            _log.error("Errore nell'aggiornamento della nota credito {}", dto.getId());
            throw new SQLException(e);
        }
    }

    public void updateScadenzaPagamento(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_U02"), dto.getDtScadenza(), dto.getImporto(), dto.getIdRisorsa(), dto.getModalitaPagamento(), dto.getImportoSpeseIncasso(), dto.getIvaSpeseIncasso(), dto.getRifPagamento(), dto.getNote(), dto.getSaldato(), dto.getAcconto(), StringUtils.isEmpty(dto.getDtPagamento()) ? null : dto.getDtPagamento(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento della scadenza di pagamento {} della nota credito {}", dto.getId(), dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

}
