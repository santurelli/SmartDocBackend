package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import it.tinna.smartdoc.server.delegate.statistiche.StatisticheDelegate;
import it.tinna.smartdoc.server.delegate.documenti.FattureDelegate;
import it.tinna.smartdoc.shared.dto.statistiche.DatiGlobaliDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.FattureListResponse;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;

@RestController
@RequestMapping("/api/fatture")
public class FattureController {

    private final FattureDelegate fattureDelegate;
    private final StatisticheDelegate statisticheDelegate;

    @Autowired
    public FattureController(FattureDelegate fattureDelegate, StatisticheDelegate statisticheDelegate) {
        this.fattureDelegate = fattureDelegate;
        this.statisticheDelegate = statisticheDelegate;
    }

    @GetMapping
    public ResponseEntity<GenericResponseDto<it.tinna.smartdoc.shared.dto.documenti.FattureListResponse>> getList(
            @RequestParam(required = false) String dataInizio,
            @RequestParam(required = false) String dataFine,
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) Integer idAgente,
            @RequestParam(required = false, defaultValue = "data_fattura") String orderColumn,
            @RequestParam(required = false, defaultValue = "asc") String orderDir,
            @RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String statoFatturaElettronica,
            @RequestParam(required = false) String numDocumento,
            @RequestParam(required = false) String stato) throws SQLException {
        
        Integer orderColumnIdx = 1;
        if ("num_fattura".equals(orderColumn)) orderColumnIdx = 2;
        else if ("d_e_clienti.denominazione".equals(orderColumn)) orderColumnIdx = 3;
        else if ("data_fattura".equals(orderColumn)) orderColumnIdx = 1;
        
        it.tinna.smartdoc.shared.dto.documenti.FattureListResponse list = fattureDelegate.getList(tipo, idCliente, dataInizio, dataFine, idAgente, stato, statoFatturaElettronica, length, start, orderColumnIdx, orderDir, numDocumento);
        return ResponseEntity.ok(new GenericResponseDto<>(list, null));
    }

    @GetMapping("/ultime")
    public ResponseEntity<GenericResponseDto<List<it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto>>> getUltime() throws SQLException {
        List<it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto> list = fattureDelegate.getUltimeFatture();
        return ResponseEntity.ok(new GenericResponseDto<>(list, null));
    }

    @GetMapping("/statistiche-globali")
    public ResponseEntity<GenericResponseDto<DatiGlobaliDto>> getStatisticheGlobali() throws SQLException {
        DatiGlobaliDto dto = statisticheDelegate.getDatiGlobali();
        return ResponseEntity.ok(new GenericResponseDto<>(dto, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<FatturaDto>> getById(@PathVariable long id) throws SQLException {
        FatturaDto dto = fattureDelegate.getById(id);
        return ResponseEntity.ok(new GenericResponseDto<>(dto, null));
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<Long>> save(@RequestBody FatturaDto dto) throws SQLException {
        long id = fattureDelegate.insert(dto);
        return ResponseEntity.ok(new GenericResponseDto<>(id, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Boolean>> delete(@PathVariable long id, @RequestParam Long user) throws SQLException {
        FatturaDto dto = new FatturaDto();
        dto.setId(id);
        dto.setUserLastUpdate(user);
        fattureDelegate.delete(java.util.Collections.singletonList(dto));
        return ResponseEntity.ok(new GenericResponseDto<>(true, null));
    }

    @GetMapping("/nextNum")
    public ResponseEntity<GenericResponseDto<Integer>> getNextNum(
            @RequestParam String data,
            @RequestParam int flElettronica,
            @RequestParam String tipo) throws SQLException {
        Integer nextNum = fattureDelegate.getNextNumFattura(data, flElettronica, it.tinna.smartdoc.shared.dto.documenti.TipoFattura.valueOf(tipo));
        return ResponseEntity.ok(new GenericResponseDto<>(nextNum, null));
    }

    @GetMapping("/combos")
    public ResponseEntity<GenericResponseDto<Map<String, Object>>> getCombosMap(@RequestParam String tipo) throws SQLException {
        Map<String, Object> map = fattureDelegate.getCombosMap(tipo);
        return ResponseEntity.ok(new GenericResponseDto<>(map, null));
    }

    @GetMapping("/print/{id}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        try {
            DocumentoWrapperDto doc = fattureDelegate.esportaFatturaPdf(it.tinna.smartdoc.server.database.DatabaseContextHolder.getClientDatabase(), id);
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

    @PutMapping("/scadenze")
    public ResponseEntity<GenericResponseDto<Boolean>> updateScadenzaPagamento(@RequestBody ScadenzaPagamentoDocumentoDto dto) throws SQLException {
        fattureDelegate.updateScadenzaPagamento(dto);
        return ResponseEntity.ok(new GenericResponseDto<>(true, null));
    }
}

