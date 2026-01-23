package it.tinna.smartdoc.server.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import it.tinna.smartdoc.server.delegate.sottocategorie.SottoCategorieDelegate;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import it.tinna.smartdoc.shared.dto.sottocategorie.SottoCategoriaDto;
import it.tinna.smartdoc.server.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/sottocategorie")
public class SottoCategorieController {

    @Autowired
    private SottoCategorieDelegate sottoCategorieDelegate;

    @PostMapping("/list")
    public ResponseEntity<GenericResponseDto> getList(@RequestBody(required = false) SottoCategoriaDto filter,
                                                     @RequestParam(required = false) Integer length,
                                                     @RequestParam(required = false) Integer start,
                                                     @RequestParam(required = false, name = "order[0][column]") Integer orderCol,
                                                     @RequestParam(required = false, name = "order[0][dir]") String orderDir) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            Integer idCategoria = filter != null ? filter.getParentId() : null;
            String search = filter != null ? filter.getDescrizione() : null;
            response.setPayload(sottoCategorieDelegate.getList(idCategoria, search, length, start, orderCol, orderDir));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/listForCombo")
    public ResponseEntity<GenericResponseDto> getListForCombo(@RequestParam(required = false) Integer idCategoria, HttpServletRequest request) {
        GenericResponseDto responseDto = new GenericResponseDto();
        try {
            responseDto.setPayload(sottoCategorieDelegate.getListForCombo(idCategoria));
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(responseDto);
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto> insert(@RequestBody SottoCategoriaDto dto) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            if (sottoCategorieDelegate.isExistent(dto.getParentId(), dto.getDescrizione(), null)) {
                response.setErrorText("Sottocategoria già esistente per questa categoria");
                return ResponseEntity.ok(response);
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            sottoCategorieDelegate.insert(dto, userDetails.getId());
            
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto> update(@PathVariable Integer id, @RequestBody SottoCategoriaDto dto) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            dto.setId(id);
            if (sottoCategorieDelegate.isExistent(dto.getParentId(), dto.getDescrizione(), id)) {
                response.setErrorText("Sottocategoria già esistente per questa categoria");
                return ResponseEntity.ok(response);
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            sottoCategorieDelegate.update(dto, userDetails.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponseDto> delete(@PathVariable Integer id) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            sottoCategorieDelegate.delete(id, userDetails.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(response);
        }
    }
}
