package it.tinna.smartdoc.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import it.tinna.smartdoc.server.delegate.contabilita.RegistrazioneContabileDelegate;
import it.tinna.smartdoc.shared.dto.contabilita.RegistrazioneContabileDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/libro-giornale")
public class LibroGiornaleController {

    @Autowired
    private RegistrazioneContabileDelegate delegate;

    @GetMapping("/list")
    public GenericResponseDto list(@RequestParam(required = false) String tipoDocumento,
                                   @RequestParam(defaultValue = "") String search,
                                   @RequestParam(required = false) String dataDa,
                                   @RequestParam(required = false) String dataA) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            List<RegistrazioneContabileDto> list = delegate.getList(tipoDocumento, search, dataDa, dataA);
            response.setPayload(list);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf(@RequestParam(required = false) String tipoDocumento,
                                      @RequestParam(defaultValue = "") String search,
                                      @RequestParam(required = false) String dataDa,
                                      @RequestParam(required = false) String dataA) {
        try {
            DocumentoWrapperDto doc = delegate.esportaLibroGiornalePdf(tipoDocumento, search, dataDa, dataA);
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

    @PostMapping("/manuale")
    public GenericResponseDto inserisciManuale(@RequestBody RegistrazioneContabileDto dto) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            long id = delegate.inserisciManuale(dto);
            response.setPayload(id);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @PutMapping("/manuale/{id}")
    public GenericResponseDto aggiornaManuale(@PathVariable long id, @RequestBody RegistrazioneContabileDto dto) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            delegate.aggiornaManuale(id, dto);
            response.setPayload(true);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/manuale/{id}")
    public GenericResponseDto eliminaManuale(@PathVariable long id) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            delegate.eliminaManuale(id);
            response.setPayload(true);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }
}
