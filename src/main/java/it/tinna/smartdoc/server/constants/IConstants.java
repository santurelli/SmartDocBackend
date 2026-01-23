package it.tinna.smartdoc.server.constants;

public interface IConstants {

    String RESOURCES_FILE = "MessageResources";

    String RESOURCES_FILE_EXTENSION = ".properties";

    // nome della cartella contenente i file di configurazione
    String CONFIG_DIR = "config";

    // nome del file di configurazione
    String CONFIG_FILE = "config.xml";

    // nome del file contenente i parametri per la connessione al db
    String DB_FILE = "db.xml";

    // nome del file contenente i parametri per la configurazione di log4j
    String LOG4J_FILE = "log4j.xml";

    // nome del file query
    String QUERY_FILE = "query.xml";

    String DOCUMENT_DIR = "document";

    String REPORT_DIR = "report";

    String CONTEXT_PATH = "smartdoc";

    // nomi delle costanti all'interno del file DB_FILE
    // parametri relativi alla connessione al db
    String DB_DRIVER = "dbDriverName";

    String DB_NAME = "dbName";

    String DB_POOLMINSIZE = "dbPoolMinSize";

    String DB_POOLMAXSIZE = "dbPoolMaxSize";

    String DB_PORT = "dbPort";

    String DB_PWD = "dbPwd";

    String DB_TYPE = "dbType";

    String DB_URL = "dbUrl";

    String DB_USER = "dbUser";

    // nomi "logici" dei gruppi utente
    String ADMIN = "ADMIN";

    String OPERATOR = "OPERATOR";

    String LIKE_OPERATOR = "%";

    String DATE_FORMAT_IT = "dd/MM/yyyy";

    String VERSION = "0.1";

    // nomi delle costanti nel file CONFIG_FILE
    String PRINT_SERVER_ERROR = "ERROR";

    String PRINT_SERVER_OK = "OK";

    String TEMPLATE_BASEDIR = "template.basedir";

    String FATTURAELETTRONICA_PROGRESSIVOINVIO_KEY = "FatturaElettronicaHeader.DatiTrasmissione.ProgressivoInvio";

    String FATTURAELETTRONICA_TIPODOCUMENTO_KEY = "FatturaElettronicaBody.DatiGenerali.DatiGeneraliDocumento.TipoDocumento";

}
