package it.tinna.smartdoc.server.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.aliquoteiva.AliquoteIvaDelegate;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

import it.tinna.smartdoc.server.delegate.aliquoteiva.AliquoteIvaDelegate;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
@RestController
@RequestMapping("/api/aliquoteiva")
public class AliquoteIvaController {

    @Autowired
    private AliquoteIvaDelegate aliquoteIvaDelegate;


    @PostMapping("/list")
    public ResponseEntity<GenericResponseDto<List<AliquotaIvaDto>>> getList(@RequestBody(required = false) AliquotaIvaDto filter, 
                                                                            @RequestParam(defaultValue = "0") int start, 
                                                                            @RequestParam(defaultValue = "10") int length,
                                                                            @RequestParam(required = false) Integer orderColumn, 
                                                                            @RequestParam(required = false) String orderDir) {
        GenericResponseDto<List<AliquotaIvaDto>> response = new GenericResponseDto<>();
        try {
            String search = filter != null ? filter.getDescrizione() : null;
            response.setPayload(aliquoteIvaDelegate.getList(search, length, start, orderColumn, orderDir));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/listForCombo")
    public ResponseEntity<GenericResponseDto<List<AliquotaIvaDto>>> getListForCombo(HttpServletRequest request) {
        GenericResponseDto<List<AliquotaIvaDto>> responseDto = new GenericResponseDto<>();
        try {
            responseDto.setPayload(aliquoteIvaDelegate.getListForCombo());
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(responseDto);
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<AliquotaIvaDto>> insert(@RequestBody AliquotaIvaDto dto) {
        GenericResponseDto<AliquotaIvaDto> response = new GenericResponseDto<>();
        try {
            if (aliquoteIvaDelegate.isExistent(dto.getDescrizione(), null)) {
                response.setErrorText("Aliquota IVA già esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer userId = ((it.tinna.smartdoc.server.security.UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            dto.setUserCreated(userId.longValue());
            aliquoteIvaDelegate.insert(dto);
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Void>> update(@PathVariable Integer id, @RequestBody AliquotaIvaDto dto) {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        try {
            dto.setId(id);
            if (aliquoteIvaDelegate.isExistent(dto.getDescrizione(), id)) {
                response.setErrorText("Aliquota IVA già esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer userId = ((it.tinna.smartdoc.server.security.UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            dto.setUserLastUpdate(userId.longValue());
            aliquoteIvaDelegate.update(dto);
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
            Integer userId = ((it.tinna.smartdoc.server.security.UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            aliquoteIvaDelegate.delete(id, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
