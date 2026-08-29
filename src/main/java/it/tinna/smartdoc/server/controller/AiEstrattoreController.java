package it.tinna.smartdoc.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.tinna.smartdoc.server.delegate.ai.AiEstrazioneDelegate;
import it.tinna.smartdoc.server.security.UserContextHolder;
import it.tinna.smartdoc.shared.dto.ai.EstrazioneFatturaFornitoreDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/ai")
public class AiEstrattoreController {

    private static final Logger log = LoggerFactory.getLogger(AiEstrattoreController.class);
    private static final int TIPO_ACCOUNT_ENTERPRISE = 4;
    private static final long MAX_FILE_SIZE_BYTES = 15L * 1024 * 1024; // 15MB

    private final AiEstrazioneDelegate aiEstrazioneDelegate;

    public AiEstrattoreController(AiEstrazioneDelegate aiEstrazioneDelegate) {
        this.aiEstrazioneDelegate = aiEstrazioneDelegate;
    }

    private boolean isPianoEnterprise() {
        Integer tipoAccount = UserContextHolder.getTipoAccount();
        return tipoAccount != null && tipoAccount >= TIPO_ACCOUNT_ENTERPRISE;
    }

    @PostMapping("/estrai-fattura-fornitore")
    public ResponseEntity<GenericResponseDto<EstrazioneFatturaFornitoreDto>> estraiFatturaFornitore(@RequestParam("file") MultipartFile file) {
        if (!isPianoEnterprise()) {
            return ResponseEntity.status(403).build();
        }

        GenericResponseDto<EstrazioneFatturaFornitoreDto> response = new GenericResponseDto<>();

        if (file == null || file.isEmpty()) {
            response.setErrorText("Nessun file caricato");
            return ResponseEntity.badRequest().body(response);
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            response.setErrorText("File troppo grande (massimo 15MB)");
            return ResponseEntity.badRequest().body(response);
        }
        String contentType = file.getContentType();
        boolean tipoValido = contentType != null && (contentType.equals("application/pdf")
                || contentType.equals("image/png") || contentType.equals("image/jpeg"));
        if (!tipoValido) {
            response.setErrorText("Formato non supportato: usa PDF, JPG o PNG");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            EstrazioneFatturaFornitoreDto dto = aiEstrazioneDelegate.estraiFatturaFornitore(file);
            response.setPayload(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore nell'estrazione AI della fattura fornitore", e);
            response.setErrorText("Errore durante l'estrazione: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
