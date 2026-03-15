package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.speseincasso.SpeseIncassoDelegate;
import it.tinna.smartdoc.shared.dto.speseincasso.SpesaIncassoDto;

@RestController
@RequestMapping("/api/spese-incasso")
public class SpeseIncassoController {

    @Autowired
    private SpeseIncassoDelegate speseIncassoDelegate;

    @GetMapping("/combo")
    public ResponseEntity<it.tinna.smartdoc.shared.dto.response.GenericResponseDto<List<SpesaIncassoDto>>> getListForCombo() {
        it.tinna.smartdoc.shared.dto.response.GenericResponseDto<List<SpesaIncassoDto>> response = new it.tinna.smartdoc.shared.dto.response.GenericResponseDto<>();
        try {
            response.setPayload(speseIncassoDelegate.getList(null));
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<it.tinna.smartdoc.shared.dto.response.GenericResponseDto<List<SpesaIncassoDto>>> getList(@org.springframework.web.bind.annotation.RequestParam(required = false) String descrizione) {
        it.tinna.smartdoc.shared.dto.response.GenericResponseDto<List<SpesaIncassoDto>> response = new it.tinna.smartdoc.shared.dto.response.GenericResponseDto<>();
        try {
            response.setPayload(speseIncassoDelegate.getList(descrizione));
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @org.springframework.web.bind.annotation.PostMapping("/save")
    public ResponseEntity<it.tinna.smartdoc.shared.dto.response.GenericResponseDto<Void>> save(@org.springframework.web.bind.annotation.RequestBody SpesaIncassoDto dto) {
        it.tinna.smartdoc.shared.dto.response.GenericResponseDto<Void> response = new it.tinna.smartdoc.shared.dto.response.GenericResponseDto<>();
        try {
            Integer userId = ((it.tinna.smartdoc.server.security.UserDetailsImpl) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            if (dto.getId() != null) {
                dto.setUserLastUpdate(userId.longValue());
                speseIncassoDelegate.update(dto);
            } else {
                dto.setUserCreated(userId.longValue());
                speseIncassoDelegate.insert(dto);
            }
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/delete/{id}")
    public ResponseEntity<it.tinna.smartdoc.shared.dto.response.GenericResponseDto<Void>> delete(@org.springframework.web.bind.annotation.PathVariable Integer id) {
        it.tinna.smartdoc.shared.dto.response.GenericResponseDto<Void> response = new it.tinna.smartdoc.shared.dto.response.GenericResponseDto<>();
        try {
            Integer userId = ((it.tinna.smartdoc.server.security.UserDetailsImpl) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            speseIncassoDelegate.delete(userId, java.util.Collections.singletonList(id));
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
