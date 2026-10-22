package it.tinna.smartdoc.server.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.marche.MarcheDelegate;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import it.tinna.smartdoc.shared.dto.marche.MarcaDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/marche")
@Slf4j
public class MarcheController {

    @Autowired
    private MarcheDelegate marcheDelegate;

    private Long getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                Integer uid = ((UserDetailsImpl) principal).getId();
                return uid != null ? uid.longValue() : 1L;
            }
        } catch (Exception ignored) { }
        return 1L;
    }

    @PostMapping("/list")
    public ResponseEntity<?> getList(@RequestBody Map<String, Object> params) {
        try {
            int start = (int) params.getOrDefault("start", 0);
            int length = (int) params.getOrDefault("length", 100);
            String search = (String) params.get("search");
            Integer orderCol = params.containsKey("orderColumn") ? (Integer) params.get("orderColumn") : null;
            String orderDir = (String) params.getOrDefault("orderDir", "asc");

            List<MarcaDto> list = marcheDelegate.getList(search, length, start, orderCol, orderDir);
            long total = list.isEmpty() ? 0 : list.get(0).getTotal();

            DatatablesResponseDto<MarcaDto> response = new DatatablesResponseDto<>();
            response.setList(list);
            response.setTotalCount(total);
            response.setTotalFiltered(total);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nel recupero lista marche", e);
            return ResponseEntity.internalServerError().body("Error fetching list: " + e.getMessage());
        }
    }

    @GetMapping("/combo")
    public ResponseEntity<GenericResponseDto<List<MarcaDto>>> getCombo() {
        GenericResponseDto<List<MarcaDto>> response = new GenericResponseDto<>();
        try {
            response.setPayload(marcheDelegate.getListForCombo());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nel recupero combo marche", e);
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/listForCombo")
    public ResponseEntity<GenericResponseDto<List<MarcaDto>>> getListForCombo() {
        return getCombo();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<MarcaDto>> getById(@PathVariable long id) {
        GenericResponseDto<MarcaDto> response = new GenericResponseDto<>();
        try {
            MarcaDto dto = marcheDelegate.getById(id);
            if (dto == null) {
                return ResponseEntity.notFound().build();
            }
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nel recupero marca id {}", id, e);
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<MarcaDto>> create(@RequestBody MarcaDto dto) {
        GenericResponseDto<MarcaDto> response = new GenericResponseDto<>();
        try {
            dto.setUserCreated(getCurrentUserId());
            long id = marcheDelegate.insert(dto);
            dto.setId(id);
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nella creazione marca", e);
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto<MarcaDto>> update(@PathVariable long id, @RequestBody MarcaDto dto) {
        GenericResponseDto<MarcaDto> response = new GenericResponseDto<>();
        try {
            dto.setId(id);
            dto.setUserLastUpdate(getCurrentUserId());
            marcheDelegate.update(dto);
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nell'aggiornamento marca id {}", id, e);
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Boolean>> delete(@PathVariable long id) {
        GenericResponseDto<Boolean> response = new GenericResponseDto<>();
        try {
            marcheDelegate.delete(id, getCurrentUserId());
            response.setPayload(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nell'eliminazione marca id {}", id, e);
            response.setErrorText(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
