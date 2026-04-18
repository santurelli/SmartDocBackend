package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import it.tinna.smartdoc.server.delegate.tipipagamento.TipiPagamentoDelegate;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;

import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.shared.dto.tipipagamento.ModalitaSdiDto;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tipi-pagamento")
public class TipiPagamentoController {

    private static final Logger log = LoggerFactory.getLogger(TipiPagamentoController.class);

    @Autowired
    private TipiPagamentoDelegate tipiPagamentoDelegate;

    @GetMapping
    public ResponseEntity<DatatablesResponseDto<TipoPagamentoDto>> getList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer length,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false, name = "order[0][column]") Integer orderCol,
            @RequestParam(required = false, name = "order[0][dir]") String orderDir) {
        
        try {
            List<TipoPagamentoDto> list = tipiPagamentoDelegate.getList(search, length, start, orderCol, orderDir);
            DatatablesResponseDto<TipoPagamentoDto> response = new DatatablesResponseDto<>();
            response.setList(list);
            long total = (list != null && !list.isEmpty()) ? list.get(0).getTotal() : 0;
            response.setTotalCount(total);
            response.setTotalFiltered(total);
            
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoPagamentoDto> getById(@PathVariable Long id) {
        try {
            TipoPagamentoDto dto = tipiPagamentoDelegate.getById(id.intValue());
            if (dto != null) {
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.notFound().build();
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody TipoPagamentoDto dto) {
        try {
            if (tipiPagamentoDelegate.isExistent(dto.getDescrizione(), (int) dto.getId())) {
                return ResponseEntity.badRequest().body("Un tipo pagamento con questa descrizione esiste già");
            }
            dto.setUserCreated(1L); // User 1 for now
            tipiPagamentoDelegate.insert(dto);
            return ResponseEntity.ok(dto);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody TipoPagamentoDto dto) {
        try {
            dto.setId(id);
            if (tipiPagamentoDelegate.isExistent(dto.getDescrizione(), (int) dto.getId())) {
                return ResponseEntity.badRequest().body("Un tipo pagamento con questa descrizione esiste già");
            }
            dto.setUserLastUpdate(1L); // User 1 for now
            tipiPagamentoDelegate.update(dto);
            return ResponseEntity.ok(dto);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            tipiPagamentoDelegate.delete(1L, List.of(id));
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/combo")
    public ResponseEntity<List<TipoPagamentoDto>> getListForCombo() {
        try {
            return ResponseEntity.ok(tipiPagamentoDelegate.getListForCombo());
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/scadenze-documento")
    public ResponseEntity<List<it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto>> getScadenzeDocumento(
            @PathVariable Integer id,
            @RequestParam String dataDocumento,
            @RequestParam java.math.BigDecimal totaleDocumento) {
        try {
            return ResponseEntity.ok(tipiPagamentoDelegate.getScadenzeDocumento(dataDocumento, id, totaleDocumento));
        } catch (Exception e) {
            log.error("Errore nel calcolo delle scadenze documento per tipo pagamento {} con data {} e totale {}", id, dataDocumento, totaleDocumento, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/modalita-sdi")
    public ResponseEntity<List<ModalitaSdiDto>> getModalitaSdi() {
        List<ModalitaSdiDto> list = Arrays.stream(ModalitaPagamentoEnum.values())
                .map(m -> ModalitaSdiDto.builder()
                        .nome(m.name())
                        .descrizione(m.getDescrizione())
                        .codiceSdi(m.getCodiceSdi())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}

