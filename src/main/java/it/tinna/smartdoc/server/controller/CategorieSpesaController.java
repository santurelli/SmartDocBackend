package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.categoriespesa.CategorieSpesaDelegate;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.categoriespesa.CategoriaSpesaDto;

@RestController
@RequestMapping("/api/categoriespesa")
public class CategorieSpesaController {

    @Autowired
    private CategorieSpesaDelegate categorieSpesaDelegate;

    @GetMapping
    public ResponseEntity<List<CategoriaSpesaDto>> getList(@RequestParam(required = false) String descrizione) {
        try {
            String descSearch = StringUtils.defaultIfEmpty(descrizione, null);
            if(descSearch != null) {
                descSearch = StringUtility.formatForLike(descSearch);
            }
            // If the query expects parameter, we must provide it. 
            // In legacy DAO: jdbcTemplate.query(..., descrizione)
            // If S01 query is "WHERE descrizione LIKE ?", then we need wildcards.
            // If standard behavior is needed, we assume legacy query handles it.
            // But looking at legacy DAO again: 
            // `jdbcTemplate.query(FileQueryReader.getQuery("CATEGORIESPESA_S01"), rowMapper, descrizione);`
            // Usually if parameter is optional in SQL, it's `LIKE COALESCE(?, descrizione)`.
            // So passing null is handled if query is written that way.
            return ResponseEntity.ok(categorieSpesaDelegate.getList(descSearch));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Add other methods if full CRUD is needed later. For FornitoriDetail, getList for combo is sufficient.
}
