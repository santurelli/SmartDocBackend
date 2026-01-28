package it.tinna.smartdoc.server.delegate.notedocumenti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.notedocumenti.NoteDocumentiDao;
import it.tinna.smartdoc.shared.dto.notedocumenti.NotaDocumentoDto;

@Service
@CacheConfig(cacheNames = "notedocumenti", cacheResolver = "companyCacheResolver")
public class NoteDocumentiDelegate {

    @Autowired
    private NoteDocumentiDao noteDocumentiDao;

    public List<NotaDocumentoDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        return noteDocumentiDao.getList(search, length, start, orderCol, orderDir);
    }

    @Cacheable
    public List<NotaDocumentoDto> getListForCombo() throws SQLException {
        return noteDocumentiDao.getListForCombo();
    }
    
    public NotaDocumentoDto getById(Integer id) throws SQLException {
        return noteDocumentiDao.getById(id);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void insert(NotaDocumentoDto dto, Integer userId) throws SQLException {
        noteDocumentiDao.insert(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void update(NotaDocumentoDto dto, Integer userId) throws SQLException {
        noteDocumentiDao.update(dto, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void delete(Integer id, Integer userId) throws SQLException {
        noteDocumentiDao.delete(id, userId);
    }
    
    public boolean isExistent(String descrizione, Integer id) throws SQLException {
        return noteDocumentiDao.checkUniqueness(descrizione, id);
    }
}
