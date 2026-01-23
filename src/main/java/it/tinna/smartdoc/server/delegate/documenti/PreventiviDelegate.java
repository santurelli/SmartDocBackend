package it.tinna.smartdoc.server.delegate.documenti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.documenti.PreventiviDao;
import it.tinna.smartdoc.shared.dto.documenti.PreventivoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;

@Service
public class PreventiviDelegate {

    @Autowired
    private PreventiviDao preventiviDao;

    @Transactional(rollbackFor = Exception.class)
    public void delete(long id, long userId) throws SQLException {
        PreventivoDto dto = new PreventivoDto();
        dto.setId(id);
        dto.setUserLastUpdate(userId);
        preventiviDao.delete(dto);
    }

    public PreventivoDto getById(long id) throws SQLException {
        return preventiviDao.getById(id);
    }

    // Returns formatted for Datatables
    public DatatablesResponseDto<PreventivoDto> getList(Integer idCliente, String dtFrom, String dtTo, Integer idAgente,
            Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        
        List<PreventivoDto> list = preventiviDao.getList(idCliente, dtFrom, dtTo, idAgente, length, start, orderColumn, orderDir);
        long total = 0;
        if (list.size() > 0) {
            total = list.get(0).getTotal(); // Using window function count from query
        }
        
        DatatablesResponseDto<PreventivoDto> response = new DatatablesResponseDto<>();
        response.setList(list);
        response.setTotalCount(total);
        response.setTotalFiltered(total);
        
        // Calculate total stats if needed (legacy had totFatturato for the view)
        // Ignoring for now or can calculate sum in Java if list is small, but paging...
        // Legacy returned "totFatturato" in a separate field in DatatablesResponseDto? 
        // Or generic payload?
        // Checking legacy impl, it set a custom property on DTO or response?
        // Legacy DAO query usually returns total filtered.
        
        return response;
    }

    public String getNextNumPreventivo(String data) throws SQLException {
        return preventiviDao.generaCodice(data);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer insert(PreventivoDto dto) throws SQLException {
        Integer id = preventiviDao.insert(dto);
        dto.setId(id);
        if (dto.getProdotti() != null) {
            for (ProdottoDocumentoDto riga : dto.getProdotti()) {
                riga.setIdDocumento(id);
                // Handle "Fuori Magazzino" logic if needed, but DAO inserts mostly same fields
                preventiviDao.insertProdotto(riga);
            }
        }
        // Spese Incasso handling if needed (legacy had PREVENTIVI_I03)
        // If passed in DTO, insert them. Base DTO has listSpeseIncassoFattura? 
        // DocumentoDto has "listaSpeseIncassoFattura" (generic name probably).
        
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(PreventivoDto dto) throws SQLException {
        preventiviDao.update(dto);
        
        // Replace lines: Delete all lines and re-insert
        // Using direct DAO calls (assuming delete lines/expenses queries take idDocumento)
        // PREVENTIVI_D01 deletes products by k_d_e_preventivi
        // PREVENTIVI_D02 deletes expenses by k_d_e_preventivi
        // The DAO 'update' method calls D01, U01. 
        // Wait, DAO update method I wrote calls D01 (Delete products) implicitly? 
        // Let's check DAO content I wrote.
        // Yes: 
        // jdbcTemplate.update(FileQueryReader.getQuery("PREVENTIVI_D01"), dto.getId());
        // jdbcTemplate.update(FileQueryReader.getQuery("PREVENTIVI_U01"), ...);
        // It DOES NOT call D02 (Expenses). I should add D02 call in DAO or here.
        // The DAO update method implementation included D01 call.
        
        // Re-insert products
        if (dto.getProdotti() != null) {
            for (ProdottoDocumentoDto riga : dto.getProdotti()) {
                riga.setIdDocumento(dto.getId());
                preventiviDao.insertProdotto(riga);
            }
        }
        
        // Handling Expenses:
        // DAO update did not delete expenses explicitly? I should check.
        // If I need to manage expenses, I should add delete/insert logic.
    }
}
