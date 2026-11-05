package it.tinna.smartdoc.batch.tasklet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;

import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;

public class CleanupOldWorkDirsTaskletTest {

    private File tempBaseDir;
    private CleanupOldWorkDirsTasklet tasklet;

    @BeforeEach
    public void setUp() throws IOException {
        tempBaseDir = new File(System.getProperty("java.io.tmpdir"), "smartdoc_cleanup_test_" + System.currentTimeMillis());
        FileUtils.forceMkdir(tempBaseDir);

        tasklet = new CleanupOldWorkDirsTasklet();
        tasklet.setWorkDir(tempBaseDir.getAbsolutePath());
        tasklet.setRetentionDays(30);
    }

    @AfterEach
    public void tearDown() {
        if (tempBaseDir != null && tempBaseDir.exists()) {
            FileUtils.deleteQuietly(tempBaseDir);
        }
    }

    private ChunkContext createChunkContext(String jobName) {
        JobInstance jobInstance = new JobInstance(1L, jobName);
        JobExecution jobExecution = new JobExecution(jobInstance, 1L, new JobParameters());
        StepExecution stepExecution = new StepExecution("cleanupStep", jobExecution);
        StepContext stepContext = new StepContext(stepExecution);
        return new ChunkContext(stepContext);
    }

    @Test
    public void testExecute_DeletesOldFoldersAndPreservesRecent() throws Exception {
        String jobName = "JOB_INVIO_FATTUREELETTRONICHE";
        File jobDir = new File(tempBaseDir, jobName);
        FileUtils.forceMkdir(jobDir);

        // 1. Cartella obsoleta per pattern data (es. 20260101)
        File oldDateDir = new File(jobDir, "TENANT1/2026/202601/20260101/20260101100000");
        FileUtils.forceMkdir(oldDateDir);
        FileUtils.writeStringToFile(new File(oldDateDir, "fattura_old.xml"), "data", StandardCharsets.UTF_8);

        // 2. Cartella recente per pattern data (oggi)
        String todayStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        File recentDateDir = new File(jobDir, "TENANT1/2026/202611/" + todayStr + "/" + todayStr + "120000");
        FileUtils.forceMkdir(recentDateDir);
        FileUtils.writeStringToFile(new File(recentDateDir, "fattura_today.xml"), "data", StandardCharsets.UTF_8);

        // 3. Cartella con lastModified obsoleto (60 giorni fa)
        File oldMtimeDir = new File(jobDir, "OLD_ESITO_FOLDER");
        FileUtils.forceMkdir(oldMtimeDir);
        File oldFile = new File(oldMtimeDir, "old_file.zip");
        FileUtils.writeStringToFile(oldFile, "old_zip", StandardCharsets.UTF_8);
        long oldTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(60);
        oldFile.setLastModified(oldTime);
        oldMtimeDir.setLastModified(oldTime);

        // 4. Cartella con lastModified recente (2 giorni fa)
        File recentMtimeDir = new File(jobDir, "RECENT_ESITO_FOLDER");
        FileUtils.forceMkdir(recentMtimeDir);
        File recentFile = new File(recentMtimeDir, "recent_file.zip");
        FileUtils.writeStringToFile(recentFile, "recent_zip", StandardCharsets.UTF_8);
        long recentTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2);
        recentFile.setLastModified(recentTime);
        recentMtimeDir.setLastModified(recentTime);

        // Esegui la tasklet
        ChunkContext chunkContext = createChunkContext(jobName);
        StepContribution contribution = new StepContribution(chunkContext.getStepContext().getStepExecution());
        tasklet.execute(contribution, chunkContext);

        // Verifiche: le vecchie devono essere rimosse, le nuove preservate
        assertFalse(oldDateDir.exists(), "La cartella con data obsoleta deve essere cancellata");
        assertFalse(oldMtimeDir.exists(), "La cartella con mtime obsoleto deve essere cancellata");

        assertTrue(recentDateDir.exists(), "La cartella con data odierna deve essere preservata");
        assertTrue(recentMtimeDir.exists(), "La cartella recente deve essere preservata");
    }

    @Test
    public void testExecute_RetentionDisabledWhenZeroOrNegative() throws Exception {
        String jobName = "JOB_RICEZIONE_ESITISDI";
        File jobDir = new File(tempBaseDir, jobName);
        FileUtils.forceMkdir(jobDir);

        File oldDir = new File(jobDir, "OLD_DIR");
        FileUtils.forceMkdir(oldDir);
        File oldFile = new File(oldDir, "test.txt");
        FileUtils.writeStringToFile(oldFile, "hello", StandardCharsets.UTF_8);
        long oldTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(60);
        oldFile.setLastModified(oldTime);
        oldDir.setLastModified(oldTime);

        tasklet.setRetentionDays(0);

        ChunkContext chunkContext = createChunkContext(jobName);
        StepContribution contribution = new StepContribution(chunkContext.getStepContext().getStepExecution());
        tasklet.execute(contribution, chunkContext);

        assertTrue(oldDir.exists(), "Con retentionDays <= 0 la pulizia deve essere disabilitata");
    }

    @Test
    public void testExecute_ConfigurazioneFromServiceDbOverridesProperty() throws Exception {
        String jobName = "JOB_TEST_DB_OVERRIDE";
        File jobDir = new File(tempBaseDir, jobName);
        FileUtils.forceMkdir(jobDir);

        // Cartella creata 10 giorni fa
        File tenDaysOldDir = new File(jobDir, "TEN_DAYS_OLD");
        FileUtils.forceMkdir(tenDaysOldDir);
        File f = new File(tenDaysOldDir, "data.txt");
        FileUtils.writeStringToFile(f, "content", StandardCharsets.UTF_8);
        long tenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10);
        f.setLastModified(tenDaysAgo);
        tenDaysOldDir.setLastModified(tenDaysAgo);

        // Inizialmente retentionDays = 30 (quindi non verrebbe cancellata)
        tasklet.setRetentionDays(30);

        // Mock configurazioneDelegate per restituire retention = 5 giorni
        ConfigurazioneDelegate delegateMock = mock(ConfigurazioneDelegate.class);
        when(delegateMock.getByKeyFromServiceDb(BatchConstants.CONFIG_DOMAIN_BATCH, BatchConstants.CONFIG_KEY_WORKDIR_RETENTION_DAYS))
                .thenReturn("5");

        // Usiamo reflection o setter se presente
        java.lang.reflect.Field field = CleanupOldWorkDirsTasklet.class.getDeclaredField("configurazioneDelegate");
        field.setAccessible(true);
        field.set(tasklet, delegateMock);

        ChunkContext chunkContext = createChunkContext(jobName);
        StepContribution contribution = new StepContribution(chunkContext.getStepContext().getStepExecution());
        tasklet.execute(contribution, chunkContext);

        // Essendo 10 giorni > 5 giorni, deve essere stata cancellata!
        assertFalse(tenDaysOldDir.exists(), "La cartella deve essere cancellata secondo la retention da DB (5 giorni)");
    }
}
