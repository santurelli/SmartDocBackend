package it.tinna.smartdoc.server.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.primanota.PrimaNotaDelegate;
import it.tinna.smartdoc.shared.dto.primanota.PagamentoPrimaNotaDto;
import it.tinna.smartdoc.shared.dto.primanota.PrimaNotaDto;
import it.tinna.smartdoc.shared.dto.primanota.PrimaNotaSearchCriteriaDto;

@RestController
@RequestMapping("/api/primanota")
public class PrimaNotaController {

    private static final Logger log = LoggerFactory.getLogger(PrimaNotaController.class);

    @Autowired
    private PrimaNotaDelegate primaNotaDelegate;

    @GetMapping
    public ResponseEntity<List<PrimaNotaDto>> getList(
            @RequestParam(required = false) Integer tipoPagamento,
            @RequestParam(required = false) String idSoggetto,
            @RequestParam(required = false) String dtFrom,
            @RequestParam(required = false) String dtTo,
            @RequestParam(required = false) Integer idRisorsa,
            @RequestParam(required = false) String tipologia,
            @RequestParam(required = false, defaultValue = "0") long idDivisione,
            @RequestParam(required = false) Integer length,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false, defaultValue = "0") Integer orderColumn,
            @RequestParam(required = false, defaultValue = "desc") String orderDir) {
        try {
            return ResponseEntity.ok(primaNotaDelegate.getList(tipoPagamento, idSoggetto, dtFrom, dtTo, idRisorsa, tipologia, idDivisione, length, start, orderColumn, orderDir));
        } catch (SQLException e) {
            log.error("Errore nel recupero della prima nota", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody PagamentoPrimaNotaDto dto) {
        try {
            primaNotaDelegate.insertPagamento(dto);
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            log.error("Errore inserimento della prima nota", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody PagamentoPrimaNotaDto dto) {
        try {
            dto.setId(id);
            primaNotaDelegate.updatePagamento(dto);
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            log.error("Errore aggiornamento della prima nota", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}/{idUtente}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @PathVariable Long idUtente) {
        try {
            primaNotaDelegate.deletePagamento(id, idUtente);
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            log.error("Errore cancellazione prima nota", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(@RequestBody PrimaNotaSearchCriteriaDto criteria) {
        try {
            // Get all rows by disabling pagination
            List<PrimaNotaDto> list = primaNotaDelegate.getList(
                criteria.getTipoPagamento(), 
                criteria.getIdSoggetto(), 
                criteria.getDtFrom(), 
                criteria.getDtTo(), 
                criteria.getIdRisorsa(), 
                criteria.getTipologia(), 
                criteria.getIdDivisione(), 
                null, null, // pagination null to get all rows
                0, "desc"
            );

            Context context = new Context();
            context.putVar("rows", list);
            context.putVar("dal", criteria.getDtFrom());
            context.putVar("al", criteria.getDtTo());

            ClassPathResource templateResource = new ClassPathResource("report/prima_nota.xls");
            try (InputStream is = templateResource.getInputStream();
                 ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                
                JxlsHelper.getInstance().processTemplate(is, os, context);
                byte[] content = os.toByteArray();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
                headers.setContentDispositionFormData("attachment", "prima_nota.xls");
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(content);
            }
        } catch (Exception e) {
            log.error("Errore nell'esportazione excel della prima nota", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}