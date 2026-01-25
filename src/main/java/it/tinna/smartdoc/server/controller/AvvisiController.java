package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.avvisi.AvvisiDelegate;
import it.tinna.smartdoc.shared.dto.avvisi.AvvisoDto;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/avvisi")
public class AvvisiController {

    @Autowired
    private AvvisiDelegate avvisiDelegate;

    @GetMapping
    public ResponseEntity<List<AvvisoDto>> getList() {
        try {
            // Simplified for combo usage mainly
            return ResponseEntity.ok(avvisiDelegate.getListForCombo());
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    @PostMapping
    public ResponseEntity<AvvisoDto> insert(@RequestBody AvvisoDto dto) {
        try {
            if (avvisiDelegate.isExistent(dto.getDescrizione(), null)) {
                 return ResponseEntity.badRequest().build(); 
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            avvisiDelegate.insert(dto, userDetails.getId());
            
            return ResponseEntity.ok(dto);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody AvvisoDto dto) {
        try {
            dto.setId(id);
             if (avvisiDelegate.isExistent(dto.getDescrizione(), id)) {
                 return ResponseEntity.badRequest().build(); 
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            avvisiDelegate.update(dto, userDetails.getId());
            
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

            avvisiDelegate.delete(id, userDetails.getId());
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
