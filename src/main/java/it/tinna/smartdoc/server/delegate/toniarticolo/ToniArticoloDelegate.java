package it.tinna.smartdoc.server.delegate.toniarticolo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.toniarticolo.ToniArticoloDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.toniarticolo.TonoArticoloDto;

@Transactional(readOnly = true)
@Service(value = "toniArticoloDelegate")
public class ToniArticoloDelegate extends BaseDelegate
{

    @Transactional(rollbackFor = Throwable.class)
    public void delete(long idUser,
                       List<Long> ids) throws SQLException
    {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        for ( Long id : ids )
        {
            dao.delete(idUser, id);
        }
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }

    public TonoArticoloDto getById(Integer id) throws SQLException
    {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<TonoArticoloDto> getList(String strToSearch,
                                         Integer length,
                                         Integer start,
                                         Integer orderColumn,
                                         String orderDir) throws SQLException
    {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        return dao.getList(strToSearch, length, start, orderColumn, orderDir);
    }

    public List<TonoArticoloDto> getListForCombo() throws Exception
    {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void insert(TonoArticoloDto dto) throws SQLException
    {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        dao.insert(dto);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void update(TonoArticoloDto dto) throws SQLException
    {
        ToniArticoloDao dao = new ToniArticoloDao(jdbcTemplate);
        dao.update(dto);
    }

}

