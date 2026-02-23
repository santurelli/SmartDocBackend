package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.agenti.AgentiDelegate;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import it.tinna.smartdoc.shared.dto.agenti.AgenteDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;

@RestController
@RequestMapping("/api/agenti")
public class AgentiController {

    @Autowired
    private AgentiDelegate agentiDelegate;

    @GetMapping
    public ResponseEntity<DatatablesResponseDto<AgenteDto>> getList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer length,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false, name = "order[0][column]") Integer orderCol,
            @RequestParam(required = false, name = "order[0][dir]") String orderDir) {
        
        try {
            List<AgenteDto> list = agentiDelegate.getList(search, length, start, orderCol, orderDir);
            DatatablesResponseDto<AgenteDto> response = new DatatablesResponseDto<>();
            response.setList(list);
            long total = (list != null && !list.isEmpty()) ? list.get(0).getTotal() : 0;
            response.setTotalCount(total);
            response.setTotalFiltered(total);
            
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgenteDto> getById(@PathVariable Integer id) {
        try {
            AgenteDto dto = agentiDelegate.getById(id);
            if (dto == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(dto);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> insert(@RequestBody AgenteDto dto) {
        try {
            if (agentiDelegate.isExistent(dto.getDenominazione(), null)) {
                return ResponseEntity.badRequest().body("Agente già esistente");
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            dto.setUserCreated(userDetails.getId().longValue());
            agentiDelegate.insert(dto);
            
            return ResponseEntity.ok(dto);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AgenteDto dto) {
        try {
            dto.setId(id);
            if (agentiDelegate.isExistent(dto.getDenominazione(), id)) {
                return ResponseEntity.badRequest().body("Agente già esistente");
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            dto.setUserLastUpdate(userDetails.getId().longValue());
            agentiDelegate.update(dto);
            
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            agentiDelegate.delete(userDetails.getId().longValue(), Collections.singletonList(id.longValue()));
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/suggestion")
    public ResponseEntity<List<AgenteDto>> getSuggestion(@RequestParam String q) {
        try {
            return ResponseEntity.ok(agentiDelegate.getSuggestion(q));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

