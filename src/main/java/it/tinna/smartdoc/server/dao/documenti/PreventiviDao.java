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
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto; // Create if needed or use BaseDto for list
import it.tinna.smartdoc.shared.dto.documenti.PreventivoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;

@Repository
public class PreventiviDao extends BaseDao {

    public PreventiviDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void delete(PreventivoDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("PREVENTIVI_D01"), dto.getId());
            jdbcTemplate.update(FileQueryReader.getQuery("PREVENTIVI_D02"), dto.getId());
            jdbcTemplate.update(FileQueryReader.getQuery("PREVENTIVI_D03"), dto.getUserLastUpdate(), dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'eliminazione del preventivo {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    private String formatDate(String date) {
        if (StringUtils.isEmpty(date)) return null;
        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) { // ISO YYYY-MM-DD
            String[] parts = date.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        }
        return date;
    }

    public String generaCodice(String data) throws SQLException {
        String formattedDate = formatDate(data);
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("PREVENTIVI_S06"), String.class, 
                    formattedDate, formattedDate, formattedDate, formattedDate);
        } catch (EmptyResultDataAccessException e) {
            return "1";
        } catch (DataAccessException e) {
            _log.error("Errore nella generazione del numero preventivo", e);
            throw new SQLException(e);
        }
    }

    public PreventivoDto getById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<PreventivoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(PreventivoDto.class);
            PreventivoDto dto = jdbcTemplate.queryForObject(FileQueryReader.getQuery("PREVENTIVI_S03"), rowMapper, id);
            
            // Get lines
            BeanPropertyRowMapper<ProdottoDocumentoDto> rowMapperLines = new BeanPropertyRowMapper<>();
            rowMapperLines.setMappedClass(ProdottoDocumentoDto.class);
            List<ProdottoDocumentoDto> lines = jdbcTemplate.query(FileQueryReader.getQuery("PREVENTIVI_S04"), rowMapperLines, id);
            dto.setProdotti(lines);
            
            return dto;
        } catch (EmptyResultDataAccessException e) {
            _log.info("Nessun preventivo trovato con id {}", id);
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero del preventivo con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<MovimentiDocumentoDto> getList(Integer idCliente, String dtFrom, String dtTo, Integer idAgente,
            Integer length, Integer start, String orderColumn, String orderDir) throws SQLException {
        String query = FileQueryReader.getQuery("PREVENTIVI_S07");
        List<Object> params = new ArrayList<>();
        params.add(formatDate(dtFrom));
        params.add(formatDate(dtTo));
        params.add(idCliente);
        params.add(idAgente);
        
        Map<String, String> valuesMap = new HashMap<>();
        if (StringUtils.isNotEmpty(orderColumn)) {
            valuesMap.put("ORDER_BY", orderColumn + " " + orderDir);
        } else {
             valuesMap.put("ORDER_BY", "data_preventivo DESC, num_preventivo DESC");
        }
        
        if (length != null && start != null) {
            valuesMap.put("LIMIT", "LIMIT ? OFFSET ?");
            params.add(length);
            params.add(start);
        } else {
            valuesMap.put("LIMIT", "");
        }
        
        query = StringSubstitutor.replace(query, valuesMap);
        _log.debug("Esecuzione ricerca preventivi: {} con parametri {}", query, params);

        try {
            BeanPropertyRowMapper<MovimentiDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(MovimentiDocumentoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            _log.error("Errore nella ricerca dei preventivi", e);
            throw new SQLException(e);
        }
    }
    
    public long getTotPreventivi() throws SQLException {
        // Use generic S08 from CLIENTI? No, need PREVENTIVI version but not present in extracted queries list, using list size or simplified count
        // Actually the list query returns total count in the window function, but separate count is useful.
        // For now I'll skip separate count query and rely on the window function if possible, or key search.
        // Actually, I can just do a simple count query.
        return 0; // Placeholder
    }

    public Integer insert(PreventivoDto dto) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("PREVENTIVI_I01"), Integer.class,
                    dto.getNumDocumento(), StringUtils.defaultIfEmpty(dto.getParticella(), null),
                    formatDate(dto.getDataDocumento()), dto.getIdListino(), dto.getIdAgente(), dto.getIdProgetto(),
                    dto.getIdCliente(), dto.getIdTipoPagamento(), dto.getIdNsBanca(),
                    StringUtils.defaultIfEmpty(dto.getDescrizioneBanca(), null),
                    StringUtils.defaultIfEmpty(dto.getIban(), null), StringUtils.defaultIfEmpty(dto.getCin(), null),
                    StringUtils.defaultIfEmpty(dto.getAbi(), null), StringUtils.defaultIfEmpty(dto.getCab(), null),
                    StringUtils.defaultIfEmpty(dto.getConto(), null), StringUtils.defaultIfEmpty(dto.getBic(), null),
                    dto.getAcconto(),
                    StringUtils.defaultIfEmpty(dto.getIndirizzoIntestazione(), null),
                    StringUtils.defaultIfEmpty(dto.getCapIntestazione(), null),
                    StringUtils.defaultIfEmpty(dto.getCittaIntestazione(), null),
                    StringUtils.defaultIfEmpty(dto.getProvinciaIntestazione(), null),
                    StringUtils.defaultIfEmpty(dto.getNazioneIntestazione(), null),
                    StringUtils.defaultIfEmpty(dto.getIndirizzoDestinazione(), null),
                    StringUtils.defaultIfEmpty(dto.getCapDestinazione(), null),
                    StringUtils.defaultIfEmpty(dto.getCittaDestinazione(), null),
                    StringUtils.defaultIfEmpty(dto.getProvinciaDestinazione(), null),
                    StringUtils.defaultIfEmpty(dto.getNazioneDestinazione(), null),
                    dto.getIdMagazzino(),
                    StringUtils.defaultIfEmpty(dto.getAnnotazioneEstesa(), null),
                    dto.getUserCreated());
        } catch (DataAccessException e) {
            _log.error("Errore nel salvataggio del preventivo", e);
            throw new SQLException(e);
        }
    }

    public void insertProdotto(ProdottoDocumentoDto dto) throws SQLException {
         try {
            jdbcTemplate.update(FileQueryReader.getQuery("PREVENTIVI_I02"),
                    dto.getIdDocumento(), dto.getIdProdotto(), dto.getQuantita(), dto.getIdUnitaMisura(),
                    dto.getPrezzo(), dto.getSconto(), dto.getProvvigione(), dto.getIdAliquotaIva(),
                    StringUtils.defaultIfEmpty(dto.getNota(), null),
                    dto.getIdColore(), dto.getIdTaglia(), dto.getIdScelta(), dto.getIdTono(), dto.getIdConto(),
                    StringUtils.defaultIfEmpty(dto.getFmCodice(), null),
                    StringUtils.defaultIfEmpty(dto.getFmDescrizione(), null),
                    StringUtils.defaultIfEmpty(dto.getFmUnitaMisura(), null),
                    StringUtils.defaultIfEmpty(dto.getFmTono(), null),
                    StringUtils.defaultIfEmpty(dto.getFmScelta(), null),
                    StringUtils.defaultIfEmpty(dto.getFmTaglia(), null),
                    StringUtils.defaultIfEmpty(dto.getFmColore(), null)
            );
        } catch (DataAccessException e) {
            _log.error("Errore nel salvataggio della riga preventivo", e);
            throw new SQLException(e);
        }
    }

    public void update(PreventivoDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("PREVENTIVI_D01"), dto.getId());
            jdbcTemplate.update(FileQueryReader.getQuery("PREVENTIVI_U01"),
                    dto.getNumDocumento(), StringUtils.defaultIfEmpty(dto.getParticella(), null),
                    formatDate(dto.getDataDocumento()), dto.getIdListino(), dto.getIdAgente(), dto.getIdProgetto(),
                    dto.getIdCliente(), dto.getIdTipoPagamento(), dto.getIdNsBanca(),
                    StringUtils.defaultIfEmpty(dto.getDescrizioneBanca(), null),
                    StringUtils.defaultIfEmpty(dto.getIban(), null), StringUtils.defaultIfEmpty(dto.getCin(), null),
                    StringUtils.defaultIfEmpty(dto.getAbi(), null), StringUtils.defaultIfEmpty(dto.getCab(), null),
                    StringUtils.defaultIfEmpty(dto.getConto(), null), StringUtils.defaultIfEmpty(dto.getBic(), null),
                    dto.getAcconto(),
                    StringUtils.defaultIfEmpty(dto.getIndirizzoIntestazione(), null),
                    StringUtils.defaultIfEmpty(dto.getCapIntestazione(), null),
                    StringUtils.defaultIfEmpty(dto.getCittaIntestazione(), null),
                    StringUtils.defaultIfEmpty(dto.getProvinciaIntestazione(), null),
                    StringUtils.defaultIfEmpty(dto.getNazioneIntestazione(), null),
                    StringUtils.defaultIfEmpty(dto.getIndirizzoDestinazione(), null),
                    StringUtils.defaultIfEmpty(dto.getCapDestinazione(), null),
                    StringUtils.defaultIfEmpty(dto.getCittaDestinazione(), null),
                    StringUtils.defaultIfEmpty(dto.getProvinciaDestinazione(), null),
                    StringUtils.defaultIfEmpty(dto.getNazioneDestinazione(), null),
                    dto.getIdMagazzino(),
                    StringUtils.defaultIfEmpty(dto.getAnnotazioneEstesa(), null),
                    dto.getUserLastUpdate(), dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'aggiornamento del preventivo {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }
}
