package it.tinna.smartdoc.server.delegate.aliquoteiva;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;

@Service(value = "aliquoteivaDelegate")
public class AliquoteIvaDelegate extends BaseDelegate
{

    // public AliquoteIvaDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    public void delete(long idUser,
                       List<Long> ids) throws SQLException
    {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        for ( Long id : ids )
        {
            dao.delete(idUser, id);
        }
    }

    public boolean isExistent(String codice,
                              Integer id) throws SQLException
    {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        return dao.isExistent(codice, id);
    }

    public AliquotaIvaDto getById(Integer id) throws SQLException
    {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<AliquotaIvaDto> getList(String strToSearch,
                                        Integer length,
                                        Integer start,
                                        Integer orderColumn,
                                        String orderDir) throws SQLException
    {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        return dao.getList(strToSearch, length, start, orderColumn, orderDir);
    }

    public List<AliquotaIvaDto> getByAliquota(double aliquota) throws SQLException
    {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        return dao.getByAliquota(aliquota);
    }

    public List<AliquotaIvaDto> getListForCombo() throws SQLException
    {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    @Transactional(rollbackFor = SQLException.class)
    public void insert(AliquotaIvaDto dto) throws SQLException
    {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        if ( dto.getPredefinita().intValue() == 1 )
        {
            dao.resetPredefinite(dto.getUserCreated());
        }
        dao.insert(dto);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void update(AliquotaIvaDto dto) throws SQLException
    {
        AliquoteIvaDao dao = new AliquoteIvaDao(jdbcTemplate);
        if ( dto.getPredefinita().intValue() == 1 )
        {
            dao.resetPredefinite(dto.getUserCreated());
        }
        dao.update(dto);
    }

}

