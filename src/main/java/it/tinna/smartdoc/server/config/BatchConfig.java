package it.tinna.smartdoc.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configurazione per il modulo Batch e Scheduling.
 * Carica le definizioni dei Job in formato XML e abilita lo scheduling nativo di Spring.
 */
@Configuration
@EnableScheduling
@ImportResource({
    "classpath:jobs/InvioFattureElettroniche_JobDefinitions.xml",
    "classpath:jobs/RicezioneEsitiSdi_JobDefinitions.xml",
    "classpath:jobs/RicezioneEsitiInvio_JobDefinitions.xml"
})
public class BatchConfig {
    // Configurazione automatica caricata tramite ImportResource
}
