package it.tinna.smartdoc.server.delegate.dipendenti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.avvisi.AvvisiDao;
import it.tinna.smartdoc.server.dao.dipendenti.DipendentiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.avvisi.AvvisoDto;
import it.tinna.smartdoc.shared.dto.dipendenti.DipendenteDto;

@Transactional(readOnly = true)
@Service(value = "dipendentiDelegate")
public class DipendentiDelegate extends BaseDelegate
{

    // public DipendentiDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(long idUser,
                       long id) throws SQLException
    {
        DipendentiDao dao = new DipendentiDao(jdbcTemplate);
        dao.delete(idUser, id);
    }

    public boolean isExistent(String cognome,
                              String nome,
                              Long id) throws SQLException
    {
        DipendentiDao dao = new DipendentiDao(jdbcTemplate);
        return dao.isExistent(cognome, nome, id);
    }

    public DipendenteDto getById(long id) throws SQLException
    {
        DipendentiDao dao = new DipendentiDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<DipendenteDto> getList(String strToSearch,
                                       int length,
                                       Integer start,
                                       Integer orderColumn,
                                       String orderDir) throws SQLException
    {
        DipendentiDao dao = new DipendentiDao(jdbcTemplate);
        return dao.getList(strToSearch, length, start, orderColumn, orderDir);
    }

    public List<AvvisoDto> getListForCombo() throws SQLException
    {
        AvvisiDao dao = new AvvisiDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void insert(DipendenteDto dto) throws SQLException
    {
        DipendentiDao dao = new DipendentiDao(jdbcTemplate);
        dao.insert(dto);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void update(DipendenteDto dto) throws SQLException
    {
        DipendentiDao dao = new DipendentiDao(jdbcTemplate);
        dao.update(dto);
    }

}

