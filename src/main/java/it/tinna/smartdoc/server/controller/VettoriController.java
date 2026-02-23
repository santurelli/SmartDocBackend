package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import it.tinna.smartdoc.server.delegate.vettori.VettoriDelegate;
import it.tinna.smartdoc.shared.dto.vettori.VettoreDto;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;

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

@RestController
@RequestMapping("/api/vettori")
public class VettoriController {

    @Autowired
    private VettoriDelegate vettoriDelegate;

    @GetMapping
    public ResponseEntity<DatatablesResponseDto<VettoreDto>> getList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer length,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false, name = "order[0][column]") Integer orderCol,
            @RequestParam(required = false, name = "order[0][dir]") String orderDir) {
        
        try {
            List<VettoreDto> list = vettoriDelegate.getList(search, length, start, orderCol, orderDir);
            DatatablesResponseDto<VettoreDto> response = new DatatablesResponseDto<>();
            response.setList(list);
            long total = (list != null && !list.isEmpty()) ? list.get(0).getTotal() : 0;
            response.setTotalCount(total);
            response.setTotalFiltered(total);
            
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/combo")
    public ResponseEntity<List<VettoreDto>> getListForCombo() {
        try {
            return ResponseEntity.ok(vettoriDelegate.getListForCombo());
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<VettoreDto> insert(@RequestBody VettoreDto dto) {
        try {
            if (vettoriDelegate.isExistent(dto.getDescrizione(), null)) {
                 return ResponseEntity.badRequest().build(); 
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            vettoriDelegate.insert(dto, userDetails.getId());
            
            return ResponseEntity.ok(dto);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody VettoreDto dto) {
        try {
            dto.setId(id);
             if (vettoriDelegate.isExistent(dto.getDescrizione(), id)) {
                 return ResponseEntity.badRequest().build(); 
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            vettoriDelegate.update(dto, userDetails.getId());
            
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
            vettoriDelegate.delete(id, userDetails.getId());
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

