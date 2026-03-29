package it.tinna.smartdoc.server.delegate.prodotti;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import it.tinna.smartdoc.server.delegate.listini.PricingDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.listini.ListiniDao;
import it.tinna.smartdoc.server.dao.prodotti.PrezziProdottiDao;
import it.tinna.smartdoc.server.dao.prodotti.ProdottiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.listini.ListiniDelegate;
import it.tinna.smartdoc.shared.dto.prodotti.PrezzoProdottoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;

@Service
public class ProdottiDelegate extends BaseDelegate {

    @Autowired
    private JdbcTemplate    jdbcTemplate;
    @Autowired
    private PricingDelegate pricingDelegate;
    @Autowired
    private PrezziProdottiDao prezziProdottiDao;
    @Autowired
    private ListiniDelegate listiniDelegate;
    @Autowired
    private ProdottiDao prodottiDao;

    public List<ProdottoDto> getList(String categoria, String search, int length, int start, int orderColumn, String orderDir,
            Double giacenza, String operatoreGiacenza, Integer idFornitore, Integer idTono, Integer idCalibro, Integer idFormato, Integer idScelta) throws SQLException {
        return prodottiDao.getList(categoria, search, length, start, orderColumn, orderDir, giacenza, operatoreGiacenza, idFornitore, idTono, idCalibro, idFormato, idScelta);
    }

    public ProdottoDto getById(long id) throws SQLException {
        return prodottiDao.getById(id);
    }

    public Map<String, Object> getCombosMap() throws SQLException {
        return prodottiDao.getCombosMap();
    }
    
     public boolean isExistentCodice(String codice, Integer id) throws SQLException {
        return prodottiDao.isExistentCodice(codice, id);
    }

    public void insert(ProdottoDto dto) throws SQLException {
        prodottiDao.insert(dto);
    }

    public void update(ProdottoDto dto) throws SQLException {
        prodottiDao.update(dto);
    }

    public String getProssimoCodice() throws SQLException {
        return prodottiDao.getProssimoCodice();
    }

    public void delete(long id, Object user) throws SQLException {
        prodottiDao.delete(id, user);
    }

    public Double getPrezzoDocumento(Long idProdotto, Long idListino) throws SQLException {
        return pricingDelegate.calculatePrice(idProdotto, idListino);
    }

    public List<PrezzoProdottoDto> getPrezzi(long idProdotto) throws SQLException {
        return prezziProdottiDao.getByIdProdotto(idProdotto);
    }

    public void savePrezzi(long idProdotto, List<PrezzoProdottoDto> prezzi) throws SQLException {
        prezziProdottiDao.deleteByIdProdotto(idProdotto);
        for (PrezzoProdottoDto dto : prezzi) {
            if (dto.getPrezzo() != null) {
                dto.setIdProdotto(idProdotto);
                prezziProdottiDao.insert(dto);
            }
        }
    }
}

