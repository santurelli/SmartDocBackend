package it.tinna.smartdoc.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.prodotti.InventarioMagazzinoDelegate;
import it.tinna.smartdoc.shared.dto.prodotti.InventarioMagazzinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.InventarioSearchCriteriaDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/inventario")
@Slf4j
public class InventarioMagazzinoController {

    @Autowired
    private InventarioMagazzinoDelegate delegate;

    @PostMapping("/list")
    public GenericResponseDto<List<InventarioMagazzinoDto>> list(@RequestBody InventarioSearchCriteriaDto criteria) {
        GenericResponseDto<List<InventarioMagazzinoDto>> response = new GenericResponseDto<>();
        try {
            response.setPayload(delegate.list(criteria));
        } catch (Exception e) {
            log.error("Error listing inventario", e);
            response.setErrorText("Errore nel recupero dell'inventario");
        }
        return response;
    }

    @PostMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(@RequestBody InventarioSearchCriteriaDto criteria) {
        try {
            // Disable pagination to export all records
            criteria.setLength(-1);
            List<InventarioMagazzinoDto> list = delegate.list(criteria);

            org.jxls.common.Context context = new org.jxls.common.Context();
            context.putVar("rows", list);

            org.springframework.core.io.ClassPathResource templateResource = new org.springframework.core.io.ClassPathResource("report/inventario_magazzino.xls");
            try (java.io.InputStream is = templateResource.getInputStream()) {
                java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();

                org.jxls.util.JxlsHelper.getInstance().processTemplate(is, os, context);

                byte[] content = os.toByteArray();

                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.ms-excel"));
                headers.setContentDispositionFormData("attachment", "inventario_magazzino.xls");
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(content);
            }
        } catch (Exception e) {
            log.error("Error exporting inventario excel", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

