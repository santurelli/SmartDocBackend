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
import it.tinna.smartdoc.shared.dto.documenti.OrdineDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;

public class OrdiniDao extends BaseDao
{

    public OrdiniDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(OrdineDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ORDINI_D04"), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione dell'ordine {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void deleteProdottiById(long id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ORDINI_D01"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione dei prodotti dell'ordine {}", id, e);
            throw new SQLException(e);
        }
    }

    public void deleteSpeseIncassoById(long id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ORDINI_D02"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione delle spese incasso dell'ordine {}", id, e);
            throw new SQLException(e);
        }
    }

    public void deleteScadenzePagamento(long id) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ORDINI_D03"), id);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione delle scadenze pagamento dell'ordine {}", id, e);
            throw new SQLException(e);
        }
    }

    public Integer insert(OrdineDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("ORDINI_I01"), Integer.class,
                dto.getNumDocumento(), StringUtils.defaultIfEmpty(dto.getParticella(), null),
                StringUtils.defaultIfEmpty(dto.getDataDocumento(), null),
                dto.getIdCausaleTrasporto(), StringUtils.defaultIfEmpty(dto.getDataOraTrasporto(), null),
                dto.getIdTipoPorto(), dto.getIdVettore(), dto.getIdAspettoBeni(), dto.getColli(), dto.getPallet(),
                dto.getIdFornitore(), dto.getIdTipoPagamento(), dto.getDescrizioneBanca(), dto.getIban(), dto.getCin(),
                dto.getAbi(), dto.getCab(), dto.getConto(), dto.getBic(), dto.getAcconto(),
                dto.getIndirizzoIntestazione(), dto.getCapIntestazione(), dto.getCittaIntestazione(), dto.getProvinciaIntestazione(), dto.getNazioneIntestazione(),
                dto.getIndirizzoDestinazione(), dto.getCapDestinazione(), dto.getCittaDestinazione(), dto.getProvinciaDestinazione(), dto.getNazioneDestinazione(),
                dto.getIdNsBanca(), dto.getIdMagazzino(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio dell'ordine (numero: {})", dto.getNumDocumento(), e);
            throw new SQLException(e);
        }
    }

    public void update(OrdineDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ORDINI_U01"),
                dto.getNumDocumento(), StringUtils.defaultIfEmpty(dto.getParticella(), null),
                StringUtils.defaultIfEmpty(dto.getDataDocumento(), null),
                dto.getIdCausaleTrasporto(), StringUtils.defaultIfEmpty(dto.getDataOraTrasporto(), null),
                dto.getIdTipoPorto(), dto.getIdVettore(), dto.getIdAspettoBeni(), dto.getColli(), dto.getPallet(),
                dto.getIdFornitore(), dto.getIdTipoPagamento(), dto.getDescrizioneBanca(), dto.getIban(), dto.getCin(),
                dto.getAbi(), dto.getCab(), dto.getConto(), dto.getBic(), dto.getAcconto(),
                dto.getIndirizzoIntestazione(), dto.getCapIntestazione(), dto.getCittaIntestazione(), dto.getProvinciaIntestazione(), dto.getNazioneIntestazione(),
                dto.getIndirizzoDestinazione(), dto.getCapDestinazione(), dto.getCittaDestinazione(), dto.getProvinciaDestinazione(), dto.getNazioneDestinazione(),
                dto.getIdNsBanca(), dto.getIdMagazzino(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dell'ordine {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void insertProdotto(ProdottoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ORDINI_I02"), dto.getIdDocumento(), dto.getIdProdotto(),
                dto.getQuantita(), dto.getIdUnitaMisura(), dto.getPrezzo(), dto.getSconto(), dto.getIdAliquotaIva(),
                dto.getNota(), dto.getIdColore(), dto.getIdTaglia(), dto.getIdScelta(), dto.getIdTono());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio dell'articolo nell'ordine con id {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public void insertSpesaIncasso(SpesaIncassoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("ORDINI_I03"), dto.getIdFattura(), dto.getIdSpesaIncasso(), dto.getIdAliquotaIva(), dto.getImporto());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio della spesa di incasso nell'ordine con id {}", dto.getIdFattura(), e);
            throw new SQLException(e);
        }
    }

    public Integer insertScadenzaPagamento(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("ORDINI_I04"), Integer.class, dto.getIdDocumento(),
                dto.getDtScadenza(), dto.getImporto(), dto.getIdRisorsa(), dto.getModalitaPagamento(), dto.getImportoSpeseIncasso(),
                dto.getIvaSpeseIncasso(), dto.getRifPagamento(), dto.getNote(), dto.getSaldato(), dto.getAcconto(),
                StringUtils.isEmpty(dto.getDtPagamento()) ? null : dto.getDtPagamento());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio della scadenza di pagamento nell'ordine con id {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public List<OrdineDto> getList(String dtFrom,
                                   String dtTo,
                                   String soggetto,
                                   Integer length,
                                   Integer start,
                                   String orderColumn,
                                   String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("ORDINI_S01");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("ORDER_BY", StringUtils.isNotEmpty(orderColumn) ? orderColumn + " " + orderDir : "d_e_ordini.data_ordine DESC, d_e_ordini.num_ordine DESC");
        valuesMap.put("LIMIT", length != null && start != null ? "LIMIT " + length + " OFFSET " + start : "");
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            BeanPropertyRowMapper<OrdineDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(OrdineDto.class);
            String soggettoLike = StringUtils.isEmpty(soggetto) ? null : "%" + soggetto.toLowerCase() + "%";
            return jdbcTemplate.query(query, rowMapper, dtFrom, dtTo, soggettoLike, soggettoLike);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco ordini", e);
            throw new SQLException(e);
        }
    }

    public OrdineDto getById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<OrdineDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(OrdineDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("ORDINI_S03"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'ordine {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<ProdottoDocumentoDto> getListProdottiById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ProdottoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ProdottoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("ORDINI_S04"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei prodotti dell'ordine {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<SpesaIncassoDocumentoDto> getListSpeseIncassoById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<SpesaIncassoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(SpesaIncassoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("ORDINI_S05"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle spese incasso dell'ordine {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<ScadenzaPagamentoDocumentoDto> getScadenzePagamento(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("ORDINI_S07"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle scadenze pagamento dell'ordine {}", id, e);
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
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("ORDINI_S02"), Long.class, numero,
                StringUtils.defaultIfEmpty(particella, null), StringUtils.defaultIfEmpty(particella, null), id,
                StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null));
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione dell'esistenza dell'ordine con numero {}, particella {} e data {}", numero, particella, data, e);
            throw new SQLException(e);
        }
    }

    public Integer getNextNum(String data) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("ORDINI_S06"), Integer.class, data, data, data, data);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return 1;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del prossimo numero ordine", e);
            throw new SQLException(e);
        }
    }

    public List<OrdineDto> getOrdiniAperti(long idFornitore) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<OrdineDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(OrdineDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("ORDINI_APERTI_S01"), rowMapper, idFornitore);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero degli ordini aperti per il fornitore {}", idFornitore, e);
            throw new SQLException(e);
        }
    }

}
