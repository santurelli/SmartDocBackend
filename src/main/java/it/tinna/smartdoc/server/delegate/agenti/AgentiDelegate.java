package it.tinna.smartdoc.server.delegate.agenti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.agenti.AgentiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.agenti.AgenteDto;

@Transactional(readOnly = true)
@Service(value = "agentiDelegate")
public class AgentiDelegate extends BaseDelegate
{

    // public AgentiDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long idUser,
                       List<Long> ids) throws SQLException
    {
        AgentiDao dao = new AgentiDao(jdbcTemplate);
        for ( Long id : ids )
        {
            dao.delete(idUser, id);
        }
    }

    public List<AgenteDto> getSuggestion(String query) throws SQLException
    {
        AgentiDao dao = new AgentiDao(jdbcTemplate);
        return dao.getSuggestion(query);
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        AgentiDao dao = new AgentiDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }

    public AgenteDto getById(Integer id) throws SQLException
    {
        AgentiDao dao = new AgentiDao(jdbcTemplate);
        return dao.getById(id);
    }

    public AgenteDto getByDenominazione(String denominazione) throws SQLException
    {
        AgentiDao dao = new AgentiDao(jdbcTemplate);
        return dao.getByDenominazione(denominazione);
    }

    public List<AgenteDto> getList(String denominazione,
                                   Integer length,
                                   Integer start,
                                   Integer orderColumn,
                                   String orderDir) throws SQLException
    {
        AgentiDao dao = new AgentiDao(jdbcTemplate);
        return dao.getList(denominazione, length, start, orderColumn, orderDir);
    }

    public List<AgenteDto> getListForCombo() throws SQLException
    {
        AgentiDao dao = new AgentiDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void insert(AgenteDto dto) throws SQLException
    {
        AgentiDao dao = new AgentiDao(jdbcTemplate);
        dao.insert(dto);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void update(AgenteDto dto) throws SQLException
    {
        AgentiDao dao = new AgentiDao(jdbcTemplate);
        dao.update(dto);
    }

}

