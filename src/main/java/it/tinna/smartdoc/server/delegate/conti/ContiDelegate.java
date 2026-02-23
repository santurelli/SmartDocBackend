package it.tinna.smartdoc.server.delegate.conti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.conti.ContiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.conti.ContoDto;

@Service(value = "contiDelegate")
public class ContiDelegate extends BaseDelegate
{

    public void delete(long idConto,
                       long idUtente) throws SQLException
    {
        ContiDao dao = new ContiDao(jdbcTemplate);
        dao.delete(idConto, idUtente);
    }

    public ContoDto getByDescrizione(String descrizione,
                                     Long id) throws SQLException
    {
        ContiDao dao = new ContiDao(jdbcTemplate);
        return dao.getByDescrizione(descrizione, id);
    }

    public ContoDto getById(long id) throws SQLException
    {
        ContiDao dao = new ContiDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<ContoDto> getList(String strToSearch,
                                  Integer length,
                                  Integer start,
                                  Integer orderColumn,
                                  String orderDir) throws SQLException
    {
        ContiDao dao = new ContiDao(jdbcTemplate);
        return dao.getList(strToSearch, length, start, orderColumn, orderDir);
    }

    public List<ContoDto> getListForCombo() throws SQLException
    {
        ContiDao dao = new ContiDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    @Transactional(rollbackFor = Throwable.class)
    public long insert(ContoDto dto) throws SQLException
    {
        ContiDao dao = new ContiDao(jdbcTemplate);
        if ( dto.getPredefinito() == 1 )
        {
            dao.resetPredefinite(dto.getUserCreated());
        }
        return dao.insert(dto);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void update(ContoDto dto) throws SQLException
    {
        ContiDao dao = new ContiDao(jdbcTemplate);
        if ( dto.getPredefinito() == 1 )
        {
            dao.resetPredefinite(dto.getUserLastUpdate());
        }
        dao.update(dto);
    }

}

