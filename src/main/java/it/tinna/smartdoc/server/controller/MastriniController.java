package it.tinna.smartdoc.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.contabilita.RegistrazioneContabileDelegate;
import it.tinna.smartdoc.shared.dto.contabilita.MastrinoDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/mastrini")
public class MastriniController {

    @Autowired
    private RegistrazioneContabileDelegate delegate;

    @GetMapping("/{idConto}")
    public GenericResponseDto get(@PathVariable long idConto,
                                  @RequestParam(required = false) String dataDa,
                                  @RequestParam(required = false) String dataA) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            MastrinoDto mastrino = delegate.getMastrino(idConto, dataDa, dataA);
            response.setPayload(mastrino);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @GetMapping("/{idConto}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable long idConto,
                                      @RequestParam(required = false) String dataDa,
                                      @RequestParam(required = false) String dataA) {
        try {
            DocumentoWrapperDto doc = delegate.esportaMastrinoPdf(idConto, dataDa, dataA);
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
