package it.tinna.smartdoc.server.dao.prodotti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.prodotti.InventarioMagazzinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.InventarioSearchCriteriaDto;

@Repository
public class InventarioMagazzinoDao extends BaseDao {

    public InventarioMagazzinoDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private Integer getMagazzinoPredefinito() {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("MAGAZZINI_S_PREDEFINITO"), Integer.class);
        } catch (Exception e) {
            _log.warn("Magazzino predefinito non trovato, uso fallback MIN(k_d_e_magazzini)");
            try {
                return jdbcTemplate.queryForObject("SELECT MIN(k_d_e_magazzini) FROM d_e_magazzini WHERE fl_deleted = 0", Integer.class);
            } catch (Exception ex) {
                _log.error("Impossibile determinare il magazzino predefinito", ex);
                return 1;
            }
        }
    }

    public List<InventarioMagazzinoDto> list(InventarioSearchCriteriaDto criteria) throws SQLException {
        try {
            String query = FileQueryReader.getQuery("INVENTARIOMAGAZZINO_S01");
            List<Object> params = new ArrayList<>();
            
            Integer idMagazzino = criteria.getIdMagazzino() != null ? criteria.getIdMagazzino() : getMagazzinoPredefinito();
            Integer idFornitore = criteria.getIdFornitore() != null ? criteria.getIdFornitore() : 0;
            Integer idArticolo = criteria.getIdArticolo() != null ? criteria.getIdArticolo() : 0;
            String dataAl = criteria.getDataAl();
            
            // Format dataAl to DD/MM/YYYY if it's in YYYY-MM-DD format
            if (dataAl != null && dataAl.contains("-")) {
                String[] parts = dataAl.split("-");
                dataAl = parts[2] + "/" + parts[1] + "/" + parts[0];
            }

            // Fixed parameters for the main query structure
            params.add(idMagazzino); // 1
            params.add(dataAl);      // 2
            params.add("VM");        // 3 (tipoValorizzazione)
            params.add(idMagazzino); // 4
            params.add(idFornitore); // 5
            params.add(dataAl);      // 6
            params.add(idMagazzino); // 7
            params.add(dataAl);      // 8
            params.add(idFornitore); // 9
            params.add(idArticolo);  // 10

            Map<String, String> valuesMap = new HashMap<>();
            
            // Handle ORDER BY
            String orderBy = "d_e_prodotti.descrizione ASC"; // Default
            if (criteria.getOrderColumn() != null) {
                String dir = criteria.getOrderDir() != null ? criteria.getOrderDir() : "asc";
                switch (criteria.getOrderColumn()) {
                    case 0: orderBy = "d_e_prodotti.codice " + dir; break;
                    case 1: orderBy = "d_e_prodotti.descrizione " + dir; break;
                    case 2: orderBy = "x.descCategoria " + dir; break;
                    case 3: orderBy = "x.descSottoCategoria " + dir; break;
                    case 4: orderBy = "d_e_fornitori.denominazione " + dir; break;
                    case 6: 
                        orderBy = "get_totale_disponibile(d_e_prodotti.k_d_e_prodotti,NULLIF(?,0),?) " + dir; 
                        params.add(idMagazzino);
                        params.add(dataAl);
                        break;
                }
            }
            valuesMap.put("ORDER_BY", orderBy);

            // Handle LIMIT
            if (criteria.getLength() > 0) {
                valuesMap.put("LIMIT", "LIMIT ? OFFSET ?");
                params.add(criteria.getLength());
                params.add(criteria.getStart());
            } else {
                valuesMap.put("LIMIT", "");
            }

            query = StringSubstitutor.replace(query, valuesMap);

            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(InventarioMagazzinoDto.class), params.toArray());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}

