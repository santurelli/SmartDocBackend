package it.tinna.smartdoc.server.dao.movimentiprodotti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.movimentiprodotti.MovimentoProdottoDto;

public class MovimentiProdottiDao extends BaseDao
{

    public MovimentiProdottiDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public List<MovimentoProdottoDto> getList(long idProdotto,
                                              String dataMovimentoDa,
                                              String dataMovimentoA,
                                              long idMagazzino,
                                              int length,
                                              int start,
                                              int orderColumn,
                                              String orderDir) throws SQLException
    {
        try
        {
            String query = FileQueryReader.getQuery("MOVIMENTIPRODOTTI_S01");
            List<Object> params = new ArrayList<>();
            params.add(idProdotto);
            params.add(dataMovimentoDa);
            params.add(dataMovimentoA);
            params.add(idMagazzino);
            Map<String, String> valuesMap = new HashMap<>();
            if ( orderColumn == 0 )
            {
                valuesMap.put("ORDER_BY", String.format("data_movimento %s", orderDir));
            }
            else if ( orderColumn == 1 )
            {
                valuesMap.put("ORDER_BY", String.format("descrizione_prodotto %s", orderDir));
            }
            else if ( orderColumn == 2 )
            {
                valuesMap.put("ORDER_BY", String.format("cliente_fornitore %s", orderDir));
            }
            if ( length > 0 && start >= 0 )
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
            BeanPropertyRowMapper<MovimentoProdottoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(MovimentoProdottoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco movimenti", e);
            throw new SQLException(e);
        }
    }

}

