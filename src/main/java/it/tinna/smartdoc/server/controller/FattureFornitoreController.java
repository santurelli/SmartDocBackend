package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.tinna.smartdoc.server.delegate.documenti.FattureFornitoreDelegate;
import it.tinna.smartdoc.shared.dto.documenti.FatturaFornitoreDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;

import lombok.extern.slf4j.Slf4j;
import com.google.gson.Gson;

@RestController
@Slf4j
@RequestMapping("/api/fatture-fornitore")
public class FattureFornitoreController {

    private final FattureFornitoreDelegate fattureFornitoreDelegate;

    @Autowired
    public FattureFornitoreController(FattureFornitoreDelegate fattureFornitoreDelegate) {
        this.fattureFornitoreDelegate = fattureFornitoreDelegate;
    }

    @GetMapping
    public ResponseEntity<GenericResponseDto<DatatablesResponseDto<FatturaFornitoreDto>>> getList(
            @RequestParam(required = false) Integer idFornitore,
            @RequestParam(required = false) String dtFrom,
            @RequestParam(required = false) String dtTo,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam(required = false) String dtRegistrazioneFrom,
            @RequestParam(required = false) String dtRegistrazioneTo,
            @RequestParam(required = false) Integer numeroRegistrazione,
            @RequestParam(required = false) String stato,
            @RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length,
            @RequestParam(required = false, defaultValue = "0") int orderColumn,
            @RequestParam(required = false, defaultValue = "asc") String orderDir) throws SQLException {
        
        DatatablesResponseDto<FatturaFornitoreDto> list = fattureFornitoreDelegate.getList(idFornitore, dtFrom, dtTo, numeroDocumento, dtRegistrazioneFrom, dtRegistrazioneTo, numeroRegistrazione, stato, length, start, orderColumn, orderDir);
        return ResponseEntity.ok(new GenericResponseDto<>(list, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<FatturaFornitoreDto>> getById(@PathVariable long id) throws Exception {
        FatturaFornitoreDto dto = fattureFornitoreDelegate.getById(id);
        return ResponseEntity.ok(new GenericResponseDto<>(dto, null));
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<Integer>> save(@RequestBody FatturaFornitoreDto dto) throws SQLException {
        try {
            Integer id = fattureFornitoreDelegate.insert(dto);
            return ResponseEntity.ok(new GenericResponseDto<>(id, null));
        } catch (Exception e) {
            log.error("Errore durante il salvataggio della fattura fornitore: {}", new Gson().toJson(dto), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Integer>> update(@PathVariable long id, @RequestBody FatturaFornitoreDto dto) throws SQLException {
        try {
            dto.setId(id);
            fattureFornitoreDelegate.update(dto);
            return ResponseEntity.ok(new GenericResponseDto<>((int) id, null));
        } catch (Exception e) {
            log.error("Errore durante l'aggiornamento della fattura fornitore {}: {}", id, new Gson().toJson(dto), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Boolean>> delete(@PathVariable long id) throws SQLException {
        FatturaFornitoreDto dto = new FatturaFornitoreDto();
        dto.setId(id);
        // Note: user info should be retrieved from security context
        fattureFornitoreDelegate.delete(java.util.Collections.singletonList(dto));
        return ResponseEntity.ok(new GenericResponseDto<>(true, null));
    }

    @PutMapping("/scadenze")
    public ResponseEntity<GenericResponseDto<Boolean>> updateScadenzaPagamento(@RequestBody ScadenzaPagamentoDocumentoDto dto) throws SQLException {
        fattureFornitoreDelegate.updateScadenzaPagamento(dto);
        return ResponseEntity.ok(new GenericResponseDto<>(true, null));
    }

    @GetMapping("/combos")
    public ResponseEntity<GenericResponseDto<Map<String, Object>>> getCombosMap() throws SQLException {
        Map<String, Object> map = fattureFornitoreDelegate.getCombosMap();
        return ResponseEntity.ok(new GenericResponseDto<>(map, null));
    }

    @GetMapping("/nextNum")
    public ResponseEntity<GenericResponseDto<Integer>> getNextNum(
            @RequestParam String data) throws SQLException {
        Integer nextNum = fattureFornitoreDelegate.getNextNum(data);
        return ResponseEntity.ok(new GenericResponseDto<>(nextNum, null));
    }
}
