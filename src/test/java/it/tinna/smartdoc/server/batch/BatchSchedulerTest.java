package it.tinna.smartdoc.server.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import it.tinna.smartdoc.server.delegate.municipality.MunicipalityDelegate;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;

/**
 * Unit tests for BatchScheduler.
 */
public class BatchSchedulerTest {

    @InjectMocks
    private BatchScheduler batchScheduler;

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job jobInvioFatture;

    @Mock
    private Job jobRicezioneEsiti;

    @Mock
    private Job jobRicezioneEsitiInvio;

    @Mock
    private MunicipalityDelegate municipalityDelegate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRunInvioFatture_SuccessWithMultipleTenants() throws Exception {
        // Setup mock data for 2 tenants
        List<MunicipalityDto> activeTenants = new ArrayList<>();
        
        MunicipalityDto t1 = new MunicipalityDto();
        t1.setDbName("tenant1");
        activeTenants.add(t1);
        
        MunicipalityDto t2 = new MunicipalityDto();
        t2.setDbName("tenant2");
        activeTenants.add(t2);

        when(municipalityDelegate.getAziendeConFatturazioneElettronica()).thenReturn(activeTenants);

        // Run the scheduler method
        batchScheduler.runInvioFatture();

        // Verify jobLauncher was called twice with correct parameters
        verify(municipalityDelegate, times(1)).getAziendeConFatturazioneElettronica();
        verify(jobLauncher, times(2)).run(eq(jobInvioFatture), any(JobParameters.class));
    }

    @Test
    public void testRunInvioFatture_NoTenants() throws Exception {
        // Setup empty tenant list
        when(municipalityDelegate.getAziendeConFatturazioneElettronica()).thenReturn(new ArrayList<>());

        // Run the scheduler method
        batchScheduler.runInvioFatture();

        // Verify launcher was never called
        verify(jobLauncher, times(0)).run(any(Job.class), any(JobParameters.class));
    }

    @Test
    public void testRunInvioFatture_ResilienceToJobFailure() throws Exception {
        // Setup 2 tenants where the first job launch fails
        List<MunicipalityDto> activeTenants = new ArrayList<>();
        MunicipalityDto t1 = new MunicipalityDto(); t1.setDbName("failTenant"); activeTenants.add(t1);
        MunicipalityDto t2 = new MunicipalityDto(); t2.setDbName("successTenant"); activeTenants.add(t2);

        when(municipalityDelegate.getAziendeConFatturazioneElettronica()).thenReturn(activeTenants);
        
        // Mock exception for the first tenant only
        // Since we can't easily match exactly which call fails without complex matchers, 
        // we test that the loop continues by verifying total calls.
        when(jobLauncher.run(eq(jobInvioFatture), any(JobParameters.class)))
            .thenThrow(new RuntimeException("Job failed"))
            .thenReturn(null);

        // Run the scheduler method
        batchScheduler.runInvioFatture();

        // Verify it attempted to run for both despite the first failure
        verify(jobLauncher, times(2)).run(eq(jobInvioFatture), any(JobParameters.class));
    }

    @Test
    public void testRunRicezioneEsiti_Success() throws Exception {
        // Run the scheduler method
        batchScheduler.runRicezioneEsiti();

        // Verify jobLauncher was called once
        verify(jobLauncher, times(1)).run(eq(jobRicezioneEsiti), any(JobParameters.class));
    }

    @Test
    public void testRunRicezioneEsitiInvio_Success() throws Exception {
        // Run the scheduler method
        batchScheduler.runRicezioneEsitiInvio();

        // Verify jobLauncher was called once for the second outcome job
        verify(jobLauncher, times(1)).run(eq(jobRicezioneEsitiInvio), any(JobParameters.class));
    }

    @Test
    public void testRunInvioFatture_DelegateException() throws Exception {
        // Mock exception from delegate
        when(municipalityDelegate.getAziendeConFatturazioneElettronica()).thenThrow(new SQLException("DB Error"));

        // Run the scheduler method
        batchScheduler.runInvioFatture();

        // Verify launcher was never called and method completed without propagation
        verify(jobLauncher, times(0)).run(any(Job.class), any(JobParameters.class));
    }
}
