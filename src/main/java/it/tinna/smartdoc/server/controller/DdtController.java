package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

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

import it.tinna.smartdoc.server.delegate.documenti.DdtDelegate;
import it.tinna.smartdoc.shared.dto.documenti.DdtDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import org.springframework.security.core.context.SecurityContextHolder;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import it.tinna.smartdoc.server.delegate.login.LoginDelegate;

@RestController
@RequestMapping("/api/ddt")
public class DdtController {

    @Autowired
    private DdtDelegate ddtDelegate;

    @Autowired
    private LoginDelegate loginDelegate;

    private UtenteDto getCurrentUser() throws SQLException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            String username = ((UserDetailsImpl) principal).getUsername();
            return loginDelegate.getUserByUsername(username);
        }
        return null;
    }

    @PostMapping("/list")
    public DatatablesResponseDto<MovimentiDocumentoDto> getList(@RequestBody Map<String, Object> p) throws SQLException {
        Integer start = (Integer) p.getOrDefault("start", 0);
        Integer length = (Integer) p.getOrDefault("length", 10);
        String orderColumn = (String) p.getOrDefault("orderColumn", "data_ddt");
        String orderDir = (String) p.getOrDefault("orderDir", "asc");
        
        String dtFrom = p.get("dataDa") != null ? (String) p.get("dataDa") : (String) p.get("dataDA");
        String dtTo = (String) p.get("dataA");
        
        Object idClienteObj = p.get("idCliente");
        Integer idCliente = (idClienteObj != null && !"".equals(idClienteObj.toString())) ? Integer.valueOf(idClienteObj.toString()) : null;
        
        Object idAgenteObj = p.get("idAgente");
        Integer idAgente = (idAgenteObj != null && !"".equals(idAgenteObj.toString())) ? Integer.valueOf(idAgenteObj.toString()) : null;

        Object idDocumentoObj = p.get("numDocumento");
        Integer idDocumento = (idDocumentoObj != null && !"".equals(idDocumentoObj.toString())) ? Integer.valueOf(idDocumentoObj.toString()) : null;

        return ddtDelegate.getList(idCliente, dtFrom, dtTo, idAgente, idDocumento, length, start, orderColumn, orderDir);
    }

    @GetMapping("/combos-map")
    public GenericResponseDto<Map<String, Object>> getCombosMap() throws SQLException {
        GenericResponseDto<Map<String, Object>> response = new GenericResponseDto<>();
        response.setPayload(ddtDelegate.getCombosMap());
        return response;
    }

    @GetMapping("/{id}")
    public GenericResponseDto<DdtDto> getById(@PathVariable long id) throws Exception {
        GenericResponseDto<DdtDto> response = new GenericResponseDto<>();
        response.setPayload(ddtDelegate.getById(id));
        return response;
    }

    @GetMapping("/nextNum")
    public GenericResponseDto<Integer> getNextNum(@RequestParam String data) throws SQLException {
        GenericResponseDto<Integer> response = new GenericResponseDto<>();
        response.setPayload(ddtDelegate.getNextNum(data));
        return response;
    }

    @PostMapping
    public GenericResponseDto<Long> insert(@RequestBody DdtDto dto) throws SQLException {
        GenericResponseDto<Long> response = new GenericResponseDto<>();
        UtenteDto user = getCurrentUser();
        if (user != null) {
            dto.setUserCreated(user.getId());
        }
        response.setPayload(ddtDelegate.insert(dto));
        return response;
    }

    @PutMapping("/{id}")
    public GenericResponseDto<Void> update(@PathVariable long id, @RequestBody DdtDto dto) throws SQLException {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        UtenteDto user = getCurrentUser();
        if (user != null) {
            dto.setUserLastUpdate(user.getId());
        }
        dto.setId(id);
        ddtDelegate.update(dto);
        return response;
    }

    @DeleteMapping("/{id}")
    public GenericResponseDto<Void> delete(@PathVariable long id) throws SQLException {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        UtenteDto user = getCurrentUser();
        long userId = user != null ? user.getId() : 0;
        ddtDelegate.delete(id, userId);
        return response;
    }

    @GetMapping("/print/{id}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        try {
            it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto doc = ddtDelegate.esportaDdtPdf(null, id);
            
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

