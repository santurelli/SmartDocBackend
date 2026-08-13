package it.tinna.smartdoc.server.dao.clienti;

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
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;

public class ClientiDao extends BaseDao
{

    public ClientiDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(ClienteDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("CLIENTI_D01"), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'eliminazione del cliente {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public String generaCodice() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CLIENTI_S03"), String.class);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella generazionedel codice cliente", e);
            throw new SQLException(e);
        }
    }

    public ClienteDto getByDenominazione(String denominazione) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ClienteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ClienteDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CLIENTI_S07"), rowMapper, denominazione);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.info("Nessun cliente trovato con denominazione {}", denominazione);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del cliente con denominazione {}", denominazione, e);
            throw new SQLException(e);
        }
    }

    public ClienteDto getByPartitaIva(String partitaIva) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ClienteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ClienteDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CLIENTI_S10"), rowMapper, partitaIva);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.info("Nessun cliente trovato con partita IVA {}", partitaIva);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del cliente con partita IVA {}", partitaIva, e);
            throw new SQLException(e);
        }
    }

    public ClienteDto getByCodiceFiscale(String codiceFiscale) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ClienteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ClienteDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CLIENTI_S11"), rowMapper, codiceFiscale);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.info("Nessun cliente trovato con codice fiscale {}", codiceFiscale);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del cliente con codice fiscale {}", codiceFiscale, e);
            throw new SQLException(e);
        }
    }

    public ClienteDto getById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ClienteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ClienteDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CLIENTI_S04"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.info("Nessun cliente trovato con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del cliente con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<ClienteDto> getList(String strToSearch,
                                    Integer length,
                                    Integer start,
                                    Integer orderColumn,
                                    String orderDir) throws SQLException
    {
        return getList(strToSearch, length, start, orderColumn, orderDir, false);
    }

    public List<ClienteDto> getList(String strToSearch,
                                    Integer length,
                                    Integer start,
                                    Integer orderColumn,
                                    String orderDir,
                                    boolean light) throws SQLException
    {
        String query = FileQueryReader.getQuery(light ? "CLIENTI_S01_LIGHT" : "CLIENTI_S01");
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
        else if ( orderColumn == 2 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("COALESCE(d_e_indirizzi_clienti.citta, '') ").append(orderDir).toString());
        }
        else if ( orderColumn == 3 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("COALESCE(d_e_clienti.referente, '') ").append(orderDir).toString());
        }
        else if ( orderColumn == 4 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("COALESCE(partita_iva || '/' || codice_fiscale, partita_iva, codice_fiscale, '') ").append(orderDir).toString());
        }
        else if ( orderColumn == 5 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder(" COALESCE(d_e_agenti.denominazione, '') ").append(orderDir).toString());
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
            BeanPropertyRowMapper<ClienteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ClienteDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella ricerca dei clienti", e);
            throw new SQLException(e);
        }
    }

    public List<ClienteDto> getListForCombo() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ClienteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ClienteDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("CLIENTI_S09"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco clienti per il popolamento delle combo", e);
            throw new SQLException(e);
        }
    }

    public List<ClienteDto> getSuggestion(String query) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ClienteDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ClienteDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("CLIENTI_S06"), rowMapper, StringUtility.formatForLike(query.toLowerCase()), StringUtility.formatForLike(query.toLowerCase()), StringUtility.formatForLike(query.toLowerCase()));
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei suggerimenti per i clienti", e);
            throw new SQLException(e);
        }
    }

    public long getTotClienti() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CLIENTI_S08"), Long.class);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del totale clienti", e);
            throw new SQLException(e);
        }
    }

    public Integer insert(ClienteDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("CLIENTI_I01"), Integer.class, StringUtils.defaultIfEmpty(dto.getCodice(), null), dto.getTipologia().name(), StringUtils.defaultIfEmpty(dto.getDenominazione(), null), StringUtils.defaultIfEmpty(dto.getReferente(), null), StringUtils.defaultIfEmpty(dto.getCodiceFiscale(), null), StringUtils.defaultIfEmpty(dto.getPartitaIva(), null), StringUtils.defaultIfEmpty(dto.getNote(), null), dto.getIdTipoPagamento(), StringUtils.defaultIfEmpty(dto.getCodSia(), null), StringUtils.defaultIfEmpty(dto.getDescrizioneBanca(), null), StringUtils.defaultIfEmpty(dto.getIban(), null), StringUtils.defaultIfEmpty(dto.getCin(), null), StringUtils.defaultIfEmpty(dto.getAbi(), null), StringUtils.defaultIfEmpty(dto.getCab(), null), StringUtils.defaultIfEmpty(dto.getConto(), null), StringUtils.defaultIfEmpty(dto.getBic(), null), dto.getIdRisorsa(), dto.getIdAliquotaIva(), dto.getIdVettore(), dto.getIdTipoPorto(), dto.getDocumentiMail(), dto.getIdAvviso(), dto.getIdNota(), dto.getIdAgente(), dto.getIdListino(), dto.getSconto(), dto.getIdZonaCompetenza(), dto.getIdSottoconto(), dto.getIdLingua(), dto.getIdContoContabile(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel salvataggio del cliente", e);
            throw new SQLException(e);
        }
    }

    public boolean isExistentCodice(String codice,
                                    Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("CLIENTI_S02"), Long.class, codice, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione dell'esistenza del cliente con codice {} e id {}", codice, id == null ? "<nullo>" : id, e);
            throw new SQLException(e);
        }
    }

    public boolean isExistentDenominazione(String denominazione,
                                           Integer id) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("CLIENTI_S05"), Long.class, denominazione, id);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione dell'esistenza del cliente con denominazione {} e id {}", denominazione, id == null ? "<nullo>" : id, e);
            throw new SQLException(e);
        }
    }

    public void update(ClienteDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("CLIENTI_U01"), StringUtils.defaultIfEmpty(dto.getCodice(), null), dto.getTipologia().name(), StringUtils.defaultIfEmpty(dto.getDenominazione(), null), StringUtils.defaultIfEmpty(dto.getReferente(), null), StringUtils.defaultIfEmpty(dto.getCodiceFiscale(), null), StringUtils.defaultIfEmpty(dto.getPartitaIva(), null), StringUtils.defaultIfEmpty(dto.getNote(), null), dto.getIdTipoPagamento(), StringUtils.defaultIfEmpty(dto.getCodSia(), null), StringUtils.defaultIfEmpty(dto.getDescrizioneBanca(), null), StringUtils.defaultIfEmpty(dto.getIban(), null), StringUtils.defaultIfEmpty(dto.getCin(), null), StringUtils.defaultIfEmpty(dto.getAbi(), null), StringUtils.defaultIfEmpty(dto.getCab(), null), StringUtils.defaultIfEmpty(dto.getConto(), null), StringUtils.defaultIfEmpty(dto.getBic(), null), dto.getIdRisorsa(), dto.getIdAliquotaIva(), dto.getIdVettore(), dto.getIdTipoPorto(), dto.getDocumentiMail(), dto.getIdAvviso(), dto.getIdNota(), dto.getIdAgente(), dto.getIdListino(), dto.getSconto(), dto.getIdZonaCompetenza(), dto.getIdSottoconto(), dto.getIdLingua(), dto.getIdContoContabile(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento del cliente {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

}

