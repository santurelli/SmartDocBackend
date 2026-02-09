package it.tinna.smartdoc.server.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportLoader {

    private static final Logger logger = LoggerFactory.getLogger(ReportLoader.class);

    private static final String REPORT_BASE_PATH = "/report_stampa/";

    private static final Map<String, JasperReport> reportCache = new HashMap<>();

    /**
     * Carica e compila un report Jasper dal classpath.
     * Utilizza una cache per evitare ricompilazioni inutili.
     * 
     * @param reportName Il nome del file .jrxml (es. "preventivo.jrxml")
     * @return Il JasperReport compilato
     * @throws JRException Se c'è un errore durante la compilazione o il caricamento
     */
    public static synchronized JasperReport getReport(String reportName) throws JRException {
        if (reportCache.containsKey(reportName)) {
            return reportCache.get(reportName);
        }

        String reportPath = REPORT_BASE_PATH + reportName;
        logger.info("Caricamento report da classpath: {}", reportPath);

        InputStream is = ReportLoader.class.getResourceAsStream(reportPath);
        if (is == null) {
            throw new JRException("Impossibile trovare il report nel classpath: " + reportPath);
        }

        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(is);
            reportCache.put(reportName, jasperReport);
            return jasperReport;
        } catch (JRException e) {
            logger.error("Errore durante la compilazione del report: {}", reportName, e);
            throw e;
        }
    }

    /**
     * Svuota la cache dei report compilati.
     * Utile in fase di sviluppo o se i report cambiano a runtime (raro per risorse
     * nel classpath).
     */
    public static synchronized void clearCache() {
        reportCache.clear();
    }
}
