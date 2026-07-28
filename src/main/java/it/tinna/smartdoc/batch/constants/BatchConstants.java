package it.tinna.smartdoc.batch.constants;

public class BatchConstants
{

    private BatchConstants()
    {
        throw new IllegalStateException("Utility class");
    }

    public static final String IDENTIFICATIVO_NODO                         = "02254010644";

    public static final String DB_KEY_SERVICE_DB                           = "servicedb";

    public static final String EXECUTIONCONTEXT_ELENCO_FATTURE             = "ELENCO_FATTURE";

    public static final String EXECUTIONCONTEXT_ELENCO_AUTOFATTURE         = "ELENCO_AUTOFATTURE";

    public static final String EXECUTIONCONTEXT_JOBDIR                     = "JOBDIR_KEY";

    public static final String EXECUTIONCONTEXT_FATTURE_ESITO_SDI          = "FATTURE_ESITO_SDI";

    public static final String JOBPARAM_DB_KEY                             = "DB_KEY";

    public static final String JOBPARAM_ID_FATTURE                         = "ID_FATTURE";

    public static final String JOBPARAM_JSON_FATTURA_ENCODED               = "JSON_FATTURA_ENCODED";            // usato per il batch di invio per justdesign

    public static final String JOBPARAM_NOME_STORE                         = "NOME_STORE";                      // usato per il batch di invio per justdesign

    public static final String CONFIG_DOMAIN_BATCH                         = "BATCH";

    public static final String CONFIG_DOMAIN_BATCH_INVIO_SDI               = "BATCH_INVIO_SDI";

    public static final String CONFIG_DOMAIN_BATCH_ESITI_SDI               = "BATCH_ESITI_SDI";

    public static final String CONFIG_DOMAIN_BATCH_ESITI_INVIO             = "BATCH_ESITI_INVIO";

    public static final String CONFIG_DOMAIN_JOB_INVIO_FATTURE             = "QUARTZ_JOB_INVIO_FATTURE";

    public static final String CONFIG_DOMAIN_JOB_LETTURA_ESITI_INVIO       = "QUARTZ_JOB_LETTURA_ESITI_INVIO";

    public static final String CONFIG_DOMAIN_JOB_LETTURA_ESITI_SDI         = "QUARTZ_JOB_LETTURA_ESITI_SDI";

    public static final String CONFIG_DOMAIN_NOTIFICA_FASTORDER            = "NOTIFICA_FASTORDER";

    public static final String CONFIG_DOMAIN_NOTIFICA_JUSTDESIGN           = "NOTIFICA_JUSTDESIGN";

    public static final String CONFIG_KEY_WORKDIR                          = "WORK_DIR";

    public static final String CONFIG_KEY_COMANDO_CIFRATURA                = "COMANDO_CIFRATURA";

    public static final String CONFIG_KEY_COMANDO_FIRMA                    = "COMANDO_FIRMA";

    public static final String CONFIG_KEY_COMANDO_DECIFRATURA              = "COMANDO_DECIFRATURA";

    public static final String CONFIG_KEY_COMANDO_VERIFICA_FIRMA           = "COMANDO_VERIFICA_FIRMA";

    public static final String CONFIG_KEY_CARTELLA_ESITI                   = "CARTELLA_ESITI";

    public static final String CONFIG_KEY_CARTELLA_OUTPUT                  = "CARTELLA_OUTPUT";

    public static final String CONFIG_KEY_MAILCCN                          = "MAIL_CCN";

    public static final String CONFIG_KEY_MAILFROM                         = "MAIL_FROM";

    public static final String CONFIG_KEY_MAILFROMNAME                     = "MAIL_FROMNAME";

    public static final String CONFIG_KEY_MAILTO                           = "MAIL_TO";

    public static final String CONFIG_KEY_MAILHOST                         = "MAIL_HOST";

    public static final String CONFIG_KEY_MAILPASSWORD                     = "MAIL_PASSWORD";

    public static final String CONFIG_KEY_MAILPORT                         = "MAIL_PORT";

    public static final String CONFIG_KEY_MAILUSER                         = "MAIL_USER";

    public static final String CONFIG_KEY_TEST                             = "TEST";

    public static final String CONFIG_KEY_URL_AVVIO_BATCH                  = "URL_AVVIO_BATCH";

    public static final String CONFIG_KEY_URL_IMPORT_FATTURE_FORNITORE     = "URL_IMPORT_FATTURE_FORNITORE";

    public static final String CONFIG_KEY_URL_IMPORT_NOTECREDITO_FORNITORE = "URL_IMPORT_NOTECREDITO_FORNITORE";

}

