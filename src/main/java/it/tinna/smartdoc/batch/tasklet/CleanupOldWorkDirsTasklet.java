package it.tinna.smartdoc.batch.tasklet;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import lombok.Getter;
import lombok.Setter;

public class CleanupOldWorkDirsTasklet implements Tasklet, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(CleanupOldWorkDirsTasklet.class);

    @Getter
    @Setter
    @Value("${smartdoc.batch.workdir}")
    private String workDir;

    @Getter
    @Setter
    @Value("${smartdoc.batch.workdir.retention-days:30}")
    private int retentionDays = 30;

    @Autowired(required = false)
    private ConfigurazioneDelegate configurazioneDelegate;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Nessun vincolo bloccante: se workDir manca a bootstrap, execute() gestisce il fallback in sicurezza
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String jobName = chunkContext.getStepContext().getJobName();
        logger.info("Avvio step di pulizia cartelle di lavoro per il job: {}", jobName);

        int effectiveRetentionDays = resolveRetentionDays();
        if (effectiveRetentionDays <= 0) {
            logger.info("RetentionDays configurato a {} (<= 0): pulizia cartelle disabilitata.", effectiveRetentionDays);
            return RepeatStatus.FINISHED;
        }

        if (!StringUtils.hasText(workDir)) {
            logger.warn("Cartella di lavoro base non configurata ('smartdoc.batch.workdir'), salto la pulizia.");
            return RepeatStatus.FINISHED;
        }

        File jobBaseDir = new File(workDir, jobName);
        if (!jobBaseDir.exists() || !jobBaseDir.isDirectory()) {
            logger.debug("Nessuna cartella di lavoro trovata per il job '{}' al percorso {}", jobName, jobBaseDir.getAbsolutePath());
            return RepeatStatus.FINISHED;
        }

        long cutoffMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(effectiveRetentionDays);
        LocalDate cutoffDate = LocalDate.now().minusDays(effectiveRetentionDays);

        logger.info("Pulizia cartelle per il job '{}' in '{}' più vecchie di {} giorni (cutoff: {} / {} ms)",
                jobName, jobBaseDir.getAbsolutePath(), effectiveRetentionDays, cutoffDate, cutoffMillis);

        try {
            cleanRecursively(jobBaseDir, cutoffDate, cutoffMillis);
        } catch (Exception e) {
            logger.error("Errore durante la pulizia delle cartelle di lavoro per il job {}: {}", jobName, e.getMessage(), e);
        }

        return RepeatStatus.FINISHED;
    }

    private int resolveRetentionDays() {
        if (configurazioneDelegate != null) {
            try {
                String dbVal = configurazioneDelegate.getByKeyFromServiceDb(
                        BatchConstants.CONFIG_DOMAIN_BATCH,
                        BatchConstants.CONFIG_KEY_WORKDIR_RETENTION_DAYS);
                if (StringUtils.hasText(dbVal)) {
                    return Integer.parseInt(dbVal.trim());
                }
            } catch (Exception e) {
                logger.debug("Impossibile leggere {} da db: {}", BatchConstants.CONFIG_KEY_WORKDIR_RETENTION_DAYS, e.getMessage());
            }
        }
        return this.retentionDays;
    }

    /**
     * Pulisce ricorsivamente file e sottocartelle sotto dir.
     * Ritorna true se la directory dir è ora completamente vuota.
     */
    public boolean cleanRecursively(File dir, LocalDate cutoffDate, long cutoffMillis) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return true;
        }

        for (File file : files) {
            try {
                if (file.isDirectory()) {
                    if (isFolderOlderThanCutoff(file, cutoffDate, cutoffMillis)) {
                        logger.info("Eliminazione cartella di lavoro obsoleta: {}", file.getAbsolutePath());
                        FileUtils.deleteDirectory(file);
                    } else {
                        // Cartella ancora recente o mista: controlliamo l'interno
                        boolean isEmptyNow = cleanRecursively(file, cutoffDate, cutoffMillis);
                        if (isEmptyNow) {
                            logger.info("Eliminazione cartella vuota: {}", file.getAbsolutePath());
                            FileUtils.deleteQuietly(file);
                        }
                    }
                } else if (file.isFile()) {
                    if (file.lastModified() < cutoffMillis) {
                        logger.debug("Eliminazione file di lavoro obsoleto: {}", file.getAbsolutePath());
                        FileUtils.deleteQuietly(file);
                    }
                }
            } catch (Exception e) {
                logger.warn("Impossibile eliminare {}: {}", file.getAbsolutePath(), e.getMessage());
            }
        }

        File[] remaining = dir.listFiles();
        return remaining == null || remaining.length == 0;
    }

    private boolean isFolderOlderThanCutoff(File dir, LocalDate cutoffDate, long cutoffMillis) {
        String name = dir.getName();
        // Se il nome è un pattern di data (es. 20260215 o 20260215103000)
        if (name.matches("\\d{8}")) {
            try {
                LocalDate folderDate = LocalDate.parse(name, DateTimeFormatter.BASIC_ISO_DATE);
                return folderDate.isBefore(cutoffDate);
            } catch (Exception ignored) {}
        } else if (name.matches("\\d{14}")) {
            try {
                LocalDate folderDate = LocalDate.parse(name.substring(0, 8), DateTimeFormatter.BASIC_ISO_DATE);
                return folderDate.isBefore(cutoffDate);
            } catch (Exception ignored) {}
        }

        // Se non è un nome di data o non ha fatto match, controlliamo ricorsivamente l'mtime dei contenuti
        return !hasContentNewerThan(dir, cutoffMillis);
    }

    private boolean hasContentNewerThan(File dir, long cutoffMillis) {
        if (dir.lastModified() >= cutoffMillis) {
            return true;
        }
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    if (hasContentNewerThan(child, cutoffMillis)) {
                        return true;
                    }
                } else if (child.isFile()) {
                    if (child.lastModified() >= cutoffMillis) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
