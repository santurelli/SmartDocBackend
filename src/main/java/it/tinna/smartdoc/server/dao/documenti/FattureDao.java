package it.tinna.smartdoc.server.dao.documenti;

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

import java.util.ArrayList;
import java.util.List;
import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.DateUtility;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.StatoPagamentoFattura;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;

public class FattureDao extends BaseDao
{

    public FattureDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void aggiornaTotaliFattura(double totale,
                                      double totalePagato,
                                      long idFattura) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_U06"), totale, totalePagato, idFattura);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dei totali per la fattura {}", idFattura, e);
        }
    }

    public void associaDoc(Integer idDocPadre,
                           String tipoDocPadre,
                           Integer idFattura) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("DOCUMENTI_I01"), idDocPadre, tipoDocPadre, idFattura, ISharedConstants.TIPODOCASSOCIATO_FATTURA);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'associazione del documento {} di tipo {} alla fattura {}", idDocPadre, tipoDocPadre, idFattura);
            throw new SQLException(e);
        }
    }

    public void delete(FatturaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_D04"), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione della fattura {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void deleteProdottiById(long id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_D01"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione degli articoli dalla fattura {}", id, e);
            throw new SQLException(e);
        }
    }

    public void deleteScadenzaPagamento(Integer id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_D05"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione della scadenza di pagamento con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public void deleteScadenzePagamento(long id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_D03"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione delle scadenze di pagamento della fattura {}", id, e);
            throw new SQLException(e);
        }
    }

    public void deleteSpeseIncassoById(long id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_D02"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione delle spese di incasso della fattura {}", id, e);
            throw new SQLException(e);
        }
    }

    public FatturaDto getById(long id) throws SQLException
    {
        for (int attempt = 1; attempt <= 2; attempt++)
        {
            try
            {
                BeanPropertyRowMapper<FatturaDto> rowMapper = new BeanPropertyRowMapper<>();
                rowMapper.setMappedClass(FatturaDto.class);
                return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S03"), rowMapper, id);
            }
            catch ( EmptyResultDataAccessException e )
            {
                if (attempt < 2)
                {
                    _log.warn("Fattura {} non trovata al tentativo {}, riprovo dopo 300ms (possibile race condition RLS/pool)", id, attempt);
                    try { Thread.sleep(300); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
                else
                {
                    _log.error("Nessuna fattura trovata con id {} dopo {} tentativi", id, attempt);
                    return null;
                }
            }
            catch ( DataAccessException e )
            {
                _log.error("Errore nel recupero della fattura con id {}", id, e);
                throw new SQLException(e);
            }
        }
        return null;
    }

    public List<FatturaDto> getByProgetto(long idProgetto,
                                          int length,
                                          int start,
                                          int orderColumn,
                                          String orderDir) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<FatturaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(FatturaDto.class);
            String query = FileQueryReader.getQuery("FATTURE_S20");
            List<Object> params = new ArrayList<>();
            Map<String, String> valuesMap = new HashMap<>();
            params.add(idProgetto);
            if ( orderColumn == 1 )
            {
                // data fattura
                valuesMap.put("ORDER_BY", new StringBuilder("5 ").append(orderDir).toString());
            }
            else if ( orderColumn == 2 )
            {
                // numero e particella fattura
                valuesMap.put("ORDER_BY", new StringBuilder("1 ").append(orderDir).append("7 ").append(orderDir).toString());
            }
            else if ( orderColumn == 3 )
            {
                // cliente
                valuesMap.put("ORDER_BY", new StringBuilder("8 ").append(orderDir).toString());
            }
            else if ( orderColumn == 4 )
            {
                // agente
                valuesMap.put("ORDER_BY", new StringBuilder("12 ").append(orderDir).toString());
            }
            else if ( orderColumn == 5 )
            {
                // totale
                valuesMap.put("ORDER_BY", new StringBuilder("9 ").append(orderDir).toString());
            }
            else if ( orderColumn == 6 )
            {
                // totale da pagare
                valuesMap.put("ORDER_BY", new StringBuilder("11 ").append(orderDir).toString());
            }
            else
            {
                // data fattura
                valuesMap.put("ORDER_BY", new StringBuilder("5 ").append(orderDir).toString());
            }
            if ( length != -1 && start != -1 )
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
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella ricerca delle fatture associate al progetto {}", idProgetto, e);
            throw new SQLException(e);
        }
    }

    /**
     * Usato quando si crea una nota debito per restituire tutte le fatture a cui è associabile il documento
     * 
     * @param idCliente
     * @param dataNotadebito
     * @return
     * @throws SQLException
     */
    public List<MovimentiDocumentoDto> getFattureAssociabili(long idCliente,
                                                             String dataNotadebito) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<MovimentiDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(MovimentiDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S23"), rowMapper, idCliente, dataNotadebito);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle fatture associabili alla nota debito per il cliente {} e la data {}", idCliente, dataNotadebito, e);
            throw new SQLException(e);
        }
    }

    public List<Long> getFattureElettronicheDaInviare(long[] idFatture) throws SQLException
    {
        try
        {
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S24"), new ResultSetExtractor<List<Long>>()
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
            _log.error("Errore nel recupero dell'elenco delle fatture elettroniche da inviare", e);
            throw new SQLException(e);
        }
    }

    public long getInsolute() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery(""), Long.class);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del numero di fatture insolute", e);
            throw new SQLException(e);
        }
    }

    public List<MovimentiDocumentoDto> getList(String tipoDocumento,
                                               Integer idCliente,
                                               String dtFrom,
                                               String dtTo,
                                               Integer idAgente,
                                               String stato,
                                               String statoFatturaElettronica,
                                               Integer length,
                                               Integer start,
                                               Integer orderColumn,
                                               String orderDir,
                                               String numDocumento,
                                               String particella) throws SQLException
    {
        String query = FileQueryReader.getQuery("FATTURE_S16");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(dtFrom) ? null : dtFrom);
        params.add(StringUtils.isEmpty(dtTo) ? null : dtTo);
        params.add(idCliente);
        params.add(idAgente);
        params.add(StringUtils.defaultIfBlank(statoFatturaElettronica, null));
        Map<String, String> valuesMap = new HashMap<>();
        
        if ( StringUtils.isNotBlank(numDocumento) )
        {
            valuesMap.put("NUM_DOCUMENTO", "AND num_fattura::text = ?");
            params.add(numDocumento);
        }
        else
        {
            valuesMap.put("NUM_DOCUMENTO", "");
        }

        if ( StringUtils.isNotBlank(tipoDocumento) )
        {
            valuesMap.put("TIPO_FATTURA", "AND tipo_fattura = ?");
            params.add(tipoDocumento);
        }
        else
        {
            valuesMap.put("TIPO_FATTURA", "");
        }

        if ( StringUtils.isNotBlank(particella) )
        {
            valuesMap.put("PARTICELLA", "AND d_e_fatture.particella ILIKE ?");
            params.add("%" + particella.trim() + "%");
        }
        else
        {
            valuesMap.put("PARTICELLA", "");
        }
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
            // data fattura
            valuesMap.put("ORDER_BY", new StringBuilder("6 ").append(orderDir).toString());
        }
        else if ( orderColumn == 2 )
        {
            // numero e particella fattura
            valuesMap.put("ORDER_BY", new StringBuilder("3 ").append(orderDir).append(", 7 ").append(orderDir).toString());
        }
        else if ( orderColumn == 3 )
        {
            // cliente
            valuesMap.put("ORDER_BY", new StringBuilder("9 ").append(orderDir).toString());
        }
        else
        {
            // ordinamento predefinito per data fattura
            valuesMap.put("ORDER_BY", new StringBuilder("6 ").append(orderDir).toString());
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

    public List<SpesaIncassoDocumentoDto> getListSpeseIncassoById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<SpesaIncassoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(SpesaIncassoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S05"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco spese incasso per la fattura {}", id);
            throw new SQLException(e);
        }
    }

    public List<ProdottoDocumentoDto> getListProdottiById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ProdottoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ProdottoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S04"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco degli articoli nella fattura {}", id);
            throw new SQLException(e);
        }
    }

    public Integer getNextNum(String data,
                              int flFatturaElettronica,
                              TipoFattura tipoFattura) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S07"), Integer.class, flFatturaElettronica, tipoFattura.name(), StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null), flFatturaElettronica, tipoFattura.name(), StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null));
        }
        catch ( EmptyResultDataAccessException e )
        {
            return 1;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione del prossimo numero fattura", e);
            throw new SQLException(e);
        }
    }

    public ScadenzaPagamentoDocumentoDto getScadenzaPagamento(long idScadenza) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDocumentoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S17"), rowMapper, idScadenza);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna scadenza di pagamento fattura trovata con id {}", idScadenza);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della scadenza pagamento con id {}", idScadenza);
            throw new SQLException(e);
        }
    }

    public List<ScadenzaPagamentoDocumentoDto> getScadenzePagamento(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S06"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco scadenze pagamento per la fattura {}", id);
            throw new SQLException(e);
        }
    }

    public double getTotale(long idFattura) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S21"), Double.class, idFattura);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessun valore restituito dal calcolo del totale della fattura {}", idFattura);
            throw new SQLException();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel calcolo del totale della fattura {}", idFattura, e);
            throw new SQLException(e);
        }

    }

    public double getTotalePagato(long idFattura) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S22"), Double.class, idFattura);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessun valore restituito dal calcolo del totale pagato per la fattura {}", idFattura);
            throw new SQLException();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel calcolo del totale pagato per la fattura {}", idFattura, e);
            throw new SQLException(e);
        }

    }

    public List<MovimentiDocumentoDto> getUltimeFatture() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<MovimentiDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(MovimentiDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("DOCUMENTI_S04"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle ultime fatture", e);
            throw new SQLException(e);
        }
    }

    public String getXmlFatturaElettronica(long idFattura) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S18"), String.class, idFattura);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna fattura trovata con id {}. Xml fattura elettronica impostato a null", idFattura);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'xml della fattura elettronica per la fattura {}", idFattura, e);
            throw new SQLException(e);
        }
    }

    public long insert(FatturaDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_I01"),
                                               Long.class,
                                               dto.getNumDocumento(),
                                               StringUtils.defaultIfBlank(dto.getParticella(), null),
                                               formatDateForQuery(dto.getDataDocumento()),
                                               dto.getIdListino(),
                                               dto.getIdAgente(),
                                               dto.getIdProgetto(),
                                               dto.getIdCausaleTrasporto(),
                                               StringUtils.defaultIfEmpty(dto.getDataOraTrasporto(), null),
                                               StringUtils.defaultIfEmpty(dto.getTarga(), null),
                                               dto.getIdTipoPorto(),
                                               dto.getIdVettore(),
                                               dto.getIdAspettoBeni(),
                                               dto.getColli(),
                                               dto.getPallet(),
                                               dto.getPesoNetto(),
                                               dto.getPesoLordo(),
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
                                               StringUtils.isEmpty(dto.getDtLiquidazioneProvvigione()) ? null : DateUtility.toTimestamp(dto.getDtLiquidazioneProvvigione()),
                                               dto.getEsigibilitaDifferita() == null ? 0 : dto.getEsigibilitaDifferita(),
                                               dto.getIdCausaleEsigibilitaDifferita(),
                                               dto.getTipoComunicazione(),
                                               dto.getCodiceUfficioDestinazione(),
                                               dto.getPec(),
                                               dto.getIdMagazzino(),
                                               dto.getSplitPayment(),
                                               dto.getFlFatturaElettronica(),
                                               StringUtils.defaultIfEmpty(dto.getCausale(), null),
                                               StringUtils.defaultIfEmpty(dto.getNumeroOrdineAcquisto(), null),
                                               formatDateForQuery(dto.getDataOrdineAcquisto()),
                                               StringUtils.defaultIfEmpty(dto.getCig(), null),
                                               StringUtils.defaultIfEmpty(dto.getCup(), null),
                                               StringUtils.defaultIfEmpty(dto.getDatiCommessa(), null),
                                               dto.getNumeroScontrino(),
                                               formatDateForQuery(dto.getDataScontrino()),
                                               dto.getTipoFattura().name(),
                                               dto.getStatoFatturaElettronica() == null ? null : dto.getStatoFatturaElettronica().name(),
                                               dto.getIdFatturaCollegata() == 0L ? null : dto.getIdFatturaCollegata(),
                                               dto.getFlRitenutaAcconto(),
                                               dto.getPercRitenutaAcconto(),
                                               dto.getImportoRitenutaAcconto(),
                                               dto.getTipoRitenuta(),
                                               dto.getFlRivalsaInps(),
                                               dto.getPercRivalsaInps(),
                                               dto.getImportoRivalsaInps(),
                                               dto.getTipoCassaInps(),
                                               dto.getPercImponibileRivalsa(),
                                               dto.getIdAliquotaIvaRivalsa(),
                                               dto.getSconto(),
                                               dto.getUserCreated());
        }
        catch ( DataAccessException | ParseException e )
        {
            _log.error("Errore nell'inserimento della fattura (numero: {}, data: {})", dto.getNumDocumento(), dto.getDataDocumento(), e);
            throw new SQLException(e);
        }
    }

    public void insertProdotto(ProdottoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_I02"), dto.getIdDocumento(), dto.getIdProdotto(), dto.getQuantita(), dto.getIdUnitaMisura(), dto.getPrezzo(), dto.getSconto(), dto.getPrezzoImponibile(), dto.getProvvigione(), dto.getIdAliquotaIva(), dto.getScarica(), dto.getNota(), dto.getIdColore(), dto.getIdTaglia(), dto.getIdScelta(), dto.getIdTono(), dto.getIdConto(), dto.getFmCodice(), dto.getFmDescrizione(), dto.getFmUnitaMisura(), dto.getFmTono(), dto.getFmScelta(), dto.getFmTaglia(), dto.getFmColore(), dto.getIdDivisione(), dto.getFlRitenuta());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dell'articolo nella fattura {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public Integer insertScadenzaPagamento(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_I04"), Integer.class, dto.getIdDocumento(), formatDateForQuery(dto.getDtScadenza()), dto.getImporto(), dto.getIdRisorsa(), dto.getModalitaPagamento(), dto.getImportoSpeseIncasso(), dto.getIvaSpeseIncasso(), dto.getRifPagamento(), dto.getNote(), dto.getSaldato(), dto.getAcconto(), formatDateForQuery(dto.getDtPagamento()));
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento della scadenza di pagamento per la fattura {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public void insertSpesaIncasso(SpesaIncassoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_I03"), dto.getIdFattura(), dto.getIdSpesaIncasso(), dto.getIdAliquotaIva(), dto.getImporto());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento della spesa di incasso per la fattura {}", dto.getIdFattura(), e);
            throw new SQLException(e);
        }
    }

    public boolean isExistentNumero(Integer numero,
                                    String particella,
                                    String data,
                                    int flFatturaElettronica,
                                    TipoFattura tipoFattura,
                                    Long id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S02"), Long.class, numero, StringUtils.defaultIfEmpty(particella, null), StringUtils.defaultIfEmpty(particella, null), id, StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null), flFatturaElettronica, tipoFattura.name());
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione dell'esistenza del numero documento {}, particella {} e tipo {}", numero, StringUtils.defaultIfBlank(particella, "<vuoto>"), tipoFattura.name(), e);
            throw new SQLException(e);
        }
    }

    public void salvaXmlFatturaElettronica(long idFattura,
                                           String xml) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_U05"), xml, idFattura);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio dell'xml della fattura elettronica per la fattura {}", idFattura, e);
            throw new SQLException(e);
        }
    }

    public void update(FatturaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_U01"),
                                dto.getNumDocumento(),
                                StringUtils.defaultIfBlank(dto.getParticella(), null),
                                formatDateForQuery(dto.getDataDocumento()),
                                dto.getIdListino(),
                                dto.getIdAgente(),
                                dto.getIdProgetto(),
                                dto.getIdCausaleTrasporto(),
                                StringUtils.defaultIfEmpty(dto.getDataOraTrasporto(), null),
                                StringUtils.defaultIfEmpty(dto.getTarga(), null),
                                dto.getIdTipoPorto(),
                                dto.getIdVettore(),
                                dto.getIdAspettoBeni(),
                                dto.getColli(),
                                dto.getPallet(),
                                dto.getPesoNetto(),
                                dto.getPesoLordo(),
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
                                dto.getSconto(),
                                dto.getProvinciaIntestazione(),
                                dto.getNazioneIntestazione(),
                                dto.getIndirizzoDestinazione(),
                                dto.getCapDestinazione(),
                                dto.getCittaDestinazione(),
                                dto.getProvinciaDestinazione(),
                                dto.getNazioneDestinazione(),
                                StringUtils.isEmpty(dto.getDtLiquidazioneProvvigione()) ? null : DateUtility.toTimestamp(dto.getDtLiquidazioneProvvigione()),
                                dto.getEsigibilitaDifferita() == null ? 0 : dto.getEsigibilitaDifferita(),
                                dto.getIdCausaleEsigibilitaDifferita(),
                                dto.getTipoComunicazione(),
                                dto.getCodiceUfficioDestinazione(),
                                dto.getPec(),
                                dto.getIdMagazzino(),
                                dto.getSplitPayment(),
                                dto.getFlFatturaElettronica(),
                                StringUtils.defaultIfEmpty(dto.getCausale(), null),
                                StringUtils.defaultIfEmpty(dto.getNumeroOrdineAcquisto(), null),
                                formatDateForQuery(dto.getDataOrdineAcquisto()),
                                StringUtils.defaultIfEmpty(dto.getCig(), null),
                                StringUtils.defaultIfEmpty(dto.getCup(), null),
                                StringUtils.defaultIfEmpty(dto.getDatiCommessa(), null),
                                dto.getNumeroScontrino(),
                                formatDateForQuery(dto.getDataScontrino()),
                                dto.getTipoFattura().name(),
                                dto.getStatoFatturaElettronica() == null ? null : dto.getStatoFatturaElettronica().name(),
                                dto.getIdFatturaCollegata() == 0L ? null : dto.getIdFatturaCollegata(),
                                dto.getFlRitenutaAcconto(),
                                dto.getPercRitenutaAcconto(),
                                dto.getImportoRitenutaAcconto(),
                                dto.getTipoRitenuta(),
                                dto.getFlRivalsaInps(),
                                dto.getPercRivalsaInps(),
                                dto.getImportoRivalsaInps(),
                                dto.getTipoCassaInps(),
                                dto.getPercImponibileRivalsa(),
                                dto.getIdAliquotaIvaRivalsa(),
                                dto.getUserLastUpdate(),
                                dto.getId());
        }
        catch ( DataAccessException | ParseException e )
        {
            _log.error("Errore nell'aggiornamento della fattura {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void updateScadenzaPagamento(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_U02"), formatDateForQuery(dto.getDtScadenza()), dto.getImporto(), dto.getIdRisorsa(), dto.getModalitaPagamento(), dto.getImportoSpeseIncasso(), dto.getIvaSpeseIncasso(), dto.getRifPagamento(), dto.getNote(), dto.getSaldato(), dto.getAcconto(), formatDateForQuery(dto.getDtPagamento()), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento della scadenza di pagamento {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    private String formatDateForQuery(String date)
    {
        if ( StringUtils.isBlank(date) ) return null;
        try
        {
            return DateUtility.format(DateUtility.parse(date), "dd/MM/yyyy");
        }
        catch ( Exception e )
        {
            _log.error("Errore durante la normalizzazione della data {}", date, e);
            return date;
        }
    }

}

