package it.tinna.smartdoc.server.dao.codicibarre;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.codicibarre.CodiceBarreDto;

public class CodiciBarreDao extends BaseDao
{

    public CodiciBarreDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void deleteByIdProdotto(long idProdotto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("CODICIBARRE_D01"), idProdotto);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione dei codice a barre per l'articolo {}", idProdotto, e);
            throw new SQLException(e);
        }
    }

    public List<CodiceBarreDto> getList(String codice) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<CodiceBarreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(CodiceBarreDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("CODICIBARRE_S01"), rowMapper, StringUtils.isEmpty(codice) ? null : StringUtility.formatForLike(codice));
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei codici a barre con codice {}", codice);
            throw new SQLException(e);
        }
    }

    public List<CodiceBarreDto> getListByIdProdotto(long idProdotto) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<CodiceBarreDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(CodiceBarreDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("CODICIBARRE_S02"), rowMapper, idProdotto);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei codici a barre del prodotto {}", idProdotto);
            throw new SQLException(e);
        }
    }

    public void insert(CodiceBarreDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("CODICIBARRE_I01"), dto.getCodice(), dto.getIdProdotto(), dto.getIdColore(), dto.getIdScelta(), dto.getIdTono(), dto.getIdTaglia(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Erorre nell'inserimento del codice a barre per il prodotto {}", dto.getIdProdotto(), e);
            throw new SQLException(e);
        }
    }

}

