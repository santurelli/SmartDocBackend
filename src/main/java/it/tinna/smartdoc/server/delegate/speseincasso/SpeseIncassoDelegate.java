package it.tinna.smartdoc.server.delegate.speseincasso;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.speseincasso.SpeseIncassoDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.speseincasso.SpesaIncassoDto;

@Service(value = "speseIncassoDelegate")
public class SpeseIncassoDelegate extends BaseDelegate
{

    // public SpeseIncassoDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    @Transactional(rollbackFor = SQLException.class)
    public void delete(Integer idUser,
                       List<Integer> ids) throws SQLException
    {
        SpeseIncassoDao dao = new SpeseIncassoDao(jdbcTemplate);
        for ( Integer id : ids )
        {
            dao.delete(idUser, id);
        }
    }

    public SpesaIncassoDto getById(Integer id) throws SQLException
    {
        SpeseIncassoDao dao = new SpeseIncassoDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<SpesaIncassoDto> getList(String descrizione) throws SQLException
    {
        SpeseIncassoDao dao = new SpeseIncassoDao(jdbcTemplate);
        return dao.getList(descrizione);
    }

    public void insert(SpesaIncassoDto dto) throws SQLException
    {
        SpeseIncassoDao dao = new SpeseIncassoDao(jdbcTemplate);
        dao.insert(dto);
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        SpeseIncassoDao dao = new SpeseIncassoDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }

    public void update(SpesaIncassoDto dto) throws SQLException
    {
        SpeseIncassoDao dao = new SpeseIncassoDao(jdbcTemplate);
        dao.update(dto);
    }

}

