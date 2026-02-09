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

import it.tinna.smartdoc.server.delegate.formatiarticolo.FormatiArticoloDelegate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import it.tinna.smartdoc.shared.dto.formatiarticolo.FormatoArticoloDto;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/formatiarticolo")
public class FormatiArticoloController {

    @Autowired
    private FormatiArticoloDelegate formatiArticoloDelegate;

    @PostMapping("/list")
    public ResponseEntity<GenericResponseDto<List<FormatoArticoloDto>>> getList(@RequestBody(required = false) FormatoArticoloDto filter) {
        GenericResponseDto<List<FormatoArticoloDto>> response = new GenericResponseDto<>();
        try {
            String search = filter != null ? filter.getDescrizione() : null;
            response.setPayload(formatiArticoloDelegate.getList(search, null, null, 0, "ASC"));
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
            responseDto.setPayload(formatiArticoloDelegate.getListForCombo());
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(responseDto);
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<FormatoArticoloDto>> insert(@RequestBody FormatoArticoloDto dto) {
        GenericResponseDto<FormatoArticoloDto> response = new GenericResponseDto<>();
        try {
            if (formatiArticoloDelegate.isExistent(dto.getDescrizione(), null)) {
                response.setErrorText("Formato già esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer userId = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            formatiArticoloDelegate.insert(dto, userId);
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Void>> update(@PathVariable Integer id, @RequestBody FormatoArticoloDto dto) {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        try {
            dto.setId(id);
            if (formatiArticoloDelegate.isExistent(dto.getDescrizione(), id)) {
                response.setErrorText("Formato già esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer userId = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            formatiArticoloDelegate.update(dto, userId);
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
            formatiArticoloDelegate.delete(id, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
