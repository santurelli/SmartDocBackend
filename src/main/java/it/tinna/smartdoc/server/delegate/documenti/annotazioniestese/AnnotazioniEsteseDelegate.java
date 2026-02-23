package it.tinna.smartdoc.server.delegate.documenti.annotazioniestese;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.documenti.annotazioniestese.AnnotazioniEsteseDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.documenti.annotazioniestese.AnnotazioneEstesaDto;

@Service(value = "annotazioniEsteseDelegate")
public class AnnotazioniEsteseDelegate extends BaseDelegate
{

    // public AnnotazioniEsteseDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    @Transactional(rollbackFor = SQLException.class)
    public void delete(long idUser,
                       List<Long> ids) throws SQLException
    {
        AnnotazioniEsteseDao dao = new AnnotazioniEsteseDao(jdbcTemplate);
        for ( Long id : ids )
        {
            dao.delete(idUser, id);
        }
    }

    public AnnotazioneEstesaDto getById(Integer id) throws SQLException
    {
        AnnotazioniEsteseDao dao = new AnnotazioniEsteseDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<AnnotazioneEstesaDto> getList(String strToSearch,
                                              Integer length,
                                              Integer start,
                                              Integer orderColumn,
                                              String orderDir) throws SQLException
    {
        AnnotazioniEsteseDao dao = new AnnotazioniEsteseDao(jdbcTemplate);
        return dao.getList(strToSearch, length, start, orderColumn, orderDir);
    }

    public void insert(AnnotazioneEstesaDto dto) throws SQLException
    {
        AnnotazioniEsteseDao dao = new AnnotazioniEsteseDao(jdbcTemplate);
        dao.insert(dto);
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        AnnotazioniEsteseDao dao = new AnnotazioniEsteseDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }

    public void update(AnnotazioneEstesaDto dto) throws SQLException
    {
        AnnotazioniEsteseDao dao = new AnnotazioniEsteseDao(jdbcTemplate);
        dao.update(dto);
    }

}

