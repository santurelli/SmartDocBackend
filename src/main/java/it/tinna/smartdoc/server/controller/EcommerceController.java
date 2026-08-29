package it.tinna.smartdoc.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.ecommerce.EcommerceIntegrationDelegate;
import it.tinna.smartdoc.shared.dto.ecommerce.EcommerceConfigDto;
import it.tinna.smartdoc.shared.dto.ecommerce.EcommerceOrdineImportatoDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/ecommerce")
@Slf4j
public class EcommerceController {

    @Autowired
    private EcommerceIntegrationDelegate ecommerceDelegate;

    @GetMapping("/config")
    public GenericResponseDto<EcommerceConfigDto> getConfig() {
        GenericResponseDto<EcommerceConfigDto> response = new GenericResponseDto<>();
        response.setPayload(ecommerceDelegate.getConfig());
        return response;
    }

    @PutMapping("/config")
    public GenericResponseDto<Void> saveConfig(@RequestBody EcommerceConfigDto dto) {
        ecommerceDelegate.saveConfig(dto);
        return new GenericResponseDto<>();
    }

    @GetMapping("/log")
    public GenericResponseDto<List<EcommerceOrdineImportatoDto>> getLog() {
        GenericResponseDto<List<EcommerceOrdineImportatoDto>> response = new GenericResponseDto<>();
        response.setPayload(ecommerceDelegate.getLog());
        return response;
    }

    @PostMapping("/sync-now")
    public ResponseEntity<GenericResponseDto<Void>> syncNow() {
        try {
            ecommerceDelegate.sincronizza();
            return ResponseEntity.ok(new GenericResponseDto<>());
        } catch (Exception e) {
            log.error("Errore durante la sincronizzazione manuale e-commerce", e);
            GenericResponseDto<Void> response = new GenericResponseDto<>();
            response.setErrorText("Errore durante la sincronizzazione: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
