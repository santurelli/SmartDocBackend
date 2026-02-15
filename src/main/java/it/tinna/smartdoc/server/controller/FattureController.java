package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.tinna.smartdoc.server.delegate.documenti.FattureDelegate;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/fatture")
public class FattureController {

    private final FattureDelegate fattureDelegate;

    @Autowired
    public FattureController(FattureDelegate fattureDelegate) {
        this.fattureDelegate = fattureDelegate;
    }

    @GetMapping
    public ResponseEntity<GenericResponseDto<List<FatturaDto>>> getList(
            @RequestParam(required = false) String dataInizio,
            @RequestParam(required = false) String dataFine,
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) Integer idAgente,
            @RequestParam(required = false, defaultValue = "id_documento") String orderColumn,
            @RequestParam(required = false, defaultValue = "DESC") String orderDir,
            @RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String numDocumento,
            @RequestParam(required = false) String stato) throws SQLException {
        
        List<FatturaDto> list = fattureDelegate.getList(dataInizio, dataFine, idCliente, idAgente, orderColumn, orderDir, start, length, tipo, stato, numDocumento);
        return ResponseEntity.ok(new GenericResponseDto<>(list, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<FatturaDto>> getById(@PathVariable long id) throws SQLException {
        FatturaDto dto = fattureDelegate.getById(id);
        return ResponseEntity.ok(new GenericResponseDto<>(dto, null));
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<Long>> save(@RequestBody FatturaDto dto) throws SQLException {
        long id = fattureDelegate.save(dto);
        return ResponseEntity.ok(new GenericResponseDto<>(id, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Boolean>> delete(@PathVariable long id, @RequestParam Long user) throws SQLException {
        FatturaDto dto = new FatturaDto();
        dto.setId(id);
        dto.setUserLastUpdate(user);
        fattureDelegate.delete(dto);
        return ResponseEntity.ok(new GenericResponseDto<>(true, null));
    }

    @GetMapping("/nextNum")
    public ResponseEntity<GenericResponseDto<Integer>> getNextNum(
            @RequestParam String data,
            @RequestParam int flElettronica,
            @RequestParam String tipo) throws SQLException {
        Integer nextNum = fattureDelegate.getNextNum(data, flElettronica, tipo);
        return ResponseEntity.ok(new GenericResponseDto<>(nextNum, null));
    }

    @GetMapping("/combos")
    public ResponseEntity<GenericResponseDto<Map<String, Object>>> getCombosMap() throws SQLException {
        Map<String, Object> map = fattureDelegate.getCombosMap();
        return ResponseEntity.ok(new GenericResponseDto<>(map, null));
    }

    @GetMapping("/print/{id}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        try {
            DocumentoWrapperDto doc = fattureDelegate.esportaFatturaPdf(id);
            if (doc == null || doc.getFlusso() == null) return ResponseEntity.notFound().build();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=" + doc.getNome());
            
            return ResponseEntity.ok().headers(headers).body(doc.getFlusso());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
