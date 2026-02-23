package it.tinna.smartdoc.server.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

import it.tinna.smartdoc.server.delegate.causalimovimentoarticoli.CausaliMovimentoArticoliDelegate;
import it.tinna.smartdoc.shared.dto.causalimovimenti.CausaleMovimentoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/causaliMovimentiArticoli")
public class CausaliMovimentoArticoliController {

    @Autowired
    private CausaliMovimentoArticoliDelegate delegate;

    @GetMapping("/list")
    public DatatablesResponseDto<CausaleMovimentoDto> list(@RequestParam String search,
                                     @RequestParam int start,
                                     @RequestParam int length,
                                     @RequestParam(name = "order[0][column]", defaultValue = "0") int orderColumn,
                                     @RequestParam(name = "order[0][dir]", defaultValue = "asc") String orderDir,
                                     HttpServletRequest request) {
        DatatablesResponseDto<CausaleMovimentoDto> response = new DatatablesResponseDto<>();
        try {
            List<CausaleMovimentoDto> list = delegate.getList(search, length, start, orderColumn, orderDir);
            response.setList(list);
            if (list != null && !list.isEmpty()) {
                response.setTotalCount(list.get(0).getTotal());
                response.setTotalFiltered(list.get(0).getTotal());
            }
        } catch (Exception e) {
            response.setError(e.getMessage());
        }
        return response;
    }

    @GetMapping("/suggestion")
    public List<CausaleMovimentoDto> suggestion(@RequestParam String q, HttpServletRequest request) {
        List<CausaleMovimentoDto> result = new ArrayList<>();
        try {
            result = delegate.getSuggestion(q);
             // Enrich with select-compatible structure if needed, or frontend handles it
            for (CausaleMovimentoDto dto : result) {
                List<String> tokens = new ArrayList<>();
                tokens.add(dto.getDescrizione());
                dto.setTokens(tokens);
                dto.setValue(dto.getDescrizione());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/create")
    public GenericResponseDto create(@RequestParam String descrizione, HttpServletRequest request) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            CausaleMovimentoDto dto = new CausaleMovimentoDto();
            dto.setDescrizione(descrizione);
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dto.setUserCreated(userDetails.getId().longValue());
            delegate.insert(dto);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @PostMapping("/update")
    public GenericResponseDto update(@RequestParam long id, @RequestParam String descrizione, HttpServletRequest request) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            CausaleMovimentoDto dto = new CausaleMovimentoDto();
            dto.setId((long)id);
            dto.setDescrizione(descrizione);
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dto.setUserLastUpdate(userDetails.getId().longValue());
            delegate.update(dto);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/delete")
    public GenericResponseDto delete(@RequestParam long id, HttpServletRequest request) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            List<Long> ids = new ArrayList<>();
            ids.add(id);
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            delegate.delete(userDetails.getId().longValue(), ids);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }
}


