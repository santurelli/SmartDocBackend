package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.HashMap;
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

//import it.tinna.smartdoc.server.constants.ISessionConstants;
import it.tinna.smartdoc.server.delegate.documenti.PreventiviDelegate;
import it.tinna.smartdoc.shared.dto.documenti.PreventivoDto;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/preventivi")
public class PreventiviController {

    @Autowired
    private PreventiviDelegate preventiviDelegate;

    @PostMapping("/list")
    public DatatablesResponseDto<PreventivoDto> getList(@RequestParam Map<String, String> p) throws SQLException {
        Integer start = Integer.valueOf(p.getOrDefault("start", "0"));
        Integer length = Integer.valueOf(p.getOrDefault("length", "10"));
        Integer orderColumn = Integer.valueOf(p.getOrDefault("orderColumn", "0"));
        String orderDir = p.getOrDefault("orderDir", "asc");
        
        // Filters
        String dtFrom = p.get("dtFrom");
        String dtTo = p.get("dtTo");
        Integer idCliente = p.get("idCliente") != null && !p.get("idCliente").isEmpty() ? Integer.valueOf(p.get("idCliente")) : null;
        Integer idAgente = p.get("idAgente") != null && !p.get("idAgente").isEmpty() ? Integer.valueOf(p.get("idAgente")) : null;

        return preventiviDelegate.getList(idCliente, dtFrom, dtTo, idAgente, length, start, orderColumn, orderDir);
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
}
