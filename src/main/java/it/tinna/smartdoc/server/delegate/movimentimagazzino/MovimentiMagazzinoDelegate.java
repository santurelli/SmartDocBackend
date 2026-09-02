package it.tinna.smartdoc.server.delegate.movimentimagazzino;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.prodotti.MovimentiMagazzinoDao;
import it.tinna.smartdoc.server.dao.prodotti.ProdottiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentoMagazzinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentiSearchCriteriaDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Transactional(readOnly = true)
@Service
public class MovimentiMagazzinoDelegate extends BaseDelegate {

    @Autowired
    private JdbcTemplate jdbcTemplate;



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
            // The UI List View (PRODOTTI_S01) uses the tenant's magazzino predefinito, not a fixed id.
            // We must use the exact same baseline to calculate the Delta correctly.
            // CAST(? AS INTEGER) is required because idProdotto is Long (BigInt) and function requires Integer.
            Integer idMagazzino = it.tinna.smartdoc.server.util.MagazzinoUtility.getMagazzinoPredefinito(jdbcTemplate);
            String sql = "SELECT get_totale_disponibile(CAST(? AS INTEGER), ?)";
            giacenzaAttuale = jdbcTemplate.queryForObject(sql, Double.class, dto.getIdProdotto(), idMagazzino);
            if (giacenzaAttuale == null) giacenzaAttuale = 0.0;
        } catch (Exception e) {
            log.error("Errore nel recupero della giacenza attuale per la rettifica", e);
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

    public List<MovimentoMagazzinoDto> list(MovimentiSearchCriteriaDto criteria) throws SQLException {
        MovimentiMagazzinoDao dao = new MovimentiMagazzinoDao(jdbcTemplate);
        return dao.list(criteria);
    }
}

