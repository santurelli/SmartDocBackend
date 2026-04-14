package it.tinna.smartdoc.server.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.batch.BatchScheduler;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Autowired(required = false)
    private BatchScheduler batchScheduler;

    @GetMapping("/run-invio")
    public ResponseEntity<String> runInvioFatture() {
        if (batchScheduler == null) {
            return ResponseEntity.status(503).body("Il modulo di scheduling è disattivato in questo profilo.");
        }
        // Viene eseguito in un thread separato per evitare timeout della richiesta HTTP
        CompletableFuture.runAsync(() -> batchScheduler.runInvioFatture());
        return ResponseEntity.ok("Avviato job di invio fatture in background.");
    }

    @GetMapping("/run-esiti")
    public ResponseEntity<String> runRicezioneEsiti() {
        if (batchScheduler == null) {
            return ResponseEntity.status(503).body("Il modulo di scheduling è disattivato in questo profilo.");
        }
        CompletableFuture.runAsync(() -> batchScheduler.runRicezioneEsiti());
        return ResponseEntity.ok("Avviato job di ricezione esiti SDI in background.");
    }

    @GetMapping("/run-esiti-invio")
    public ResponseEntity<String> runRicezioneEsitiInvio() {
        if (batchScheduler == null) {
            return ResponseEntity.status(503).body("Il modulo di scheduling è disattivato in questo profilo.");
        }
        CompletableFuture.runAsync(() -> batchScheduler.runRicezioneEsitiInvio());
        return ResponseEntity.ok("Avviato job di ricezione esiti invio in background.");
    }
}
