package it.tinna.smartdoc.server.dao.documenti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.documenti.DdtDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;

@Repository
public class DdtDao extends BaseDao {

    public DdtDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private String formatDate(String date) {
        if (StringUtils.isEmpty(date)) return null;
        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) { // ISO YYYY-MM-DD
            String[] parts = date.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        }
        return date;
    }

    public void delete(DdtDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("DDT_D03"), dto.getUserLastUpdate(), dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nella cancellazione del ddt {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void deleteProdottiById(long id) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("DDT_D01"), id);
        } catch (DataAccessException e) {
            _log.error("Errore nella cancellazione degli articoli dal ddt {}", id, e);
            throw new SQLException(e);
        }
    }

    public void deleteSpeseIncassoById(long id) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("DDT_D02"), id);
        } catch (DataAccessException e) {
            _log.error("Errore nella cancellazione delle spese di incasso del ddt {}", id, e);
            throw new SQLException(e);
        }
    }

    public DdtDto getById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<DdtDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DdtDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("DDT_S01"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            _log.error("Nessun ddt trovato con id {}", id);
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero del ddt con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<MovimentiDocumentoDto> getList(Integer idCliente,
                                               String dtFrom,
                                               String dtTo,
                                               Integer idAgente,
                                               Integer idDocumento,
                                               Integer length,
                                               Integer start,
                                               String orderColumn,
                                               String orderDir) throws SQLException {
        String query = FileQueryReader.getQuery("DDT_S06");
        List<Object> params = new ArrayList<>();
        params.add(formatDate(dtFrom));
        params.add(formatDate(dtTo));
        params.add(idCliente);
        params.add(idAgente);
        params.add(idDocumento);
        Map<String, String> valuesMap = new HashMap<>();
        
        if (StringUtils.isNotEmpty(orderColumn)) {
            valuesMap.put("ORDER_BY", orderColumn + " " + orderDir);
        } else {
            valuesMap.put("ORDER_BY", "d_e_ddt.data_ddt DESC, d_e_ddt.num_ddt DESC");
        }

        if (length != null && start != null) {
            valuesMap.put("LIMIT", "LIMIT ? OFFSET ?");
            params.add(length);
            params.add(start);
        } else {
            valuesMap.put("LIMIT", "");
        }
        query = StringSubstitutor.replace(query, valuesMap);
        try {
            BeanPropertyRowMapper<MovimentiDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(MovimentiDocumentoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nella ricerca dei ddt", e);
            throw new SQLException(e);
        }
    }

    public List<SpesaIncassoDocumentoDto> getListSpeseIncassoById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<SpesaIncassoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(SpesaIncassoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("DDT_S03"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero delle spese di incasso per il ddt {}", id);
            throw new SQLException(e);
        }
    }

    public List<ProdottoDocumentoDto> getListProdottiById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<ProdottoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ProdottoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("DDT_S02"), rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero dell'elenco articoli nel ddt {}", id);
            throw new SQLException(e);
        }
    }

    public Integer getNextNum(String data) throws SQLException {
        String fData = formatDate(data);
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("DDT_S05"), Integer.class, fData, fData, fData, fData);
        } catch (EmptyResultDataAccessException e) {
            return 1;
        } catch (DataAccessException e) {
            _log.error("Errore nella determinazione del prossimo numero ddt", e);
            throw new SQLException(e);
        }
    }

    public long insert(DdtDto dto) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("DDT_I01"), Long.class, 
                dto.getNumDocumento(), 
                StringUtils.defaultIfEmpty(dto.getParticella(), null), 
                formatDate(dto.getDataDocumento()), 
                dto.getIdListino(), 
                dto.getIdAgente(), 
                dto.getIdProgetto(), 
                dto.getIdCausaleTrasporto(), 
                dto.getDataOraTrasporto(), // Warning: legacy used TO_TIMESTAMP(?,'DD/MM/YYYY HH24:MI')
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
                dto.getCodiceUfficioDestinazione(), 
                dto.getAcconto(), 
                dto.getIdMagazzino(), 
                dto.getUserCreated());
        } catch (DataAccessException e) {
            _log.error("Errore nell'inserimento del ddt", e);
            throw new SQLException(e);
        }
    }

    public void insertProdotto(ProdottoDocumentoDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("DDT_I02"), 
                dto.getIdDocumento(), 
                dto.getIdProdotto(), 
                dto.getQuantita(), 
                dto.getIdUnitaMisura(), 
                dto.getPrezzo(), 
                dto.getSconto(), 
                dto.getProvvigione(), 
                dto.getIdAliquotaIva(), 
                dto.getScarica(), 
                dto.getNota(), 
                dto.getIdColore(), 
                dto.getIdTaglia(), 
                dto.getIdScelta(), 
                dto.getIdTono(), 
                dto.getIdConto(), 
                dto.getFmCodice(), 
                dto.getFmDescrizione(), 
                dto.getFmUnitaMisura(), 
                dto.getFmTono(), 
                dto.getFmScelta(), 
                dto.getFmTaglia(), 
                dto.getFmColore());
        } catch (DataAccessException e) {
            _log.error("Errore nell'inserimento dell'articolo nel ddt {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public void insertSpesaIncasso(SpesaIncassoDocumentoDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("DDT_I03"), dto.getIdFattura(), dto.getIdSpesaIncasso(), dto.getIdAliquotaIva(), dto.getImporto());
        } catch (DataAccessException e) {
            _log.error("Errore nell'inserimento della spesa di incasso per il ddt {}", dto.getIdFattura(), e);
            throw new SQLException(e);
        }
    }

    public boolean isExistentNumero(Integer numero,
                                    String particella,
                                    String data,
                                    Long id) throws SQLException {
        String fData = formatDate(data);
        try {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("DDT_S04"), Long.class, 
                numero, 
                StringUtils.defaultIfEmpty(particella, null), 
                StringUtils.defaultIfEmpty(particella, null), 
                id, 
                fData, fData);
            return l > 0;
        } catch (DataAccessException e) {
            _log.error("Errore nella determinazione dell'esistenza del numero ddt {}, particella {}", numero, StringUtils.defaultIfBlank(particella, "<vuoto>"), e);
            throw new SQLException(e);
        }
    }

    public void update(DdtDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("DDT_U01"), 
                dto.getNumDocumento(), 
                StringUtils.defaultIfEmpty(dto.getParticella(), null), 
                formatDate(dto.getDataDocumento()), 
                dto.getIdListino(), 
                dto.getIdAgente(), 
                dto.getIdProgetto(), 
                dto.getIdCausaleTrasporto(), 
                dto.getDataOraTrasporto(), 
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
                dto.getCodiceUfficioDestinazione(), 
                dto.getAcconto(), 
                dto.getIdMagazzino(), 
                dto.getUserLastUpdate(), 
                dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'aggiornamento del ddt {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }
}
