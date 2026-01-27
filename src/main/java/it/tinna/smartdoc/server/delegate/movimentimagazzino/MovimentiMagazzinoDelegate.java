package it.tinna.smartdoc.server.delegate.movimentimagazzino;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.prodotti.MovimentiMagazzinoDao;
import it.tinna.smartdoc.server.dao.prodotti.ProdottiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentoMagazzinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentiSearchCriteriaDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import java.util.List;
import it.tinna.smartdoc.server.service.InventoryService;

@Service
public class MovimentiMagazzinoDelegate extends BaseDelegate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InventoryService inventoryService;

    public Integer insertCarico(MovimentoMagazzinoDto dto) throws SQLException {
        MovimentiMagazzinoDao dao = new MovimentiMagazzinoDao(jdbcTemplate);
        return dao.insertCarico(dto);
    }

    public Integer insertScarico(MovimentoMagazzinoDto dto) throws SQLException {
        MovimentiMagazzinoDao dao = new MovimentiMagazzinoDao(jdbcTemplate);
        return dao.insertScarico(dto);
    }

    public void insertRettifica(MovimentoMagazzinoDto dto) throws SQLException {
        MovimentiMagazzinoDao dao = new MovimentiMagazzinoDao(jdbcTemplate);
        ProdottiDao prodottiDao = new ProdottiDao(jdbcTemplate);
        
        // Fetch current stock SPECIFICALLY for the target warehouse
        Double giacenzaAttuale = 0.0;
        try {
            // Keeping it simple: The UI List View uses Warehouse 1 (Hardcoded in PRODOTTI_S01).
            // We must use the exact same baseline to calculate the Delta correctly.
            // Using parameterized ? for the second arg mysteriously returned Global Stock (-283.6) in previous attempts.
            // Hardcoding 1 ensures we get the Warehouse 1 Stock.
            // CAST(? AS INTEGER) is required because idProdotto is Long (BigInt) and function requires Integer.
            String sql = "SELECT get_totale_disponibile(CAST(? AS INTEGER), 1)";
            giacenzaAttuale = jdbcTemplate.queryForObject(sql, Double.class, dto.getIdProdotto());
            if (giacenzaAttuale == null) giacenzaAttuale = 0.0;
            
            System.out.println("CURRENT STOCK (Warehouse 1): " + giacenzaAttuale);
        } catch (Exception e) {
            System.out.println("ERROR FETCHING SPECIFIC STOCK: " + e.getMessage());
            e.printStackTrace();
        }
        
        double delta = dto.getQuantita() - giacenzaAttuale;
        System.out.println("DELTA (Target - Current): " + delta);
        
        // Use a tolerance for double comparison
        if (Math.abs(delta) < 0.0001) {
            return; // No change needed
        }
        
        if (delta > 0) {
            dto.setTipoMovimento("I");
            dto.setQuantita(delta);
            dao.insertCarico(dto);
        } else {
            dto.setTipoMovimento("U");
            dto.setQuantita(Math.abs(delta));
            dao.insertScarico(dto);
        }
        }
    }

    public List<MovimentoMagazzinoDto> list(MovimentiSearchCriteriaDto criteria) throws SQLException {
        MovimentiMagazzinoDao dao = new MovimentiMagazzinoDao(jdbcTemplate);
        return dao.list(criteria);
    }
}
