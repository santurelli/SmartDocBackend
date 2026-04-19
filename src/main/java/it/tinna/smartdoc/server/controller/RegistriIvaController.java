package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.registriiva.RegistriIvaDelegate;
import it.tinna.smartdoc.shared.constants.Periodi;
import it.tinna.smartdoc.shared.dto.documenti.RegistriIvaListResponse;

@RestController
@RequestMapping("/api/registri-iva")
public class RegistriIvaController {

    @Autowired
    private RegistriIvaDelegate registriIvaDelegate;

    @GetMapping("/anni")
    public List<Integer> getElencoAnniDocIva() throws SQLException {
        return registriIvaDelegate.getElencoAnniDocIva();
    }

    @GetMapping("/documenti")
    public RegistriIvaListResponse getListIvaDocumenti(
            @RequestParam String periodo,
            @RequestParam int anno) throws SQLException {
        
        Periodi p = Periodi.valueOf(periodo);
        // Default pagination to null, orderColumn 0, orderDir ASC for frontend simplicity
        return registriIvaDelegate.getListIvaDocumenti(p, anno, null, null, 0, "ASC");
    }

    @GetMapping("/export-pdf")
    public org.springframework.http.ResponseEntity<byte[]> exportPdf(
            @RequestParam String periodo,
            @RequestParam int anno,
            @RequestParam String tipoRegistro) {
        try {
            Periodi p = Periodi.valueOf(periodo);
            byte[] pdfBytes = registriIvaDelegate.exportRegistroIva(p, anno, tipoRegistro);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "registro_" + tipoRegistro + "_" + anno + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return org.springframework.http.ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }
}
