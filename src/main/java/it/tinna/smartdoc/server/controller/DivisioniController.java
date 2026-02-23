package it.tinna.smartdoc.server.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.divisioni.DivisioniDelegate;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/divisioni")
public class DivisioniController {

    @Autowired
    private DivisioniDelegate divisioniDelegate;

    @PostMapping("/listForCombo")
    public ResponseEntity<GenericResponseDto> getListForCombo(HttpServletRequest request) {
        GenericResponseDto responseDto = new GenericResponseDto();
        try {
            responseDto.setPayload(divisioniDelegate.getListForCombo());
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(responseDto);
        }
    }
}

