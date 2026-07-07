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
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;
import it.tinna.smartdoc.server.delegate.login.LoginDelegate;

import lombok.extern.slf4j.Slf4j;
import com.google.gson.Gson;
import java.util.concurrent.CompletableFuture;
import it.tinna.smartdoc.server.batch.BatchScheduler;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;

@RestController
@Slf4j
@RequestMapping("/api/fatture")
public class FattureController {

    private final FattureDelegate fattureDelegate;
    private final StatisticheDelegate statisticheDelegate;
    private final LoginDelegate loginDelegate;

    @Autowired(required = false)
    private BatchScheduler batchScheduler;

    @Autowired
    private FatturaElettronicaDelegate fatturaElettronicaDelegate;

    @Autowired
    public FattureController(FattureDelegate fattureDelegate, StatisticheDelegate statisticheDelegate, LoginDelegate loginDelegate) {
        this.fattureDelegate = fattureDelegate;
        this.statisticheDelegate = statisticheDelegate;
        this.loginDelegate = loginDelegate;
    }

    private UtenteDto getCurrentUser() throws SQLException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            String username = ((UserDetailsImpl) principal).getUsername();
            return loginDelegate.getUserByUsername(username);
        }
        return null;
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
        try {
            if (dto.getId() > 0) {
                UtenteDto user = getCurrentUser();
                if (user != null) {
                    dto.setUserLastUpdate(user.getId());
                }
                fattureDelegate.update(user, dto);
                return ResponseEntity.ok(new GenericResponseDto<>(dto.getId(), null));
            } else {
                UtenteDto user = getCurrentUser();
                if (user != null) {
                    dto.setUserCreated(user.getId());
                }
                long id = fattureDelegate.insert(dto);
                return ResponseEntity.ok(new GenericResponseDto<>(id, null));
            }
        } catch (Exception e) {
            log.error("Errore durante il salvataggio della fattura: {}", new Gson().toJson(dto), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Boolean>> delete(@PathVariable long id) throws SQLException {
        UtenteDto user = getCurrentUser();
        FatturaDto dto = new FatturaDto();
        dto.setId(id);
        if (user != null) {
            dto.setUserLastUpdate(user.getId());
        }
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
    public ResponseEntity<GenericResponseDto<Map<String, Object>>> getCombosMap(@RequestParam(required = false, defaultValue = "FATTURA") String tipo) throws SQLException {
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

    @PutMapping("/{id}/send-sdi")
    public ResponseEntity<GenericResponseDto<Boolean>> sendSdi(@PathVariable long id) throws SQLException {
        fattureDelegate.sendToSdi(id);
        if (batchScheduler != null) {
            String dbKey = DatabaseContextHolder.getClientDatabase();
            try {
                fatturaElettronicaDelegate.resetStatoInvioFattura(dbKey, id);
            } catch (SQLException e) {
                log.warn("Errore nel reset stato invio fattura {}: {}", id, e.getMessage());
            }
            CompletableFuture.runAsync(() -> batchScheduler.runInvioFatture(dbKey, new long[]{ id }));
        }
        return ResponseEntity.ok(new GenericResponseDto<>(true, null));
    }

    @PostMapping("/import-xml")
    public ResponseEntity<GenericResponseDto<Long>> importXml(@RequestParam("file") MultipartFile file) throws Exception {
        Long id = fattureDelegate.importXml(file);
        return ResponseEntity.ok(new GenericResponseDto<>(id, null));
    }

    @GetMapping("/download-xml/{id}")
    public ResponseEntity<byte[]> downloadXml(@PathVariable long id) {
        try {
            FatturaElettronicaWrapperDto doc = fattureDelegate.getFatturaElettronica(id);
            if (doc == null || doc.getFlussoFatturaElettronica() == null) return ResponseEntity.notFound().build();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            String filename = doc.getFattura() != null && doc.getFattura().getNomeFileFattura() != null ? 
                             doc.getFattura().getNomeFileFattura() : "fattura_" + id;
            if (!filename.toLowerCase().endsWith(".xml")) filename += ".xml";
            
            headers.add("Content-Disposition", "attachment; filename=" + filename);
            
            return ResponseEntity.ok().headers(headers).body(doc.getFlussoFatturaElettronica());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

