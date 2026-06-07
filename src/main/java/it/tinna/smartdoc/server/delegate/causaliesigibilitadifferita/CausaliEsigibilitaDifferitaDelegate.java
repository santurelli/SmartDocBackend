package it.tinna.smartdoc.server.delegate.causaliesigibilitadifferita;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.causaliesigibilitadifferita.CausaliEsigibilitaDifferitaDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.causaliesigibilitadifferita.CausaleEsigibilitaDifferitaDto;

@Transactional(readOnly = true)
@Service(value = "causaliesigibilitadifferitaDelegate")
public class CausaliEsigibilitaDifferitaDelegate extends BaseDelegate
{

    // public CausaliEsigibilitaDifferitaDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(long idUser,
                       List<Long> ids) throws SQLException
    {
        CausaliEsigibilitaDifferitaDao dao = new CausaliEsigibilitaDifferitaDao(jdbcTemplate);
        for ( Long id : ids )
        {
            dao.delete(idUser, id);
        }
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        CausaliEsigibilitaDifferitaDao dao = new CausaliEsigibilitaDifferitaDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }

    public CausaleEsigibilitaDifferitaDto getById(int id) throws SQLException
    {
        CausaliEsigibilitaDifferitaDao dao = new CausaliEsigibilitaDifferitaDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<CausaleEsigibilitaDifferitaDto> getList(String strToSearch,
                                                        Integer length,
                                                        Integer start,
                                                        Integer orderColumn,
                                                        String orderDir) throws SQLException
    {
        CausaliEsigibilitaDifferitaDao dao = new CausaliEsigibilitaDifferitaDao(jdbcTemplate);
        return dao.getList(strToSearch, length, start, orderColumn, orderDir);
    }

    public List<CausaleEsigibilitaDifferitaDto> getListForCombo() throws SQLException
    {
        CausaliEsigibilitaDifferitaDao dao = new CausaliEsigibilitaDifferitaDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void insert(CausaleEsigibilitaDifferitaDto dto) throws SQLException
    {
        CausaliEsigibilitaDifferitaDao dao = new CausaliEsigibilitaDifferitaDao(jdbcTemplate);
        dao.insert(dto);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void update(CausaleEsigibilitaDifferitaDto dto) throws SQLException
    {
        CausaliEsigibilitaDifferitaDao dao = new CausaliEsigibilitaDifferitaDao(jdbcTemplate);
        dao.update(dto);
    }

}

