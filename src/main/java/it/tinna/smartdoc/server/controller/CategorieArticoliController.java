package it.tinna.smartdoc.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

import it.tinna.smartdoc.server.delegate.categorie.CategorieDelegate;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.shared.dto.categorie.CategoriaDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/categorie-articoli")
public class CategorieArticoliController {

    @Autowired
    private CategorieDelegate categorieDelegate;

    @PostMapping("/list")
    public ResponseEntity<GenericResponseDto<List<CategoriaDto>>> getList(@RequestBody(required = false) CategoriaDto filter) {
        GenericResponseDto<List<CategoriaDto>> response = new GenericResponseDto<>();
        try {
            // Standard management list usually handles search/limit in a specific way. 
            // For categories, we follow the pattern: search on descrizione.
            String search = filter != null ? filter.getDescrizione() : null;
            response.setPayload(categorieDelegate.getList(search, null, null, 1, "ASC"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/listForCombo")
    public ResponseEntity<GenericResponseDto<List<CategoriaDto>>> getListForCombo(HttpServletRequest request) {
        GenericResponseDto<List<CategoriaDto>> responseDto = new GenericResponseDto<>();
        try {
            responseDto.setPayload(categorieDelegate.getListForCombo());
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(responseDto); // Matching legacy behavior for combo
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<CategoriaDto>> insert(@RequestBody CategoriaDto dto) {
        GenericResponseDto<CategoriaDto> response = new GenericResponseDto<>();
        try {
            if (categorieDelegate.isExistent(dto.getDescrizione(), null)) {
                response.setErrorText("Categoria già esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer userId = ((it.tinna.smartdoc.server.security.UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            categorieDelegate.insert(dto, userId);
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Void>> update(@PathVariable Integer id, @RequestBody CategoriaDto dto) {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        try {
            dto.setId(id);
            if (categorieDelegate.isExistent(dto.getDescrizione(), id)) {
                response.setErrorText("Categoria già esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer userId = ((it.tinna.smartdoc.server.security.UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            categorieDelegate.update(dto, userId);
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
            categorieDelegate.delete(id, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

