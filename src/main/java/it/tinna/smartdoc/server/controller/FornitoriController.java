package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import it.tinna.smartdoc.server.delegate.fornitori.FornitoriDelegate;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/fornitori")
public class FornitoriController {

    @Autowired
    private FornitoriDelegate fornitoriDelegate;

    @GetMapping
    public ResponseEntity<DatatablesResponseDto<FornitoreDto>> getList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer length,
            @RequestParam(required = false, defaultValue = "0") Integer orderColumn,
            @RequestParam(required = false, defaultValue = "asc") String orderDir) {
        
        try {
            List<FornitoreDto> list = fornitoriDelegate.getList(search, length, start, orderColumn, orderDir);
            long total = list.isEmpty() ? 0 : list.get(0).getTotal();
            
            DatatablesResponseDto<FornitoreDto> response = new DatatablesResponseDto<>();
            response.setList(list);
            response.setTotalCount(total);
            response.setTotalFiltered(total);
            
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornitoreDto> getById(@PathVariable Integer id) {
        try {
            FornitoreDto dto = fornitoriDelegate.getById(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<Integer>> insert(@RequestBody FornitoreDto dto) {
        GenericResponseDto<Integer> response = new GenericResponseDto<>();
        try {
            // Check uniqueness
            if (dto.getCodice() != null && fornitoriDelegate.isExistentCodice(dto.getCodice(), null)) {
                response.setErrorText("Codice esistente");
                return ResponseEntity.badRequest().body(response);
            }
            if (dto.getDenominazione() != null && fornitoriDelegate.isExistentDenominazione(dto.getDenominazione(), null)) {
                response.setErrorText("Denominazione esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer id = fornitoriDelegate.insert(dto);
            response.setPayload(id);
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Void>> update(@PathVariable Integer id, @RequestBody FornitoreDto dto) {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        try {
            dto.setId(id);
            // Check uniqueness excluding self
            if (dto.getCodice() != null && fornitoriDelegate.isExistentCodice(dto.getCodice(), id)) {
                response.setErrorText("Codice esistente");
                return ResponseEntity.badRequest().body(response);
            }
            if (dto.getDenominazione() != null && fornitoriDelegate.isExistentDenominazione(dto.getDenominazione(), id)) {
                response.setErrorText("Denominazione esistente");
                return ResponseEntity.badRequest().body(response);
            }

            fornitoriDelegate.update(dto);
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            FornitoreDto dto = new FornitoreDto();
            dto.setId(id);
            fornitoriDelegate.delete(List.of(dto));
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/listForCombo")
    public ResponseEntity<List<FornitoreDto>> getListForCombo() {
        try {
            return ResponseEntity.ok(fornitoriDelegate.getListForCombo());
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/suggestion")
    public ResponseEntity<List<FornitoreDto>> getSuggestion(@RequestParam String q) {
        try {
            return ResponseEntity.ok(fornitoriDelegate.getSuggestion(q));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/check-uniqueness")
    public ResponseEntity<GenericResponseDto<Boolean>> checkUniqueness(
            @RequestParam(required = false) String codice,
            @RequestParam(required = false) String denominazione,
            @RequestParam(required = false) Integer id) {
        GenericResponseDto<Boolean> response = new GenericResponseDto<>();
        try {
            if (codice != null && fornitoriDelegate.isExistentCodice(codice, id)) {
                 response.setPayload(false);
                 response.setErrorText("Codice esistente");
            } else if (denominazione != null && fornitoriDelegate.isExistentDenominazione(denominazione, id)) {
                 response.setPayload(false);
                 response.setErrorText("Denominazione esistente");
            } else {
                response.setPayload(true);
            }
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
             response.setErrorText(e.getMessage());
             return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/generate-code")
    public ResponseEntity<GenericResponseDto<String>> generateCodice() {
        GenericResponseDto<String> response = new GenericResponseDto<>();
        try {
            response.setPayload(fornitoriDelegate.generaCodice());
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam(required = false) String search) {
        try {
            List<FornitoreDto> list = fornitoriDelegate.getList(search, null, null, 0, "asc");

            org.jxls.common.Context context = new org.jxls.common.Context();
            context.putVar("fornitori", list);

            org.springframework.core.io.ClassPathResource templateResource = new org.springframework.core.io.ClassPathResource("report/elenco_fornitori.xls");
            try (java.io.InputStream is = templateResource.getInputStream()) {
                java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();

                org.jxls.util.JxlsHelper.getInstance().processTemplate(is, os, context);

                byte[] content = os.toByteArray();

                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.ms-excel"));
                headers.setContentDispositionFormData("attachment", "elenco_fornitori.xls");
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
