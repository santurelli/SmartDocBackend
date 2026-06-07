package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.tinna.smartdoc.server.delegate.documenti.NoteCreditoFornitoreDelegate;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoFornitoreDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;

import lombok.extern.slf4j.Slf4j;
import com.google.gson.Gson;

@RestController
@Slf4j
@RequestMapping("/api/note-credito-fornitore")
public class NoteCreditoFornitoreController {

    private final NoteCreditoFornitoreDelegate noteCreditoFornitoreDelegate;

    @Autowired
    public NoteCreditoFornitoreController(NoteCreditoFornitoreDelegate noteCreditoFornitoreDelegate) {
        this.noteCreditoFornitoreDelegate = noteCreditoFornitoreDelegate;
    }

    @GetMapping
    public ResponseEntity<GenericResponseDto<DatatablesResponseDto<NotaCreditoFornitoreDto>>> getList(
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
        
        DatatablesResponseDto<NotaCreditoFornitoreDto> list = noteCreditoFornitoreDelegate.getList(idFornitore, dtFrom, dtTo, numeroDocumento, dtRegistrazioneFrom, dtRegistrazioneTo, numeroRegistrazione, stato, length, start, orderColumn, orderDir);
        return ResponseEntity.ok(new GenericResponseDto<>(list, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<NotaCreditoFornitoreDto>> getById(@PathVariable long id) throws Exception {
        NotaCreditoFornitoreDto dto = noteCreditoFornitoreDelegate.getById(id);
        return ResponseEntity.ok(new GenericResponseDto<>(dto, null));
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<Integer>> save(@RequestBody NotaCreditoFornitoreDto dto) throws SQLException {
        try {
            if (dto.getId() > 0) {
                noteCreditoFornitoreDelegate.update(dto);
                return ResponseEntity.ok(new GenericResponseDto<>((int) dto.getId(), null));
            } else {
                Integer id = noteCreditoFornitoreDelegate.insert(dto);
                return ResponseEntity.ok(new GenericResponseDto<>(id, null));
            }
        } catch (Exception e) {
            log.error("Errore durante il salvataggio della nota credito fornitore: {}", new Gson().toJson(dto), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Boolean>> delete(@PathVariable long id) throws SQLException {
        NotaCreditoFornitoreDto dto = new NotaCreditoFornitoreDto();
        dto.setId(id);
        // Note: user info should be retrieved from security context
        noteCreditoFornitoreDelegate.delete(java.util.Collections.singletonList(dto));
        return ResponseEntity.ok(new GenericResponseDto<>(true, null));
    }

    @PutMapping("/scadenze")
    public ResponseEntity<GenericResponseDto<Boolean>> updateScadenzaPagamento(@RequestBody ScadenzaPagamentoDocumentoDto dto) throws SQLException {
        noteCreditoFornitoreDelegate.updateScadenzaPagamento(dto);
        return ResponseEntity.ok(new GenericResponseDto<>(true, null));
    }

    @GetMapping("/combos")
    public ResponseEntity<GenericResponseDto<Map<String, Object>>> getCombosMap() throws SQLException {
        Map<String, Object> map = noteCreditoFornitoreDelegate.getCombosMap();
        return ResponseEntity.ok(new GenericResponseDto<>(map, null));
    }

    @GetMapping("/nextNum")
    public ResponseEntity<GenericResponseDto<Integer>> getNextNum(
            @RequestParam String data) throws SQLException {
        Integer nextNum = noteCreditoFornitoreDelegate.getNextNum(data);
        return ResponseEntity.ok(new GenericResponseDto<>(nextNum, null));
    }
}
