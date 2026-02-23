package it.tinna.smartdoc.server.dao.registriiva;

import java.sql.ResultSet;
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
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.ArrayList;
import java.util.List;
import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.constants.Periodi;
import it.tinna.smartdoc.shared.dto.documenti.IvaDocumentoDto;

public class RegistriIvaDao extends BaseDao
{

    public RegistriIvaDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public List<Integer> getElencoAnniDocIva() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<Integer> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(Integer.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("IVADOCUMENTI_S02"), new ResultSetExtractor<List<Integer>>()
            {
                @Override
                public List<Integer> extractData(ResultSet rs) throws SQLException, DataAccessException
                {
                    List<Integer> result = new ArrayList<>();
                    while (rs.next())
                    {
                        result.add(rs.getInt(1));
                    }
                    return result;
                }
            });
            // return jdbcTemplate.query(FileQueryReader.getQuery("IVADOCUMENTI_S02"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco anni iva", e);
            throw new SQLException(e);
        }
    }

    public List<IvaDocumentoDto> getListIvaDocumenti(Periodi periodo,
                                                     int anno,
                                                     Integer length,
                                                     Integer start,
                                                     Integer orderColumn,
                                                     String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("IVADOCUMENTI_S01");
        List<Object> params = new ArrayList<>();
        Map<String, String> valuesMap = new HashMap<>();
        if ( periodo == Periodi.PRIMO_TIMESTRE || periodo == Periodi.SECONDO_TIMESTRE || periodo == Periodi.TERZO_TRIMESTRE || periodo == Periodi.QUARTO_TRIMESTRE )
        {
            String dataDa = "";
            String dataA = "";
            String whereCondition = "";
            if ( periodo == Periodi.PRIMO_TIMESTRE )
            {
                dataDa = "01/" + anno;
                dataA = "03/" + anno;
            }
            else if ( periodo == Periodi.SECONDO_TIMESTRE )
            {
                dataDa = "04/" + anno;
                dataA = "06/" + anno;
            }
            else if ( periodo == Periodi.TERZO_TRIMESTRE )
            {
                dataDa = "07/" + anno;
                dataA = "09/" + anno;
            }
            else if ( periodo == Periodi.QUARTO_TRIMESTRE )
            {
                dataDa = "10/" + anno;
                dataA = "12/" + anno;
            }
            params.add(dataDa);
            params.add(dataA);
            whereCondition = " and date_trunc('month', data_iva) >= to_date(?, 'MM/YYYY') " + " and date_trunc('month', data_iva) <= to_date(?, 'MM/YYYY') ";
            valuesMap.put("WHERE_PERIODO", whereCondition);
        }
        else if ( periodo == Periodi.ANNUALE )
        {
            valuesMap.put("WHERE_PERIODO", " ");
        }
        else
        {
            String mese = "" + periodo.getMese() + "/" + anno;
            String whereCondition = " and date_trunc('month', data_iva) = to_date(?, 'MM/YYYY') ";
            valuesMap.put("WHERE_PERIODO", whereCondition);
            params.add(mese);
        }
        if ( orderColumn == 0 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("gruppoDocumento, data_iva ").append(orderDir).toString());
        }
        else
        {
            valuesMap.put("ORDER_BY", new StringBuilder("gruppoDocumento, data_iva ").append(orderDir).toString());
        }
        params.add(anno);
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
            BeanPropertyRowMapper<IvaDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(IvaDocumentoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco iva documenti per il periodo {} e l'anno {}", periodo, anno, e);
            throw new SQLException(e);
        }
    }

}

