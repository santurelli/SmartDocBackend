package it.tinna.smartdoc.server.dao.prodotti;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.prodotti.PrezzoProdottoDto;

@Repository
public class PrezziProdottiDao extends BaseDao
{

    public PrezziProdottiDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public List<PrezzoProdottoDto> creaListaByIdListinoePrezzo(Integer idListino,
                                                               BigDecimal prezzo) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<PrezzoProdottoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(PrezzoProdottoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("PRODOTTI_S07"), rowMapper, idListino, prezzo, idListino, idListino);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della lista prezzi per il listino {}", idListino, e);
            throw new SQLException(e);
        }
    }

    public void deleteByIdProdotto(long idProdotto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("PRODOTTI_D02"), idProdotto);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione dei prezzi per l'articolo {}", idProdotto, e);
            throw new SQLException(e);
        }
    }

    public List<PrezzoProdottoDto> getByIdProdotto(long idProdotto) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<PrezzoProdottoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(PrezzoProdottoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("PRODOTTI_S06"), rowMapper, idProdotto);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco prezzi per l'articolo {}", idProdotto, e);
            throw new SQLException(e);
        }
    }

    public void insert(PrezzoProdottoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("PRODOTTI_I02"), dto.getIdListino(), dto.getIdProdotto(), dto.getPrezzo());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento del prezzo per l'articolo {} ed il listino {}", dto.getIdProdotto(), dto.getIdListino(), e);
            throw new SQLException(e);
        }
    }

}

