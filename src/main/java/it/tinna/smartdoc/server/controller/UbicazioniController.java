package it.tinna.smartdoc.server.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.server.delegate.prodotti.ProdottiDelegate;
import it.tinna.smartdoc.server.delegate.ubicazioni.UbicazioniDelegate;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import it.tinna.smartdoc.shared.dto.configurazione.ConfigurazioneDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import it.tinna.smartdoc.shared.dto.ubicazioni.UbicazioneDto;
import it.tinna.smartdoc.shared.dto.ubicazioni.UbicazioneProdottoDto;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/ubicazioni")
@Slf4j
public class UbicazioniController {

    @Autowired
    private UbicazioniDelegate ubicazioniDelegate;

    @Autowired
    private ProdottiDelegate prodottiDelegate;

    @Autowired
    private ConfigurazioneDelegate configurazioneDelegate;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    private String getCurrentUsername() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                return ((UserDetailsImpl) principal).getUsername();
            }
        } catch (Exception ignored) { }
        return "admin";
    }

    @PostMapping("/list")
    public ResponseEntity<?> getList(@RequestBody Map<String, Object> params) {
        try {
            int start = (int) params.getOrDefault("start", 0);
            int length = (int) params.getOrDefault("length", 100);
            String search = (String) params.get("search");

            List<UbicazioneDto> list = ubicazioniDelegate.getList(search, length, start);
            long total = list.isEmpty() ? 0 : list.get(0).getTotal();

            DatatablesResponseDto<UbicazioneDto> response = new DatatablesResponseDto<>();
            response.setList(list);
            response.setTotalCount(total);
            response.setTotalFiltered(total);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nel recupero della lista ubicazioni", e);
            return ResponseEntity.internalServerError().body("Errore nel recupero delle ubicazioni: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable long id) {
        try {
            UbicazioneDto dto = ubicazioniDelegate.getById(id);
            if (dto != null) {
                GenericResponseDto<UbicazioneDto> response = new GenericResponseDto<>();
                response.setPayload(dto);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Errore nel recupero dell'ubicazione id {}", id, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody UbicazioneDto dto) {
        try {
            Long userId = getCurrentUserId();
            if (dto.getId() == 0) {
                dto.setUserCreated(userId);
            } else {
                dto.setUserLastUpdate(userId);
            }
            long id = ubicazioniDelegate.save(dto);
            dto.setId(id);

            GenericResponseDto<UbicazioneDto> response = new GenericResponseDto<>();
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nel salvataggio dell'ubicazione", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        try {
            ubicazioniDelegate.delete(id, getCurrentUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Errore nella cancellazione dell'ubicazione id {}", id, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/prodotto/{idProdotto}")
    public ResponseEntity<?> getUbicazioniByProdotto(@PathVariable long idProdotto) {
        try {
            List<UbicazioneProdottoDto> list = ubicazioniDelegate.getUbicazioniByProdotto(idProdotto);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("Errore nel recupero ubicazioni per articolo {}", idProdotto, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{idUbicazione}/prodotti")
    public ResponseEntity<?> getProdottiByUbicazione(@PathVariable long idUbicazione) {
        try {
            List<UbicazioneProdottoDto> list = ubicazioniDelegate.getProdottiByUbicazione(idUbicazione);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("Errore nel recupero prodotti per ubicazione {}", idUbicazione, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/prodotto/save")
    public ResponseEntity<?> saveProdottoUbicazione(@RequestBody UbicazioneProdottoDto dto) {
        try {
            Long userId = getCurrentUserId();
            if (dto.getId() == 0) {
                dto.setUserCreated(userId);
            } else {
                dto.setUserLastUpdate(userId);
            }
            long id = ubicazioniDelegate.saveProdottoUbicazione(dto);
            dto.setId(id);

            GenericResponseDto<UbicazioneProdottoDto> response = new GenericResponseDto<>();
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nel salvataggio ubicazione prodotto", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/prodotto/{id}")
    public ResponseEntity<?> deleteProdottoUbicazione(@PathVariable long id) {
        try {
            ubicazioniDelegate.deleteProdottoUbicazione(id, getCurrentUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Errore nella cancellazione ubicazione prodotto id {}", id, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/prelievo")
    public ResponseEntity<?> prelievo(@RequestBody Map<String, Object> req) {
        try {
            long idProdotto = Long.parseLong(req.get("idProdotto").toString());
            ProdottoDto prodotto = prodottiDelegate.getById(idProdotto);
            if (prodotto == null) {
                return ResponseEntity.badRequest().body("Prodotto non trovato");
            }

            List<Map<String, Object>> itemsList = new ArrayList<>();
            if (req.containsKey("items") && req.get("items") instanceof List) {
                itemsList = (List<Map<String, Object>>) req.get("items");
            }

            Long currentUid = getCurrentUserId();
            ubicazioniDelegate.scaricaUbicazioniEGiacenza(idProdotto, itemsList, prodotto, currentUid);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Prelievo effettuato con successo. Giacenza generale e ubicazioni aggiornate.");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Errore nel prelievo ubicazioni per prodotto", e);
            return ResponseEntity.internalServerError().body("Errore nel prelievo: " + e.getMessage());
        }
    }

    @PostMapping("/stampa-prelievo")
    public ResponseEntity<?> stampaPrelievo(@RequestBody Map<String, Object> req) {
        try {
            long idProdotto = Long.parseLong(req.get("idProdotto").toString());
            ProdottoDto prodotto = prodottiDelegate.getById(idProdotto);
            if (prodotto == null) {
                return ResponseEntity.badRequest().body("Prodotto non trovato");
            }

            // Può ricevere "items" (lista di righe da prelevare) oppure i singoli campi per retrocompatibilità
            List<Map<String, Object>> itemsList = new ArrayList<>();
            if (req.containsKey("items") && req.get("items") instanceof List) {
                itemsList = (List<Map<String, Object>>) req.get("items");
            } else {
                Map<String, Object> singleItem = new HashMap<>();
                if (req.containsKey("idUbicazione")) singleItem.put("idUbicazione", req.get("idUbicazione"));
                if (req.containsKey("codUbicazione")) singleItem.put("codUbicazione", req.get("codUbicazione"));
                if (req.containsKey("quantita")) singleItem.put("quantita", req.get("quantita"));
                if (req.containsKey("dot")) singleItem.put("dot", req.get("dot"));
                itemsList.add(singleItem);
            }

            // Se richiesto, decrementa la giacenza dalle ubicazioni coinvolte e scala la giacenza generale
            boolean scaricaGiacenza = req.containsKey("scaricaGiacenza") && Boolean.parseBoolean(req.get("scaricaGiacenza").toString());
            if (scaricaGiacenza) {
                Long currentUid = getCurrentUserId();
                ubicazioniDelegate.scaricaUbicazioniEGiacenza(idProdotto, itemsList, prodotto, currentUid);
            }

            boolean doPrint = !req.containsKey("stampa") || Boolean.parseBoolean(req.get("stampa").toString());
            if (!doPrint) {
                Map<String, Object> result = new HashMap<>();
                result.put("sentByServer", false);
                result.put("message", "Prelievo effettuato con successo. Giacenza generale e ubicazioni aggiornate.");
                return ResponseEntity.ok(result);
            }

            // Lettura configurazione stampante
            Map<String, ConfigurazioneDto> globalConfig = configurazioneDelegate.getByDomain("GLOBAL");
            String printerIp = "192.168.1.200";
            String bridgeUrl = "http://localhost:3001";
            if (globalConfig != null) {
                if (globalConfig.containsKey("STAMPANTE_PRELIEVO_IP") && globalConfig.get("STAMPANTE_PRELIEVO_IP").getValore() != null) {
                    printerIp = globalConfig.get("STAMPANTE_PRELIEVO_IP").getValore();
                }
                if (globalConfig.containsKey("PRINTER_BRIDGE_URL") && globalConfig.get("PRINTER_BRIDGE_URL").getValore() != null) {
                    bridgeUrl = globalConfig.get("PRINTER_BRIDGE_URL").getValore();
                }
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String now = LocalDateTime.now().format(dtf);

            List<String> lines = new ArrayList<>();
            lines.add("================================");
            lines.add("      SCHEDA DI PRELIEVO        ");
            lines.add("================================");
            lines.add("Data: " + now);
            lines.add("Articolo: " + (prodotto.getCodice() != null ? prodotto.getCodice() : ""));
            lines.add("Desc: " + (prodotto.getDescrizione() != null ? prodotto.getDescrizione() : ""));
            
            if (prodotto.getSezione() != null && prodotto.getSerie() != null && prodotto.getDiametro() != null) {
                String tyreSpec = prodotto.getSezione() + "/" + (prodotto.getSerie() != null ? prodotto.getSerie() : "") + " R" + prodotto.getDiametro() 
                    + (prodotto.getIndiceVelocita() != null ? " " + prodotto.getIndiceVelocita() : "");
                lines.add("Misura: " + tyreSpec);
            }
            lines.add("--------------------------------");
            lines.add("UBICAZIONE / DOT           Q.TA'");
            lines.add("--------------------------------");

            double totQta = 0;
            for (Map<String, Object> item : itemsList) {
                long idUbi = item.containsKey("idUbicazione") && item.get("idUbicazione") != null ? Long.parseLong(item.get("idUbicazione").toString()) : 0;
                double qta = item.containsKey("quantita") && item.get("quantita") != null ? Double.parseDouble(item.get("quantita").toString()) : 0.0;
                if (qta <= 0) continue;
                totQta += qta;

                String dot = item.containsKey("dot") && item.get("dot") != null ? item.get("dot").toString() : "";
                UbicazioneDto ubicazione = idUbi > 0 ? ubicazioniDelegate.getById(idUbi) : null;
                String codUbi = ubicazione != null ? ubicazione.getCodUbicazione() : (item.containsKey("codUbicazione") && item.get("codUbicazione") != null ? item.get("codUbicazione").toString() : "N.D.");
                String corsia = ubicazione != null && ubicazione.getCorsia() != null ? ubicazione.getCorsia() : (item.containsKey("corsia") && item.get("corsia") != null ? item.get("corsia").toString() : "");
                String ripiano = ubicazione != null && ubicazione.getRipiano() != null ? ubicazione.getRipiano() : (item.containsKey("ripiano") && item.get("ripiano") != null ? item.get("ripiano").toString() : "");
                String livello = ubicazione != null && ubicazione.getLivello() != null ? ubicazione.getLivello() : (item.containsKey("livello") && item.get("livello") != null ? item.get("livello").toString() : "");

                String ubiStr = codUbi + (!dot.isEmpty() ? " (DOT " + dot + ")" : "");
                String qtaStr = ((int) qta) + " pz";
                int pad = 32 - ubiStr.length() - qtaStr.length();
                if (pad > 0) {
                    lines.add(ubiStr + " ".repeat(pad) + qtaStr);
                } else {
                    lines.add(ubiStr);
                    lines.add(" ".repeat(Math.max(0, 32 - qtaStr.length())) + qtaStr);
                }

                List<String> posDetails = new ArrayList<>();
                if (!corsia.isEmpty()) posDetails.add("Corsia: " + corsia);
                if (!ripiano.isEmpty()) posDetails.add("Rip: " + ripiano);
                if (!livello.isEmpty()) posDetails.add("Liv: " + livello);
                if (!posDetails.isEmpty()) {
                    lines.add("  " + String.join(" | ", posDetails));
                }
            }

            lines.add("--------------------------------");
            lines.add("TOTALE PRELIEVO: " + ((int) totQta) + " pz");
            lines.add("--------------------------------");
            lines.add("Operatore: " + getCurrentUsername());
            lines.add("Firma: _________________________");
            lines.add("================================");

            String protocol = "ESCPOS";
            if (globalConfig != null && globalConfig.containsKey("STAMPANTE_PRELIEVO_PROTOCOLLO") && globalConfig.get("STAMPANTE_PRELIEVO_PROTOCOLLO").getValore() != null) {
                protocol = globalConfig.get("STAMPANTE_PRELIEVO_PROTOCOLLO").getValore();
            }

            boolean isSimulator = "SIMULATOR".equalsIgnoreCase(printerIp) || "SIMULATOR".equalsIgnoreCase(protocol);
            Map<String, Object> connection = new HashMap<>();
            connection.put("type", isSimulator ? "SIMULATOR" : "TCPIP");
            connection.put("host", printerIp);
            connection.put("protocol", protocol);

            Map<String, Object> payload = new HashMap<>();
            payload.put("command", "text-report");
            payload.put("connection", connection);
            payload.put("lines", lines);

            // Prova invio diretto al PrinterBridge se raggiungibile
            boolean sentByServer = false;
            String serverMsg = "";
            try {
                String requestJson = objectMapper.writeValueAsString(payload);
                HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
                HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(bridgeUrl + "/printer-command"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .timeout(Duration.ofSeconds(5))
                    .build();

                HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
                    sentByServer = true;
                    serverMsg = "Stampa inviata con successo alla stampante (" + printerIp + ")";
                }
            } catch (Exception ex) {
                log.info("PrinterBridge non raggiungibile dal backend ({}), il client frontend eseguira la chiamata locale: {}", bridgeUrl, ex.getMessage());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("sentByServer", sentByServer);
            result.put("message", serverMsg);
            result.put("printerIp", printerIp);
            result.put("bridgeUrl", bridgeUrl);
            result.put("bridgePayload", payload);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Errore nella generazione della scheda di prelievo", e);
            return ResponseEntity.internalServerError().body("Errore: " + e.getMessage());
        }
    }
}
