package it.tinna.smartdoc.server.delegate.prodotti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.prodotti.InventarioMagazzinoDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.prodotti.InventarioMagazzinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.InventarioSearchCriteriaDto;

@Service(value = "inventarioMagazzinoDelegate")
public class InventarioMagazzinoDelegate extends BaseDelegate {

    public List<InventarioMagazzinoDto> list(InventarioSearchCriteriaDto criteria) throws SQLException {
        InventarioMagazzinoDao dao = new InventarioMagazzinoDao(jdbcTemplate);
        return dao.list(criteria);
    }
}

