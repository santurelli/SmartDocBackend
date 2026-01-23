package it.tinna.smartdoc.server.database;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;

import it.tinna.smartdoc.server.constants.IConstants;

public class FileQueryReader {

    static HashMap<String, String> queryMap;

    private static Log _log = LogFactory.getLog(FileQueryReader.class);

    static {
        try {
            // String fileQueryDirectory = new
            // StringBuilder(IOUtility.getWebInfPath()).append(IConstants.CONFIG_DIR).toString();
            XMLConfiguration config = new XMLConfiguration();
            // config.setFile(ConfigurationUtils.getFile("\"" + fileQueryDirectory + "\"",
            // IConstants.QUERY_FILE));
            ClassPathResource resource = new ClassPathResource(
                    IConstants.CONFIG_DIR + File.separator + IConstants.QUERY_FILE);
            config.setFile(resource.getFile());
            config.setDelimiterParsingDisabled(true);
            config.load();
            List<Object> key = config.getList("query[@name]");
            List<Object> value = config.getList("query.content");
            queryMap = new HashMap<String, String>();
            for (int i = 0; i < key.size(); i++) {
                queryMap.put(key.get(i).toString(), value.get(i).toString());
            }
        } catch (ConfigurationException e) {
            _log.fatal("Errore nella lettura del file query", e);
        } catch (Exception e) {
            _log.fatal("Errore nella lettura del file query", e);
        }
    }

    private FileQueryReader() {
    }

    public static String getQuery(String name) {
        return queryMap.get(name);
    }

    public static void main(String[] args) {
        new FileQueryReader();
    }
}
