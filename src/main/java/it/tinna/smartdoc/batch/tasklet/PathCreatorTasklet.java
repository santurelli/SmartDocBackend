package it.tinna.smartdoc.batch.tasklet;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import it.tinna.smartdoc.batch.constants.BatchConstants;

public class PathCreatorTasklet implements Tasklet, InitializingBean
{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${smartdoc.batch.workdir}")
    private String workDir;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.hasText(workDir, "Configurare 'smartdoc.batch.workdir' in application.properties");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception
    {
        String jobName = chunkContext.getStepContext().getJobName();
        File jobDir = new File(workDir, jobName);
        try
        {
            FileUtils.forceMkdir(jobDir);
            logger.info("Creata cartella di lavoro del job: {}", jobDir.getAbsolutePath());
            chunkContext.getStepContext()
                        .getStepExecution()
                        .getJobExecution()
                        .getExecutionContext()
                        .put(BatchConstants.EXECUTIONCONTEXT_JOBDIR, jobDir.getAbsolutePath());
        }
        catch ( Exception e )
        {
            logger.error("Errore nella creazione della cartella di lavoro del job: {}", jobDir.getAbsolutePath(), e);
            throw e;
        }
        return RepeatStatus.FINISHED;
    }
}
