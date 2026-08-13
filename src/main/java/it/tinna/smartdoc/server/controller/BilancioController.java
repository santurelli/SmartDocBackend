package it.tinna.smartdoc.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.contabilita.BilancioDelegate;
import it.tinna.smartdoc.shared.dto.contabilita.BilancioDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/bilancio")
public class BilancioController {

    @Autowired
    private BilancioDelegate delegate;

    @GetMapping("/{anno}")
    public GenericResponseDto get(@PathVariable int anno) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            BilancioDto bilancio = delegate.genera(anno);
            response.setPayload(bilancio);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @GetMapping("/{anno}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable int anno) {
        try {
            DocumentoWrapperDto doc = delegate.esportaPdf(anno);
            if (doc == null || doc.getFlusso() == null) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=" + doc.getNome());
            return ResponseEntity.ok().headers(headers).body(doc.getFlusso());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
