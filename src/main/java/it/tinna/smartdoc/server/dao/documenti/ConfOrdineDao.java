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
import it.tinna.smartdoc.shared.dto.documenti.ConfOrdineDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;

@Repository
public class ConfOrdineDao extends BaseDao {

    public ConfOrdineDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void delete(ConfOrdineDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CONFORDINE_D01"), dto.getId());
            jdbcTemplate.update(FileQueryReader.getQuery("CONFORDINE_D02"), dto.getUserLastUpdate(), dto.getId());
            jdbcTemplate.update(FileQueryReader.getQuery("CONFORDINE_D03"), dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'eliminazione della conferma d'ordine {}", dto.getId(), e);
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
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CONFORDINE_S03"), String.class, 
                    formattedDate, formattedDate, formattedDate, formattedDate);
        } catch (EmptyResultDataAccessException e) {
            return "1";
        } catch (DataAccessException e) {
            _log.error("Errore nella generazione del numero conferma d'ordine", e);
            throw new SQLException(e);
        }
    }

    public ConfOrdineDto getById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<ConfOrdineDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ConfOrdineDto.class);
            ConfOrdineDto dto = jdbcTemplate.queryForObject(FileQueryReader.getQuery("CONFORDINE_S01"), rowMapper, id);
            
            // Get lines
            BeanPropertyRowMapper<ProdottoDocumentoDto> rowMapperLines = new BeanPropertyRowMapper<>();
            rowMapperLines.setMappedClass(ProdottoDocumentoDto.class);
            List<ProdottoDocumentoDto> lines = jdbcTemplate.query(FileQueryReader.getQuery("CONFORDINE_S02"), rowMapperLines, id);
            dto.setProdotti(lines);
            
            return dto;
        } catch (EmptyResultDataAccessException e) {
            _log.info("Nessuna conferma d'ordine trovata con id {}", id);
            return null;
        } catch (DataAccessException e) {
            _log.error("Errore nel recupero della conferma d'ordine con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<MovimentiDocumentoDto> getList(Integer idCliente, String dtFrom, String dtTo, Integer idAgente,
            Integer length, Integer start, String orderColumn, String orderDir) throws SQLException {
        String query = FileQueryReader.getQuery("CONFORDINE_S07");
        List<Object> params = new ArrayList<>();
        params.add(formatDate(dtFrom));
        params.add(formatDate(dtTo));
        params.add(idCliente);
        params.add(idAgente);
        
        Map<String, String> valuesMap = new HashMap<>();
        if (StringUtils.isNotEmpty(orderColumn)) {
            valuesMap.put("ORDER_BY", orderColumn + " " + orderDir);
        } else {
             valuesMap.put("ORDER_BY", "data_confordine DESC, num_confordine DESC");
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
            _log.error("Errore nella ricerca delle conferme d'ordine", e);
            throw new SQLException(e);
        }
    }

    public Integer insert(ConfOrdineDto dto) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CONFORDINE_I01"), Integer.class,
                    dto.getNumDocumento(), StringUtils.defaultIfEmpty(dto.getParticella(), null),
                    formatDate(dto.getDataDocumento()), dto.getIdListino(), dto.getIdAgente(),
                    dto.getIdCausaleTrasporto(), dto.getDataOraTrasporto(), dto.getTarga(),
                    dto.getIdTipoPorto(), dto.getIdVettore(), dto.getIdAspettoBeni(),
                    dto.getColli(), dto.getPallet(), dto.getPesoNetto(), dto.getPesoLordo(),
                    dto.getIdCliente(), dto.getIdTipoPagamento(), dto.getIdNsBanca(),
                    StringUtils.defaultIfEmpty(dto.getDescrizioneBanca(), null),
                    StringUtils.defaultIfEmpty(dto.getIban(), null), StringUtils.defaultIfEmpty(dto.getCin(), null),
                    StringUtils.defaultIfEmpty(dto.getAbi(), null), StringUtils.defaultIfEmpty(dto.getCab(), null),
                    StringUtils.defaultIfEmpty(dto.getConto(), null), StringUtils.defaultIfEmpty(dto.getBic(), null),
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
                    dto.getAcconto(), dto.getIdMagazzino(),
                    dto.getAnnotazioneEstesa(),
                    dto.getIdDocAssociato(), dto.getTipoDocAssociato(),
                    dto.getUserCreated());
        } catch (DataAccessException e) {
            _log.error("Errore nel salvataggio della conferma d'ordine", e);
            throw new SQLException(e);
        }
    }

    public void insertProdotto(ProdottoDocumentoDto dto) throws SQLException {
         try {
            jdbcTemplate.update(FileQueryReader.getQuery("CONFORDINE_I02"),
                    dto.getIdDocumento(), dto.getIdProdotto(), dto.getQuantita(), dto.getIdUnitaMisura(),
                    dto.getPrezzo(), dto.getSconto(), dto.getProvvigione(), dto.getIdAliquotaIva(),
                    dto.getScarica(), StringUtils.defaultIfEmpty(dto.getNota(), null),
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
            _log.error("Errore nel salvataggio della riga conferma d'ordine", e);
            throw new SQLException(e);
        }
    }

    public void update(ConfOrdineDto dto) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("CONFORDINE_D01"), dto.getId());
            jdbcTemplate.update(FileQueryReader.getQuery("CONFORDINE_U01"),
                    dto.getNumDocumento(), StringUtils.defaultIfEmpty(dto.getParticella(), null),
                    formatDate(dto.getDataDocumento()), dto.getIdListino(), dto.getIdAgente(),
                    dto.getIdCausaleTrasporto(), dto.getDataOraTrasporto(), dto.getTarga(),
                    dto.getIdTipoPorto(), dto.getIdVettore(), dto.getIdAspettoBeni(),
                    dto.getColli(), dto.getPallet(), dto.getPesoNetto(), dto.getPesoLordo(),
                    dto.getIdCliente(), dto.getIdTipoPagamento(), dto.getIdNsBanca(),
                    StringUtils.defaultIfEmpty(dto.getDescrizioneBanca(), null),
                    StringUtils.defaultIfEmpty(dto.getIban(), null), StringUtils.defaultIfEmpty(dto.getCin(), null),
                    StringUtils.defaultIfEmpty(dto.getAbi(), null), StringUtils.defaultIfEmpty(dto.getCab(), null),
                    StringUtils.defaultIfEmpty(dto.getConto(), null), StringUtils.defaultIfEmpty(dto.getBic(), null),
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
                    dto.getAcconto(), dto.getIdMagazzino(),
                    dto.getAnnotazioneEstesa(),
                    dto.getUserLastUpdate(), dto.getId());
        } catch (DataAccessException e) {
            _log.error("Errore nell'aggiornamento della conferma d'ordine {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }
    public void insertDocumentoCollegato(Integer idDocPadre, String tipoDocPadre, Integer idDocFiglio, String tipoDocFiglio) throws SQLException {
        try {
            jdbcTemplate.update(FileQueryReader.getQuery("DOCUMENTI_I01"), idDocPadre, tipoDocPadre, idDocFiglio, tipoDocFiglio);
        } catch (DataAccessException e) {
            _log.error("Errore nel salvataggio del collegamento documento {} -> {}", idDocPadre, idDocFiglio, e);
            throw new SQLException(e);
        }
    }

    public boolean existsDocumentoCollegato(Integer idDocPadre, String tipoDocPadre, Integer idDocFiglio, String tipoDocFiglio) throws SQLException {
        try {
            Integer count = jdbcTemplate.queryForObject(FileQueryReader.getQuery("DOCUMENTI_S10"), Integer.class, idDocPadre, tipoDocPadre, idDocFiglio, tipoDocFiglio);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            _log.error("Errore nella verifica del collegamento documento {} -> {}", idDocPadre, idDocFiglio, e);
            throw new SQLException(e);
        }
    }
}

