package it.tinna.smartdoc.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.contabilita.ChiusuraEsercizioDelegate;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import it.tinna.smartdoc.shared.dto.contabilita.ChiusuraEsercizioPreviewDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/chiusura-esercizio")
public class ChiusuraEsercizioController {

    @Autowired
    private ChiusuraEsercizioDelegate delegate;

    @GetMapping("/{anno}/anteprima")
    public GenericResponseDto anteprima(@PathVariable int anno) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            ChiusuraEsercizioPreviewDto preview = delegate.anteprima(anno);
            response.setPayload(preview);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @PostMapping("/{anno}/chiudi")
    public GenericResponseDto chiudi(@PathVariable int anno) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = userDetails.getId() != null ? userDetails.getId().longValue() : null;
            ChiusuraEsercizioPreviewDto result = delegate.chiudi(anno, userId);
            response.setPayload(result);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }
}
