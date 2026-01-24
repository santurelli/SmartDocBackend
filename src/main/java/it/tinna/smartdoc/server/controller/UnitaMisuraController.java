package it.tinna.smartdoc.server.controller;

import it.tinna.smartdoc.server.delegate.unitamisura.UnitaMisuraDelegate;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import it.tinna.smartdoc.shared.dto.unitamisura.UnitaMisuraDto;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unitamisura")
public class UnitaMisuraController {

    @Autowired
    private UnitaMisuraDelegate unitaMisuraDelegate;


    @PostMapping("/list")
    public ResponseEntity<GenericResponseDto<List<UnitaMisuraDto>>> getList(@RequestBody(required = false) UnitaMisuraDto filter, 
                                                                            @RequestParam(defaultValue = "0") int start, 
                                                                            @RequestParam(defaultValue = "10") int length,
                                                                            @RequestParam(required = false) Integer orderColumn, 
                                                                            @RequestParam(required = false) String orderDir) {
        GenericResponseDto<List<UnitaMisuraDto>> response = new GenericResponseDto<>();
        try {
            String search = filter != null ? filter.getDescrizione() : null;
            response.setPayload(unitaMisuraDelegate.getList(search, length, start, orderColumn, orderDir));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/listForCombo")
    public ResponseEntity<GenericResponseDto<List<UnitaMisuraDto>>> getListForCombo(HttpServletRequest request) {
        GenericResponseDto<List<UnitaMisuraDto>> responseDto = new GenericResponseDto<>();
        try {
            responseDto.setPayload(unitaMisuraDelegate.getListForCombo());
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.ok(responseDto);
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<UnitaMisuraDto>> insert(@RequestBody UnitaMisuraDto dto) {
        GenericResponseDto<UnitaMisuraDto> response = new GenericResponseDto<>();
        try {
            if (unitaMisuraDelegate.isExistent(dto.getDescrizione(), null)) {
                response.setErrorText("Unità di misura già esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer userId = ((it.tinna.smartdoc.server.security.UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            dto.setUserCreated(userId.longValue());
            unitaMisuraDelegate.insert(dto);
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Void>> update(@PathVariable Integer id, @RequestBody UnitaMisuraDto dto) {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        try {
            dto.setId(id);
            if (unitaMisuraDelegate.isExistent(dto.getDescrizione(), id)) {
                response.setErrorText("Unità di misura già esistente");
                return ResponseEntity.badRequest().body(response);
            }

            Integer userId = ((it.tinna.smartdoc.server.security.UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            dto.setUserLastUpdate(userId.longValue());
            unitaMisuraDelegate.update(dto);
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
            unitaMisuraDelegate.delete(id, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText(ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
