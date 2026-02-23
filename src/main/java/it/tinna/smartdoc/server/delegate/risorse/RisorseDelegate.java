package it.tinna.smartdoc.server.delegate.risorse;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import it.tinna.smartdoc.server.dao.risorse.RisorseDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.risorse.RisorsaDto;

@Service(value = "risorseDelegate")
public class RisorseDelegate extends BaseDelegate
{

    // public RisorseDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    public void delete(final long idUser,
                       List<Long> ids) throws SQLException
    {
        final RisorseDao dao = new RisorseDao(jdbcTemplate);
        for (long arg0 : ids)
        {
            try
            {
                dao.delete(idUser, arg0);
            }
            catch ( SQLException e )
            {
                break;
            }
        }
    }

    public boolean isExistent(String tipologia,
                              String descrizione,
                              Integer id) throws SQLException
    {
        RisorseDao dao = new RisorseDao(jdbcTemplate);
        return dao.isExistent(tipologia, descrizione, id);
    }

    public RisorsaDto getByDenominazione(String tipologia,
                                         String descrizione) throws SQLException
    {
        RisorseDao dao = new RisorseDao(jdbcTemplate);
        return dao.getByDenominazione(tipologia, descrizione);
    }

    public RisorsaDto getById(Integer id) throws SQLException
    {
        RisorseDao dao = new RisorseDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<RisorsaDto> getList(String tipologia,
                                    String strToSearch,
                                    Integer length,
                                    Integer start,
                                    Integer orderColumn,
                                    String orderDir) throws SQLException
    {
        RisorseDao dao = new RisorseDao(jdbcTemplate);
        return dao.getList(tipologia, strToSearch, length, start, orderColumn, orderDir);
    }

    public List<RisorsaDto> getListForCombo(String tipologia) throws SQLException
    {
        RisorseDao dao = new RisorseDao(jdbcTemplate);
        return dao.getListForCombo(tipologia);
    }

    // public List<ItemSuggestion> getSuggestionBanche(String query) throws
    // Exception {
    // Connection conn = null;
    // try {
    // conn = PooledCnn.getSingleton(dbKey);
    // RisorseDao dao = new RisorseDao(conn);
    // return dao.getSuggestionBanche(query);
    // }
    // finally {
    // PooledCnn.close(conn);
    // }
    // }

    @Transactional(rollbackFor = SQLException.class)
    public void insert(RisorsaDto dto) throws SQLException
    {
        RisorseDao dao = new RisorseDao(jdbcTemplate);
        if ( dto.getPredefinita().intValue() == 1 )
        {
            dao.resetPredefinite(dto.getTipologia(), dto.getUserCreated());
        }
        dao.insert(dto);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void update(RisorsaDto dto) throws SQLException
    {
        RisorseDao dao = new RisorseDao(jdbcTemplate);
        if ( dto.getPredefinita().intValue() == 1 )
        {
            dao.resetPredefinite(dto.getTipologia(), dto.getUserCreated());
        }
        dao.update(dto);
    }

}

