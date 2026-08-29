package it.tinna.smartdoc.server.dao.fornitori;

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
// import it.tinna.smartdoc.server.service.clienti.BaseClienteFornitoreJsonDto;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;

public class FornitoriDao extends BaseDao
{

    public FornitoriDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(FornitoreDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FORNITORI_D01"), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'eliminazione del fornitore {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public String generaCodice() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FORNITORI_S03"), String.class);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella generazione del codice fornitore", e);
            throw new SQLException(e);
        }
    }

    public FornitoreDto getByDenominazione(String denominazione) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<FornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(FornitoreDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FORNITORI_S07"), rowMapper, denominazione);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.info("Nessun fornitore trovato con denominazione {}", denominazione);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del fornitore con denominazione {}", denominazione, e);
            throw new SQLException(e);
        }
    }

    public FornitoreDto getById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<FornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(FornitoreDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FORNITORI_S04"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.info("Nessun fornitore trovato con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del fornitore con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public FornitoreDto getByPartitaIva(String partitaIva) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<FornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(FornitoreDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FORNITORI_S10"), rowMapper, partitaIva);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.info("Nessun fornitore trovato con partita iva {}", partitaIva);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del fornitore con partita iva {}", partitaIva, e);
            throw new SQLException(e);
        }
    }

    /**
     * Come getByPartitaIva ma tollerante a differenze di formattazione (spazi, prefisso IT,
     * maiuscole/minuscole): confronta solo cifre/lettere. Usato dall'importazione AI, dove la
     * Partita IVA estratta da un documento potrebbe non essere formattata esattamente come quella
     * salvata in anagrafica.
     */
    public FornitoreDto getByPartitaIvaNormalizzata(String partitaIvaNormalizzata) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<FornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(FornitoreDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FORNITORI_S11"), rowMapper, partitaIvaNormalizzata);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del fornitore con partita iva normalizzata {}", partitaIvaNormalizzata, e);
            throw new SQLException(e);
        }
    }

    /**
     * Fallback per l'importazione AI quando la Partita IVA non e' leggibile/estratta dal documento:
     * prova a trovare il fornitore per denominazione esatta (case/spazi insensitive).
     */
    public FornitoreDto getByDenominazioneEsatta(String denominazione) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<FornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(FornitoreDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FORNITORI_S12"), rowMapper, denominazione);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del fornitore per denominazione {}", denominazione, e);
            throw new SQLException(e);
        }
    }

    public List<FornitoreDto> getList(String strToSearch,
                                      Integer length,
                                      Integer start,
                                      Integer orderColumn,
                                      String orderDir) throws SQLException
    {
        BeanPropertyRowMapper<FornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
        rowMapper.setMappedClass(FornitoreDto.class);
        String query = FileQueryReader.getQuery("FORNITORI_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        Map<String, String> valuesMap = new HashMap<>();
        if ( orderColumn == 0 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("codice ").append(orderDir).toString());
        }
        else if ( orderColumn == 1 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("denominazione ").append(orderDir).toString());
        }
        /*
         * else if (orderColumn == 2) { valuesMap.put("ORDER_BY", new StringBuilder("COALESCE(d_e_indirizzi_fornitori.citta, '') ").append( orderDir).toString()); }
         */ else if ( orderColumn == 3 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("COALESCE(d_e_clienti.referente, '') ").append(orderDir).toString());
        }
        else if ( orderColumn == 4 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("COALESCE(partita_iva || '/' || codice_fiscale, partita_iva, codice_fiscale, '') ").append(orderDir).toString());
        }
        else
        {
            valuesMap.put("ORDER_BY", new StringBuilder("denominazione ").append(orderDir).toString());
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
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco fornitori", e);
            throw new SQLException(e);
        }
    }

    public List<FornitoreDto> getListForCombo() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<FornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(FornitoreDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FORNITORI_S09"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco fornitori per il popolamento delle combo", e);
            throw new SQLException(e);
        }
    }

    public List<FornitoreDto> getSuggestion(String query) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<FornitoreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(FornitoreDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FORNITORI_S06"), rowMapper, StringUtils.defaultIfEmpty(StringUtility.formatForLike(query.toLowerCase()), null), StringUtils.defaultIfEmpty(StringUtility.formatForLike(query.toLowerCase()), null), StringUtils.defaultIfEmpty(StringUtility.formatForLike(query.toLowerCase()), null));
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei suggerimenti per i fornitori", e);
            throw new SQLException(e);
        }
    }

    public long getTotFornitori() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FORNITORI_S08"), Long.class);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del totale fornitori", e);
            throw new SQLException(e);
        }
    }

    public long insert(FornitoreDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FORNITORI_I01"), Long.class, StringUtils.defaultIfEmpty(dto.getCodice(), null), StringUtils.defaultIfEmpty(dto.getDenominazione(), null), StringUtils.defaultIfEmpty(dto.getReferente(), null), StringUtils.defaultIfEmpty(dto.getCodiceFiscale(), null), StringUtils.defaultIfEmpty(dto.getPartitaIva(), null), StringUtils.defaultIfEmpty(dto.getNote(), null), dto.getIdTipoPagamento(), StringUtils.defaultIfEmpty(dto.getCodSia(), null), StringUtils.defaultIfEmpty(dto.getDescrizioneBanca(), null), StringUtils.defaultIfEmpty(dto.getIban(), null), StringUtils.defaultIfEmpty(dto.getCin(), null), StringUtils.defaultIfEmpty(dto.getAbi(), null), StringUtils.defaultIfEmpty(dto.getCab(), null), StringUtils.defaultIfEmpty(dto.getConto(), null), StringUtils.defaultIfEmpty(dto.getBic(), null), dto.getIdRisorsa(), dto.getIdAliquotaIva(), dto.getIdCategoriaSpesa(), dto.getIdVettore(), dto.getIdTipoPorto(), dto.getDocumentiMail(), dto.getIdAvviso(), dto.getIdNota(), dto.getIdContoContabile(), dto.getFlRitenutaAcconto() != null ? dto.getFlRitenutaAcconto() : 0, StringUtils.defaultIfEmpty(dto.getTipoRitenuta(), "PERSONE_FISICHE"), dto.getPercRitenutaAcconto() != null ? dto.getPercRitenutaAcconto() : java.math.BigDecimal.valueOf(20.00), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento del fornitore", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistentCodice(String codice,
                                    Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("FORNITORI_S02"), Long.class, codice, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella verifica dell'esistenza del codice fornitore con codice {} e id {}", codice, id == null ? "<nullo>" : id, e);
            throw new SQLException(e);
        }
    }

    public boolean isExistentDenominazione(String denominazione,
                                           Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("FORNITORI_S05"), Long.class, denominazione, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella verifica dell'esistenza del codice fornitore con denominazione {} e id {}", denominazione, id == null ? "<nullo>" : id, e);
            throw new SQLException(e);
        }
    }

    public void update(FornitoreDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FORNITORI_U01"), StringUtils.defaultIfEmpty(dto.getCodice(), null), StringUtils.defaultIfEmpty(dto.getDenominazione(), null), StringUtils.defaultIfEmpty(dto.getReferente(), null), StringUtils.defaultIfEmpty(dto.getCodiceFiscale(), null), StringUtils.defaultIfEmpty(dto.getPartitaIva(), null), StringUtils.defaultIfEmpty(dto.getNote(), null), dto.getIdTipoPagamento(), StringUtils.defaultIfEmpty(dto.getCodSia(), null), StringUtils.defaultIfEmpty(dto.getDescrizioneBanca(), null), StringUtils.defaultIfEmpty(dto.getIban(), null), StringUtils.defaultIfEmpty(dto.getCin(), null), StringUtils.defaultIfEmpty(dto.getAbi(), null), StringUtils.defaultIfEmpty(dto.getCab(), null), StringUtils.defaultIfEmpty(dto.getConto(), null), StringUtils.defaultIfEmpty(dto.getBic(), null), dto.getIdRisorsa(), dto.getIdAliquotaIva(), dto.getIdCategoriaSpesa(), dto.getIdVettore(), dto.getIdTipoPorto(), dto.getDocumentiMail(), dto.getIdAvviso(), dto.getIdNota(), dto.getIdContoContabile(), dto.getFlRitenutaAcconto() != null ? dto.getFlRitenutaAcconto() : 0, StringUtils.defaultIfEmpty(dto.getTipoRitenuta(), "PERSONE_FISICHE"), dto.getPercRitenutaAcconto() != null ? dto.getPercRitenutaAcconto() : java.math.BigDecimal.valueOf(20.00), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento del fornitore {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

