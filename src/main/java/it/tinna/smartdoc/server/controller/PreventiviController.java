package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.core.io.ClassPathResource;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

//import it.tinna.smartdoc.server.constants.ISessionConstants;
import it.tinna.smartdoc.server.delegate.documenti.PreventiviDelegate;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.PreventivoDto;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/preventivi")
public class PreventiviController {

    @Autowired
    private PreventiviDelegate preventiviDelegate;

    @PostMapping("/list")
    public DatatablesResponseDto<MovimentiDocumentoDto> getList(@RequestBody Map<String, Object> p) throws SQLException {
        Integer start = (Integer) p.getOrDefault("start", 0);
        Integer length = (Integer) p.getOrDefault("length", 10);
        String orderColumn = (String) p.getOrDefault("orderColumn", "data_preventivo");
        String orderDir = (String) p.getOrDefault("orderDir", "asc");
        
        // Filters
        String dtFrom = (String) p.get("dtFrom");
        String dtTo = (String) p.get("dtTo");
        
        Object idClienteObj = p.get("idCliente");
        Integer idCliente = (idClienteObj != null && !"".equals(idClienteObj.toString())) ? Integer.valueOf(idClienteObj.toString()) : null;
        
        Object idAgenteObj = p.get("idAgente");
        Integer idAgente = (idAgenteObj != null && !"".equals(idAgenteObj.toString())) ? Integer.valueOf(idAgenteObj.toString()) : null;

        return preventiviDelegate.getList(idCliente, dtFrom, dtTo, idAgente, length, start, orderColumn, orderDir);
    }

    @GetMapping("/combos-map")
    public GenericResponseDto<Map<String, Object>> getCombosMap() throws SQLException {
        GenericResponseDto<Map<String, Object>> response = new GenericResponseDto<>();
        response.setPayload(preventiviDelegate.getCombosMap());
        return response;
    }

    @GetMapping("/{id}")
    public GenericResponseDto<PreventivoDto> getById(@PathVariable long id) throws SQLException {
        GenericResponseDto<PreventivoDto> response = new GenericResponseDto<>();
        response.setPayload(preventiviDelegate.getById(id));
        return response;
    }

    @GetMapping("/nextNum")
    public GenericResponseDto<String> getNextNum(@RequestParam String data) throws SQLException {
        GenericResponseDto<String> response = new GenericResponseDto<>();
        response.setPayload(preventiviDelegate.getNextNumPreventivo(data));
        return response;
    }

    @PostMapping
    public GenericResponseDto<Integer> insert(@RequestBody PreventivoDto dto, HttpServletRequest request) throws SQLException {
        GenericResponseDto<Integer> response = new GenericResponseDto<>();
        //UtenteDto user = (UtenteDto) request.getSession().getAttribute(ISessionConstants.USER_LOGGED_IN);
        UtenteDto user = null; // TODO: restore user from security context
        if (user != null) {
            dto.setUserCreated(user.getId());
        }
        response.setPayload(preventiviDelegate.insert(dto));
        return response;
    }

    @PutMapping("/{id}")
    public GenericResponseDto<Void> update(@PathVariable long id, @RequestBody PreventivoDto dto, HttpServletRequest request) throws SQLException {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        //UtenteDto user = (UtenteDto) request.getSession().getAttribute(ISessionConstants.USER_LOGGED_IN);
        UtenteDto user = null; // TODO: restore user from security context
        if (user != null) {
            dto.setUserLastUpdate(user.getId());
        }
        dto.setId(id);
        preventiviDelegate.update(dto);
        return response;
    }

    @DeleteMapping("/{id}")
    public GenericResponseDto<Void> delete(@PathVariable long id, HttpServletRequest request) throws SQLException {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        //UtenteDto user = (UtenteDto) request.getSession().getAttribute(ISessionConstants.USER_LOGGED_IN);
        UtenteDto user = null; // TODO: restore user from security context
        long userId = user != null ? user.getId() : 0;
        preventiviDelegate.delete(id, userId);
        return response;
    }

    @PostMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(@RequestBody Map<String, Object> p) {
        try {
            // Extract same params as list
            String dtFrom = (String) p.get("dtFrom");
            String dtTo = (String) p.get("dtTo");
            Object idClienteObj = p.get("idCliente");
            Integer idCliente = (idClienteObj != null && !"".equals(idClienteObj.toString())) ? Integer.valueOf(idClienteObj.toString()) : null;
            Object idAgenteObj = p.get("idAgente");
            Integer idAgente = (idAgenteObj != null && !"".equals(idAgenteObj.toString())) ? Integer.valueOf(idAgenteObj.toString()) : null;
            String orderColumn = (String) p.getOrDefault("orderColumn", "data_preventivo");
            String orderDir = (String) p.getOrDefault("orderDir", "asc");

            List<MovimentiDocumentoDto> list = preventiviDelegate.getList(idCliente, dtFrom, dtTo, idAgente, null, null, orderColumn, orderDir).getList();

            Context context = new Context();
            context.putVar("preventivi", list);

            ClassPathResource templateResource = new ClassPathResource("report/elenco_preventivi.xls");
            try (InputStream is = templateResource.getInputStream()) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JxlsHelper.getInstance().processTemplate(is, os, context);
                byte[] content = os.toByteArray();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
                headers.setContentDispositionFormData("attachment", "elenco_preventivi.xls");
                return ResponseEntity.ok().headers(headers).body(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/print/{id}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        try {
            it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto doc = preventiviDelegate.esportaPreventivoPdf(null, id);
            
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

