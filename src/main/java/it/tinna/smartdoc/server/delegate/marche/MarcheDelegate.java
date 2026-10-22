package it.tinna.smartdoc.server.delegate.marche;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.marche.MarcheDao;
import it.tinna.smartdoc.shared.dto.marche.MarcaDto;

@Service
public class MarcheDelegate {

    @Autowired
    private MarcheDao marcheDao;

    public List<MarcaDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return marcheDao.getList(search, length, start, orderCol, orderDir);
    }

    public List<MarcaDto> getListForCombo() throws SQLException {
        return marcheDao.getListForCombo();
    }

    public MarcaDto getById(long id) throws SQLException {
        return marcheDao.getById(id);
    }

    public long insert(MarcaDto dto) throws SQLException {
        return marcheDao.insert(dto);
    }

    public void update(MarcaDto dto) throws SQLException {
        marcheDao.update(dto);
    }

    public void delete(long id, long userId) throws SQLException {
        marcheDao.delete(id, userId);
    }
}
