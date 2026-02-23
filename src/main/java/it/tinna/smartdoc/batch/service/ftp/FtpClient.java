package it.tinna.smartdoc.batch.service.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tinna.smartdoc.batch.exception.FtpException;
import it.tinna.smartdoc.batch.exception.NotYetImplementedException;

public class FtpClient implements IFtpClient
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String server;

    private int    port;

    private String user;

    private String pass;

    public FtpClient(String server,
                     int port,
                     String user,
                     String pass)
    {
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    @Override
    public void deleteFile(String dir,
                           String fileName) throws FtpException
    {
        throw new NotYetImplementedException();
    }

    @Override
    public void downloadFile(String dir,
                             String fileName,
                             OutputStream os) throws FtpException
    {
        throw new NotYetImplementedException();
    }

    @Override
    public String[] listFiles(String dir) throws FtpException
    {
        throw new NotYetImplementedException();
    }

    public void upload(File file,
                       String location) throws FtpException
    {
        logger.debug("FtpClient: Start");
        FTPClient ftp = new FTPClient();
        ftp.setConnectTimeout(10000);
        try
        {
            ftp.connect(server, port);
        }
        catch ( IOException e )
        {
            logger.error(new StringBuilder("Errore nella connessione all'indirizzo ftp ").append(server).append(":").append(port).toString(), e);
            throw new FtpException(new StringBuilder("Errore nella connessione all'indirizzo ").append(server).append(":").append(port).toString());
        }
        try
        {
            ftp.login(user, pass);
            ftp.enterLocalPassiveMode();
        }
        catch ( IOException e )
        {
            logger.error("Errore nel login al server ft.", e);
            throw new FtpException("Errore nel login al server ftp");
        }
        try
        {
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
        }
        catch ( IOException e1 )
        {
            logger.error("Errore nell'impostazione del tipo di trasferimento binario", e1);
            throw new FtpException("Errore nell'impostazione del tipo di trasferimento binario");
        }
        InputStream input;
        try
        {
            input = new FileInputStream(file);
        }
        catch ( FileNotFoundException e1 )
        {
            logger.error(new StringBuilder("Il file da inviare ").append(file.getAbsolutePath()).append(" non esiste oppure è una directory oppure non può essere letto").toString(), e1);
            throw new FtpException(new StringBuilder("Il file da inviare ").append(file.getAbsolutePath()).append(" non esiste oppure è una directory oppure non può essere letto").toString());
        }
        boolean result = false;
        try
        {
            logger.debug("FtpClient: upload del file.");
            result = ftp.storeFile(location + FilenameUtils.getName(file.getName()), input);
            if ( !result )
            {
                logger.error(new StringBuilder("Il trasferimento del file ").append(file.getAbsolutePath()).append(" non è stato concluso con successo").toString());
                throw new FtpException(new StringBuilder("Il trasferimento del file ").append(file.getAbsolutePath()).append(" non è stato concluso con successo").toString());
            }
        }
        catch ( IOException e )
        {
            logger.error(new StringBuilder("Errore nell'upload via ftp del file ").append(file.getAbsolutePath()).toString(), e);
            throw new FtpException(new StringBuilder("Errore nell'upload via ftp del file ").append(file.getAbsolutePath()).toString());
        }
        try
        {
            logger.debug("FtpClient: upload eseguito.");
            input.close();
            ftp.disconnect();
        }
        catch ( IOException e )
        {
            logger.error("Errore nella disconnessione dal server ftp", e);
            throw new FtpException("Errore nella disconnessione dal server ftp");
        }
        logger.debug("FtpClient: End.");
    }

    public Date getFileInfo(String dir,
                            String fileName) throws FtpException
    {
        throw new NotYetImplementedException();
    }
}

