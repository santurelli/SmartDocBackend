package it.tinna.smartdoc.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.sceltearticolo.ScelteArticoloDelegate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import it.tinna.smartdoc.shared.dto.sceltearticolo.SceltaArticoloDto;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/sceltearticolo")
public class ScelteArticoloController {

    @Autowired
    private ScelteArticoloDelegate scelteArticoloDelegate;

    @PostMapping("/list")
    public ResponseEntity<GenericResponseDto<List<SceltaArticoloDto>>> getList(@RequestBody(required = false) SceltaArticoloDto filter) {
        GenericResponseDto<List<SceltaArticoloDto>> response = new GenericResponseDto<>();
        try {
            String search = filter != null ? filter.getDescrizione() : null;
            response.setPayload(scelteArticoloDelegate.getList(search, null, null, 0, "ASC"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/listForCombo")
    public ResponseEntity<GenericResponseDto> getListForCombo(HttpServletRequest request) {
        GenericResponseDto responseDto = new GenericResponseDto();
        try {
            responseDto.setPayload(scelteArticoloDelegate.getListForCombo());
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(responseDto);
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<SceltaArticoloDto>> insert(@RequestBody SceltaArticoloDto dto) {
        GenericResponseDto<SceltaArticoloDto> response = new GenericResponseDto<>();
        try {
            if (scelteArticoloDelegate.isExistent(dto.getDescrizione(), null)) {
                response.setErrorText("Scelta già esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer userId = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            scelteArticoloDelegate.insert(dto, userId);
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Void>> update(@PathVariable Integer id, @RequestBody SceltaArticoloDto dto) {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        try {
            dto.setId(id);
            if (scelteArticoloDelegate.isExistent(dto.getDescrizione(), id)) {
                response.setErrorText("Scelta già esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer userId = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            scelteArticoloDelegate.update(dto, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Void>> delete(@PathVariable Integer id) {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        try {
            Integer userId = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            scelteArticoloDelegate.delete(id, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
