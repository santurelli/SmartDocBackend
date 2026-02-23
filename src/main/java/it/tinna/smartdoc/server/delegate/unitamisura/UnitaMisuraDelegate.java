package it.tinna.smartdoc.server.delegate.unitamisura;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import it.tinna.smartdoc.server.dao.unitamisura.UnitaMisuraDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.unitamisura.UnitaMisuraDto;

@Service(value = "unitaMisuraDelegate")
public class UnitaMisuraDelegate extends BaseDelegate
{

    // public UnitaMisuraDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    public void delete(final long idUser,
                       List<Long> ids) throws SQLException
    {
        final UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
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

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }

    public UnitaMisuraDto isExistentDifferentUm(Integer id) throws SQLException
    {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        return dao.isExistentDifferentUm(id);
    }

    public UnitaMisuraDto getById(Integer id) throws SQLException
    {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<UnitaMisuraDto> getList(String strToSearch,
                                        Integer length,
                                        Integer start,
                                        Integer orderColumn,
                                        String orderDir) throws SQLException
    {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        return dao.getList(strToSearch, length, start, orderColumn, orderDir);
    }

    public List<UnitaMisuraDto> getListForCombo() throws SQLException
    {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public void insert(UnitaMisuraDto dto) throws SQLException
    {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        dao.insert(dto);
    }

    public void update(UnitaMisuraDto dto) throws SQLException
    {
        UnitaMisuraDao dao = new UnitaMisuraDao(jdbcTemplate);
        dao.update(dto);
    }

}

