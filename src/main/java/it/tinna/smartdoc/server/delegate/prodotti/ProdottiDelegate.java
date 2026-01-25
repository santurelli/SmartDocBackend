package it.tinna.smartdoc.server.delegate.prodotti;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.prodotti.ProdottiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;

@Service
public class ProdottiDelegate extends BaseDelegate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ProdottoDto> getList(String categoria, String search, int length, int start, int orderColumn, String orderDir,
            Double giacenza, String operatoreGiacenza, Integer idFornitore, Integer idTono, Integer idCalibro) throws SQLException {
        ProdottiDao dao = new ProdottiDao(jdbcTemplate);
        return dao.getList(categoria, search, length, start, orderColumn, orderDir, giacenza, operatoreGiacenza, idFornitore, idTono, idCalibro);
    }

    public ProdottoDto getById(long id) throws SQLException {
        ProdottiDao dao = new ProdottiDao(jdbcTemplate);
        return dao.getById(id);
    }

    public Map<String, Object> getCombosMap() throws SQLException {
        ProdottiDao dao = new ProdottiDao(jdbcTemplate);
        return dao.getCombosMap();
    }
    
     public boolean isExistentCodice(String codice, Integer id) throws SQLException {
        ProdottiDao dao = new ProdottiDao(jdbcTemplate);
        return dao.isExistentCodice(codice, id);
    }

    public void insert(ProdottoDto dto) throws SQLException {
        ProdottiDao dao = new ProdottiDao(jdbcTemplate);
        dao.insert(dto);
    }

    public void update(ProdottoDto dto) throws SQLException {
        ProdottiDao dao = new ProdottiDao(jdbcTemplate);
        dao.update(dto);
    }

    public String getProssimoCodice() throws SQLException {
        ProdottiDao dao = new ProdottiDao(jdbcTemplate);
        return dao.getProssimoCodice();
    }

    public void delete(long id, Object user) throws SQLException {
        ProdottiDao dao = new ProdottiDao(jdbcTemplate);
        dao.delete(id, user);
    }
}
