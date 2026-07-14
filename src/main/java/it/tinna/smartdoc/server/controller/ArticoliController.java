package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.prodotti.ProdottiDelegate;
import it.tinna.smartdoc.shared.dto.prodotti.PrezzoProdottoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import it.tinna.smartdoc.server.security.JwtService;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/articoli")
@Slf4j
public class ArticoliController {

    @Autowired
    private ProdottiDelegate prodottiDelegate;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/list")
    public ResponseEntity<?> getList(@RequestBody Map<String, Object> params) {
        try {
            // Extract Datatable params
            int draw = (int) params.getOrDefault("draw", 1);
            int start = (int) params.getOrDefault("start", 0);
            int length = (int) params.getOrDefault("length", 10);
            String search = (String) params.get("search");
            String categoria = (String) params.get("categoria");
            int orderColumn = (int) params.getOrDefault("orderColumn", 0);
            String orderDir = (String) params.getOrDefault("orderDir", "asc");

            Object giacenzaObj = params.get("giacenza");
            Double giacenza = (giacenzaObj != null && !"".equals(giacenzaObj.toString())) ? Double.parseDouble(giacenzaObj.toString()) : null;
            
            String operatoreGiacenza = (String) params.get("operatoreGiacenza");
            
            Object idFornitoreObj = params.get("idFornitore");
            Integer idFornitore = (idFornitoreObj != null && !"".equals(idFornitoreObj.toString())) ? Integer.parseInt(idFornitoreObj.toString()) : null;
            
            Object idTonoObj = params.get("idTono");
            Integer idTono = (idTonoObj != null && !"".equals(idTonoObj.toString())) ? Integer.parseInt(idTonoObj.toString()) : null;
            
            Object idCalibroObj = params.get("idCalibro");
            Integer idCalibro = (idCalibroObj != null && !"".equals(idCalibroObj.toString())) ? Integer.parseInt(idCalibroObj.toString()) : null;

            Object idFormatoObj = params.get("idFormato");
            Integer idFormato = (idFormatoObj != null && !"".equals(idFormatoObj.toString())) ? Integer.parseInt(idFormatoObj.toString()) : null;

            Object idSceltaObj = params.get("idScelta");
            Integer idScelta = (idSceltaObj != null && !"".equals(idSceltaObj.toString())) ? Integer.parseInt(idSceltaObj.toString()) : null;

            List<ProdottoDto> list = prodottiDelegate.getList(categoria, search, length, start, orderColumn, orderDir, giacenza, operatoreGiacenza, idFornitore, idTono, idCalibro, idFormato, idScelta);
            long total = list.isEmpty() ? 0 : list.get(0).getTotal();

            DatatablesResponseDto<ProdottoDto> response = new DatatablesResponseDto<>();
            response.setList(list);
            response.setTotalCount(total);
            response.setTotalFiltered(total);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nel recupero lista articoli", e);
            return ResponseEntity.internalServerError().body("Error fetching list: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable long id) {
        try {
            ProdottoDto dto = prodottiDelegate.getById(id);
            if (dto != null) {
                GenericResponseDto<ProdottoDto> response = new GenericResponseDto<>();
                response.setPayload(dto);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error fetching by id: " + e.getMessage());
        }
    }

    @PostMapping("/check-code")
    public ResponseEntity<?> checkCode(@RequestBody Map<String, Object> params) {
         try {
            String codice = (String) params.get("codice");
            Integer id = params.containsKey("id") ? (Integer) params.get("id") : null;
            boolean exists = prodottiDelegate.isExistentCodice(codice, id);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error checking code");
        }
    }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProdottoDto dto) {
        try {
            // Auto-generate code if empty
            if (dto.getCodice() == null || dto.getCodice().isEmpty()) {
                dto.setCodice(prodottiDelegate.getProssimoCodice());
            } else {
                // Check duplicate if manually entered
                if (prodottiDelegate.isExistentCodice(dto.getCodice(), null)) {
                    return ResponseEntity.badRequest().body("Codice già esistente");
                }
            }

            long id = prodottiDelegate.insert(dto);
            GenericResponseDto<Long> response = new GenericResponseDto<>();
            response.setPayload(id);
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            log.error("Errore nella creazione dell'articolo: {}", dto.getCodice(), e);
            return ResponseEntity.internalServerError().body("Error creating article: " + e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody ProdottoDto dto) {
        try {
             // Check duplicate if code changed (though UI usually disables code edit)
            if (dto.getCodice() != null && !dto.getCodice().isEmpty()) {
                 if (prodottiDelegate.isExistentCodice(dto.getCodice(), (int)dto.getId())) {
                    return ResponseEntity.badRequest().body("Codice già esistente");
                }
            }
            
            prodottiDelegate.update(dto);
            return ResponseEntity.ok(new GenericResponseDto<Void>());
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error updating article: " + e.getMessage());
        }
    }

    @GetMapping("/next-code")
    public ResponseEntity<?> getNextCode() {
        try {
            String code = prodottiDelegate.getProssimoCodice();
            return ResponseEntity.ok(Map.of("codice", code));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error generating code");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        GenericResponseDto<Void> response = new GenericResponseDto<>();
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer userId = userDetails.getId();
            prodottiDelegate.delete(id, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setErrorText("Error deleting article: " + ExceptionUtils.getMessage(e));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/price")
    public ResponseEntity<?> getPrice(@RequestBody Map<String, Object> params) {
        try {
            Long idProdotto = params.get("idProdotto") != null ? Long.parseLong(params.get("idProdotto").toString()) : null;
            Long idListino = params.get("idListino") != null && !params.get("idListino").toString().isEmpty() ? Long.parseLong(params.get("idListino").toString()) : null;

            if (idProdotto == null) {
                return ResponseEntity.badRequest().body("idProdotto is required");
            }

            Double prezzo = prodottiDelegate.getPrezzoDocumento(idProdotto, idListino);
            return ResponseEntity.ok(Map.of("prezzo", prezzo));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error fetching price: " + e.getMessage());
        }
    }
    @GetMapping("/{id}/prezzi")
    public ResponseEntity<?> getPrezzi(@PathVariable long id) {
        try {
            List<PrezzoProdottoDto> list = prodottiDelegate.getPrezzi(id);
            GenericResponseDto<List<PrezzoProdottoDto>> response = new GenericResponseDto<>();
            response.setPayload(list);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error fetching prices: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/prezzi")
    public ResponseEntity<?> savePrezzi(@PathVariable long id, @RequestBody List<PrezzoProdottoDto> prezzi) {
        try {
            prodottiDelegate.savePrezzi(id, prezzi);
            return ResponseEntity.ok(new GenericResponseDto<Void>());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error saving prices: " + e.getMessage());
        }
    }
}

