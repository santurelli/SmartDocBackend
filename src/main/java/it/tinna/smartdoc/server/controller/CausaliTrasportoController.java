package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.causalitrasporto.CausaliTrasportoDelegate;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import it.tinna.smartdoc.shared.dto.documenti.CausaleTrasportoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/causalitrasporto")
public class CausaliTrasportoController {

    @Autowired
    private CausaliTrasportoDelegate delegate;

    @GetMapping("/list")
    public DatatablesResponseDto<CausaleTrasportoDto> list(@RequestParam(required = false) String search,
                                                        @RequestParam int start,
                                                        @RequestParam int length,
                                                        @RequestParam(name = "order[0][column]", defaultValue = "0") int orderColumn,
                                                        @RequestParam(name = "order[0][dir]", defaultValue = "asc") String orderDir,
                                                        HttpServletRequest request) {
        DatatablesResponseDto<CausaleTrasportoDto> response = new DatatablesResponseDto<>();
        try {
            List<CausaleTrasportoDto> list = delegate.getList(search, length, start, orderColumn, orderDir);
            response.setList(list);
            if (list != null && !list.isEmpty()) {
                response.setTotalCount(list.get(0).getTotal());
                response.setTotalFiltered(list.get(0).getTotal());
            }
        } catch (SQLException e) {
            response.setError(e.getMessage());
        }
        return response;
    }

    @GetMapping("/combo")
    public List<CausaleTrasportoDto> combo(HttpServletRequest request) throws SQLException {
        return delegate.getListForCombo();
    }

    @PostMapping("/create")
    public GenericResponseDto create(@RequestParam String descrizione, @RequestParam(defaultValue = "0") int predefinita, HttpServletRequest request) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            CausaleTrasportoDto dto = new CausaleTrasportoDto();
            dto.setDescrizione(descrizione);
            dto.setPredefinita(predefinita);
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            delegate.insert(dto, userDetails.getId());
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @PostMapping("/update")
    public GenericResponseDto update(@RequestParam int id, @RequestParam String descrizione, @RequestParam(defaultValue = "0") int predefinita, HttpServletRequest request) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            CausaleTrasportoDto dto = new CausaleTrasportoDto();
            dto.setId(id);
            dto.setDescrizione(descrizione);
            dto.setPredefinita(predefinita);
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            delegate.update(dto, userDetails.getId());
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/delete")
    public GenericResponseDto delete(@RequestParam int id, HttpServletRequest request) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            delegate.delete(id, userDetails.getId());
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }
}
