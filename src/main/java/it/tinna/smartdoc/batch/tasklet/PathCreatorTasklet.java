package it.tinna.smartdoc.batch.tasklet;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import it.tinna.smartdoc.batch.constants.BatchConstants;
import lombok.Setter;

public class PathCreatorTasklet implements Tasklet, InitializingBean
{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Setter
    private String templateDir;

    @Setter
    private String baseDir;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(templateDir, "Fornire un templateDir valido");
    }

    @Override
    public RepeatStatus execute(StepContribution arg0,
                                ChunkContext arg1) throws Exception
    {
        Map<String, Object> model = new HashMap<>();
        model.put("jobName", arg1.getStepContext().getJobName());
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        cfg.setTemplateLoader(stringLoader);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        stringLoader.putTemplate("workdir", templateDir);
        Writer out = new StringWriter();
        try
        {
            Template template1 = cfg.getTemplate("workdir");
            template1.process(model, out);
            File f = new File(out.toString());
            FileUtils.forceMkdir(f);
            logger.info(String.format("Creata cartella di lavoro del job: %s", out.toString()));
            arg1.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put(BatchConstants.EXECUTIONCONTEXT_JOBDIR, out.toString());
        }
        catch ( IOException | TemplateException e )
        {
            logger.error("Errore nella creazione della cartella di lavoro del job", e);
            throw new Exception(e);
        }
        return RepeatStatus.FINISHED;
    }
}

