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

import it.tinna.smartdoc.server.delegate.documenti.NoteCreditoDelegate;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.FattureListResponse;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/note-credito")
public class NoteCreditoController {

    private final NoteCreditoDelegate noteCreditoDelegate;

    @Autowired
    public NoteCreditoController(NoteCreditoDelegate noteCreditoDelegate) {
        this.noteCreditoDelegate = noteCreditoDelegate;
    }

    @GetMapping
    public ResponseEntity<GenericResponseDto<FattureListResponse>> getList(
            @RequestParam(required = false) String dataInizio,
            @RequestParam(required = false) String dataFine,
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) Integer idAgente,
            @RequestParam(required = false, defaultValue = "1") Integer orderColumn,
            @RequestParam(required = false, defaultValue = "asc") String orderDir,
            @RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length,
            @RequestParam(required = false) String numDocumento,
            @RequestParam(required = false) String stato) throws SQLException {
        
        FattureListResponse list = noteCreditoDelegate.getList(idCliente, dataInizio, dataFine, idAgente, stato, length, start, orderColumn, orderDir);
        return ResponseEntity.ok(new GenericResponseDto<>(list, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<NotaCreditoDto>> getById(@PathVariable long id) throws SQLException {
        NotaCreditoDto dto = noteCreditoDelegate.getById(id);
        return ResponseEntity.ok(new GenericResponseDto<>(dto, null));
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<Long>> save(@RequestBody NotaCreditoDto dto) throws SQLException {
        long id = noteCreditoDelegate.insert(dto);
        return ResponseEntity.ok(new GenericResponseDto<>(id, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Boolean>> delete(@PathVariable long id, @RequestParam Long user) throws SQLException {
        NotaCreditoDto dto = new NotaCreditoDto();
        dto.setId(id);
        dto.setUserLastUpdate(user);
        noteCreditoDelegate.delete(java.util.Collections.singletonList(dto));
        return ResponseEntity.ok(new GenericResponseDto<>(true, null));
    }

    @GetMapping("/nextNum")
    public ResponseEntity<GenericResponseDto<Integer>> getNextNum(
            @RequestParam String data,
            @RequestParam int flElettronica) throws SQLException {
        Integer nextNum = noteCreditoDelegate.getNextNumNotaCredito(data, flElettronica);
        return ResponseEntity.ok(new GenericResponseDto<>(nextNum, null));
    }

    @GetMapping("/combos")
    public ResponseEntity<GenericResponseDto<Map<String, Object>>> getCombosMap() throws SQLException {
        Map<String, Object> map = noteCreditoDelegate.getCombosMap();
        return ResponseEntity.ok(new GenericResponseDto<>(map, null));
    }

    @GetMapping("/print/{id}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        try {
            DocumentoWrapperDto doc = noteCreditoDelegate.esportaNotaCreditoPdf(it.tinna.smartdoc.server.database.DatabaseContextHolder.getClientDatabase(), id);
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

