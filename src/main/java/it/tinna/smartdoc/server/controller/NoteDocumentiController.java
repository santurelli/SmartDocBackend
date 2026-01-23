package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import it.tinna.smartdoc.server.delegate.notedocumenti.NoteDocumentiDelegate;
import it.tinna.smartdoc.shared.dto.notedocumenti.NotaDocumentoDto;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notedocumenti")
public class NoteDocumentiController {

    @Autowired
    private NoteDocumentiDelegate noteDocumentiDelegate;

    @GetMapping
    public ResponseEntity<List<NotaDocumentoDto>> getList() {
        try {
            return ResponseEntity.ok(noteDocumentiDelegate.getListForCombo());
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<NotaDocumentoDto> insert(@RequestBody NotaDocumentoDto dto) {
        try {
            if (noteDocumentiDelegate.isExistent(dto.getDescrizione(), null)) {
                 return ResponseEntity.badRequest().build(); 
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            noteDocumentiDelegate.insert(dto, userDetails.getId());
            
            return ResponseEntity.ok(dto);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody NotaDocumentoDto dto) {
        try {
            dto.setId(id);
             if (noteDocumentiDelegate.isExistent(dto.getDescrizione(), id)) {
                 return ResponseEntity.badRequest().build(); 
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            noteDocumentiDelegate.update(dto, userDetails.getId());
            
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
            noteDocumentiDelegate.delete(id, userDetails.getId());
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
