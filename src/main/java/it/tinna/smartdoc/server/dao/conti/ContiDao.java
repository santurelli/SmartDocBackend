package it.tinna.smartdoc.server.dao.conti;

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
import it.tinna.smartdoc.shared.dto.conti.ContoDto;

public class ContiDao extends BaseDao
{

    public ContiDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void delete(long idConto,
                       long idUtente) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("PIANOCONTI_D01"), idUtente, idConto);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'eliminazione del conto {}", idConto, e);
            throw new SQLException(e);
        }
    }

    public ContoDto getByDescrizione(String descrizione,
                                     Long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ContoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ContoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("PIANOCONTI_S06"), rowMapper, descrizione, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del conto con descrizione {}", descrizione, e);
            throw new SQLException(e);
        }
    }

    public ContoDto getById(long id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ContoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ContoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("PIANOCONTI_S03"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessun conto trovato con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del conto con id {}", id, e);
            throw new SQLException(e);
        }
    }

    public List<ContoDto> getList(String strToSearch,
                                  Integer length,
                                  Integer start,
                                  Integer orderColumn,
                                  String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("PIANOCONTI_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(strToSearch) ? null : StringUtility.formatForLike(strToSearch));
        Map<String, String> valuesMap = new HashMap<>();
        if ( orderColumn == 0 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("descrizione ").append(orderDir).toString());
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
            BeanPropertyRowMapper<ContoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ContoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco causali di trasporto", e);
            throw new SQLException(e);
        }
    }

    public List<ContoDto> getListForCombo() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ContoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ContoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("PIANOCONTI_S04"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco conti per il popolamento della combo", e);
            throw new SQLException(e);
        }
    }

    // public List<ContoDto> getContiFratelli(Integer id) throws Exception
    // {
    // QueryRunner qRunner = new QueryRunner();
    // return qRunner.query(conn, FileQueryReader.getQuery("PIANOCONTI_S01"), new TrimmedBeanListHandler<ContoDto>(ContoDto.class), new Object[]
    // { id });
    // }
    //
    // public List<ContoDto> getContiForTree(String text) throws Exception
    // {
    // QueryRunner qRunner = new QueryRunner();
    // return qRunner.query(conn, FileQueryReader.getQuery("PIANOCONTI_S08"), new TrimmedBeanListHandler<ContoDto>(ContoDto.class), new Object[]
    // { StringUtils.isEmpty(text) ? null : StringUtility.formatForLike(text.toLowerCase()),
    // StringUtils.isEmpty(text) ? null : StringUtility.formatForLike(text.toLowerCase()) });
    // }
    //
    // public List<ContoDto> getContiRadice() throws Exception
    // {
    // QueryRunner qRunner = new QueryRunner();
    // return qRunner.query(conn, FileQueryReader.getQuery("PIANOCONTI_S02"), new TrimmedBeanListHandler<ContoDto>(ContoDto.class), new Object[]
    // {});
    // }
    //
    // public List<ItemSuggestion> getSuggestionCodiceDescrizione(String query) throws Exception
    // {
    // QueryRunner qRunner = new QueryRunner();
    // return qRunner.query(conn, FileQueryReader.getQuery("PIANOCONTI_S05"), new TrimmedBeanListHandler<ItemSuggestion>(ItemSuggestion.class), new Object[]
    // { StringUtility.formatForLike(query.toLowerCase()),
    // StringUtility.formatForLike(query.toLowerCase()) });
    // }

    public long insert(ContoDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("PIANOCONTI_I01"), Long.class, dto.getDescrizione(), dto.getPredefinito(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento del conto", e);
            throw new SQLException(e);
        }
    }

    // /**
    // * Dato l'id del nodo padre ritorna true se qualcuno dei figli ha lo stesso codice
    // *
    // * @param ancestorId
    // * id nodo padre
    // * @param codice
    // * codice da controllare
    // * @return true se qualcuno dei figli diretti di ancestorId ha codice uguale a quello passato
    // * @throws Exception
    // */
    // public boolean isExistentByAncestor(Integer ancestorId,
    // String codice) throws Exception
    // {
    // QueryRunner qRunner = new QueryRunner();
    // Long num = qRunner.query(conn, FileQueryReader.getQuery("PIANOCONTI_S07"), new ScalarHandler<Long>(), new Object[]
    // { ancestorId,
    // codice.toLowerCase() });
    // return num.longValue() > 0;
    // }
    //
    // public boolean isExistentRoot(String codice) throws Exception
    // {
    // QueryRunner qRunner = new QueryRunner();
    // Long num = qRunner.query(conn, FileQueryReader.getQuery("PIANOCONTI_S09"), new ScalarHandler<Long>(), new Object[]
    // { codice.toLowerCase() });
    // return num.longValue() > 0;
    // }
    //
    // public List<ContoDto> searchConti(String descrizione,
    // String codice,
    // Integer idPadre) throws Exception
    // {
    // QueryRunner qRunner = new QueryRunner();
    // return qRunner.query(conn, FileQueryReader.getQuery("PIANOCONTI_S04"), new TrimmedBeanListHandler<ContoDto>(ContoDto.class), new Object[]
    // { StringUtils.isEmpty(descrizione) ? null : StringUtility.formatForLike(descrizione),
    // StringUtils.isEmpty(codice) ? null : StringUtility.formatForLike(codice),
    // idPadre });
    // }

    public void resetPredefinite(long idUser) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("PIANOCONTI_U02"), idUser);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel reset del conto predefinito", e);
            throw new SQLException(e);
        }
    }

    public void update(ContoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("PIANOCONTI_U01"), dto.getDescrizione(), dto.getPredefinito(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore durante l'aggiornamento del conto {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }
}

