package it.tinna.smartdoc.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.cassa.PrevisioneCassaDelegate;
import it.tinna.smartdoc.shared.dto.cassa.PrevisioneCassaDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/previsione-cassa")
public class PrevisioneCassaController {

    @Autowired
    private PrevisioneCassaDelegate delegate;

    @GetMapping
    public GenericResponseDto<PrevisioneCassaDto> get() {
        GenericResponseDto<PrevisioneCassaDto> response = new GenericResponseDto<>();
        try {
            response.setPayload(delegate.calcola());
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }
}
