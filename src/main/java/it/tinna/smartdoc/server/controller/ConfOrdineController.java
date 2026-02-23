package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import it.tinna.smartdoc.server.delegate.documenti.ConfOrdineDelegate;
import it.tinna.smartdoc.shared.dto.documenti.ConfOrdineDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/conf-ordine")
public class ConfOrdineController {

    @Autowired
    private ConfOrdineDelegate confOrdineDelegate;

    @PostMapping("/list")
    public DatatablesResponseDto<MovimentiDocumentoDto> getList(@RequestBody Map<String, Object> p) throws SQLException {
        Integer start = (Integer) p.getOrDefault("start", 0);
        Integer length = (Integer) p.getOrDefault("length", 10);
        String orderColumn = (String) p.getOrDefault("orderColumn", "data_confordine");
        String orderDir = (String) p.getOrDefault("orderDir", "asc");
        
        // Filters
        String dtFrom = (String) p.get("dtFrom");
        String dtTo = (String) p.get("dtTo");
        
        Object idClienteObj = p.get("idCliente");
        Integer idCliente = (idClienteObj != null && !"".equals(idClienteObj.toString())) ? Integer.valueOf(idClienteObj.toString()) : null;
        
        Object idAgenteObj = p.get("idAgente");
        Integer idAgente = (idAgenteObj != null && !"".equals(idAgenteObj.toString())) ? Integer.valueOf(idAgenteObj.toString()) : null;
        return confOrdineDelegate.getList(idCliente, dtFrom, dtTo, idAgente, length, start, orderColumn, orderDir);
    }

    @GetMapping("/combos-map")
    public GenericResponseDto<Map<String, Object>> getCombosMap() throws SQLException {
        GenericResponseDto<Map<String, Object>> response = new GenericResponseDto<>();
        response.setPayload(confOrdineDelegate.getCombosMap());
        return response;
    }

    @GetMapping("/{id}")
    public GenericResponseDto<ConfOrdineDto> getById(@PathVariable long id) throws SQLException {
        GenericResponseDto<ConfOrdineDto> response = new GenericResponseDto<>();
        response.setPayload(confOrdineDelegate.getById(id));
        return response;
    }

    @GetMapping("/nextNum")
    public GenericResponseDto<String> getNextNum(@RequestParam String data) throws SQLException {
        GenericResponseDto<String> response = new GenericResponseDto<>();
        response.setPayload(confOrdineDelegate.getNextNum(data));
        return response;
    }

    @PostMapping
    public GenericResponseDto<Integer> insert(@RequestBody ConfOrdineDto dto, HttpServletRequest request) throws SQLException {
        GenericResponseDto<Integer> response = new GenericResponseDto<>();
        // TODO: get user from context
        response.setPayload(confOrdineDelegate.save(dto));
        return response;
    }

    @PutMapping("/{id}")
    public GenericResponseDto<Void> update(@PathVariable long id, @RequestBody ConfOrdineDto dto, HttpServletRequest request) throws SQLException {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        dto.setId(id);
        confOrdineDelegate.save(dto);
        return response;
    }

    @DeleteMapping("/{id}")
    public GenericResponseDto<Void> delete(@PathVariable long id, HttpServletRequest request) throws SQLException {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        // TODO: get user from context
        confOrdineDelegate.delete(id, 0L);
        return response;
    }

    @GetMapping("/print/{id}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        try {
            it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto doc = confOrdineDelegate.esportaConfOrdinePdf(null, id);
            
            if (doc == null || doc.getFlusso() == null) {
                return ResponseEntity.status(404).body("Errore nella generazione del documento".getBytes());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=" + (doc.getNome() != null ? doc.getNome() : "documento.pdf"));
            
            return ResponseEntity.ok().headers(headers).body(doc.getFlusso());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

