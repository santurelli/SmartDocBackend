package it.tinna.smartdoc.server.dao.tabdecod;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.constants.CategorieTabDecod;
import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.tabdecod.TabDecodDto;

public class TabDecodDao extends BaseDao
{

    public TabDecodDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public List<TabDecodDto> getListByCategoria(CategorieTabDecod categoria) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<TabDecodDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(TabDecodDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("TABDECOD_S01"), rowMapper, categoria.name());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<TabDecodDto>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle righe dalla tab_decod per la categoria {}", categoria.name(), e);
            throw new SQLException(e);
        }
    }

}

