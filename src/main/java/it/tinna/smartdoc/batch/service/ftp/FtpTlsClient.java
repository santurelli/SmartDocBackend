package it.tinna.smartdoc.batch.service.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilters;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tinna.smartdoc.batch.exception.FtpException;

public class FtpTlsClient implements IFtpClient
{
    private Logger _log = LoggerFactory.getLogger(this.getClass());

    private String server;

    private int    port;

    private String user;

    private String pass;

    public FtpTlsClient(String server,
                        int port,
                        String user,
                        String pass)
    {
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    private FTPSClient createClient() throws FtpException
    {
        FTPSClient ftps = new FTPSClient("TLS");
        // ftps.addProtocolCommandListener(new Log4jCommandListener(System.out));
        // ftps.addProtocolCommandListener(new PrintCommandListener(System.out));
        ftps.setControlEncoding("UTF-8");
        // final String ipServer = this.server;
        // HostnameResolver resolver = new HostnameResolver()
        // {
        //
        // @Override
        // public String resolve(String hostname) throws UnknownHostException
        // {
        // // TODO Auto-generated method stub
        // _log.info("New HostnameResolver hostname= '" + hostname + "'");
        // _log.info("New HostnameResolver ipServer= '" + ipServer + "'");
        // return ipServer;
        // }
        // };
        // ftps.setPassiveNatWorkaroundStrategy(resolver);

        try
        {
            connect(ftps);
        }
        catch ( Exception e1 )
        {
            throw new FtpException(e1);
        }
        return ftps;
    }

    public void downloadFile(String dir,
                             String fileName,
                             OutputStream os) throws FtpException
    {
        FTPSClient ftps = null;
        try
        {
            ftps = createClient();
            try
            {
                if ( !ftps.changeWorkingDirectory(dir) )
                {
                    _log.error("Impossibile cambiare la directory di lavoro in {}", dir);
                    throw new FtpException("Impossibile cambiare la directory di lavoro");
                }
            }
            catch ( IOException e )
            {
                _log.error("Errore durante il tentativo di cambiare la directory di lavoro in {}", dir);
                throw new FtpException(e);
            }
            try
            {
                if ( !ftps.retrieveFile(fileName, os) )
                {
                    _log.error("Errore durante il download del file  {} dalla cartella {}", fileName, dir);
                    throw new FtpException(String.format("Errore durante il download del file  %s dalla cartella %s", fileName, dir));
                }
            }
            catch ( IOException e )
            {
                _log.error("Errore durante il download del file  {} dalla cartella {}", fileName, dir, e);
                throw new FtpException(e);
            }
        }
        finally
        {
            disconnect(ftps);
        }
    }

    public Date getFileInfo(String dir,
                            String fileName) throws FtpException
    {
        FTPSClient ftps = null;
        try
        {
            ftps = createClient();
            try
            {
                if ( !ftps.changeWorkingDirectory(dir) )
                {
                    _log.error("Impossibile cambiare la directory di lavoro in {}", dir);
                    throw new FtpException("Impossibile cambiare la directory di lavoro");
                }
            }
            catch ( IOException e )
            {
                _log.error("Errore durante il tentativo di cambiare la directory di lavoro in {}", dir, e);
                throw new FtpException(e);
            }
            try
            {
                FTPFile file = ftps.mdtmFile(fileName);
                if ( null == file )
                {
                    _log.error("Errore durante il download del file {} dalla cartella {}", fileName, dir);
                    throw new FtpException(String.format("Errore durante il download del file  %s dalla cartella %s", fileName, dir));
                }
                else
                {
                    Date ct = file.getTimestamp().getTime();
                    return ct;
                }
            }
            catch ( IOException e )
            {
                _log.error("Errore durante il download del file {} dalla cartella {}", fileName, dir, e);
                throw new FtpException(e);
            }
        }
        finally
        {
            disconnect(ftps);
        }
    }

    public void deleteFile(String dir,
                           String fileName) throws FtpException
    {
        FTPSClient ftps = null;
        try
        {
            ftps = createClient();
            try
            {
                if ( !ftps.changeWorkingDirectory(dir) )
                {
                    _log.error("Impossibile cambiare la directory di lavoro in {}", dir);
                    throw new FtpException("Impossibile cambiare la directory di lavoro");
                }
            }
            catch ( IOException e )
            {
                _log.error("Errore durante il tentativo di cambiare la directory di lavoro in {}", dir, e);
                throw new FtpException(e);
            }
            try
            {
                if ( !ftps.deleteFile(fileName) )
                {
                    _log.error("Errore durante la cancellazione del file {} dalla cartella {}", fileName, dir);
                    throw new FtpException(String.format("Errore durante la cancellazione del file  %s dalla cartella %s", fileName, dir));
                }
            }
            catch ( IOException e )
            {
                _log.error("Errore durante la cancellazione del file {} dalla cartella {}", fileName, dir, e);
                throw new FtpException(e);
            }
        }
        finally
        {
            disconnect(ftps);
        }

    }

    public String[] listFiles(String dir) throws FtpException
    {
        FTPSClient ftps = null;
        try
        {
            ftps = createClient();
            try
            {
                FTPFile[] list = ftps.listFiles(dir, FTPFileFilters.ALL);
                String[] result = new String[list.length];
                for ( int i = 0; i < list.length; i++ )
                {
                    result[i] = list[i].getName();
                }
                return result;
            }
            catch ( IOException e )
            {
                _log.error("Errore nel recupero dell'elenco file nella cartella {}", dir, e);
                throw new FtpException(e);
            }
        }
        finally
        {
            disconnect(ftps);
        }
    }

    @Override
    public void upload(File file,
                       String location) throws FtpException
    {
        _log.debug("FtpTlsClient: Start.");
        FTPSClient ftps = null;
        try
        {
            ftps = createClient();
            try
            {
                ftps.setFileType(FTP.BINARY_FILE_TYPE);
            }
            catch ( IOException e1 )
            {
                _log.error("Impossibile impostare il tipo file a binario");
                throw new FtpException(e1);
            }
            _log.debug("FtpTlsClient: upload del file.");
            try
            {
                if ( !ftps.changeWorkingDirectory(location) )
                {
                    _log.error("Impossibile cambiare la directory di lavoro in {}", location);
                    throw new FtpException("Impossibile cambiare la directory di lavoro");
                }
            }
            catch ( IOException e )
            {
                _log.error("Errore durante il tentativo di cambiare la directory di lavoro in {}", location, e);
                throw new FtpException("Errore durante il tentativo di cambiare la directory di lavoro");
            }
            try (InputStream input = new FileInputStream(file))
            {
                boolean result = ftps.storeFile(FilenameUtils.getName(file.getName()), input);
                if ( !result )
                {
                    _log.error("Il trasferimento del file {} non è stato completato", file.getAbsolutePath());
                    throw new FtpException(String.format("Il trasferimento del file %s non è stato completato", file.getAbsolutePath()));
                }
                _log.debug("FtpTlsClient: upload eseguito.");
            }
            catch ( IOException e )
            {
                _log.error("Errore durante l'upload del file {}", file.getAbsolutePath(), e);
                throw new FtpException(e);
            }

        }
        finally
        {
            disconnect(ftps);
        }
    }

    private void connect(FTPSClient ftp) throws Exception
    {
        try
        {
            ftp.setConnectTimeout(10000);
            int reply;
            ftp.connect(server, port);
            reply = ftp.getReplyCode();
            if ( !FTPReply.isPositiveCompletion(reply) )
            {
                ftp.disconnect();
                _log.error("Errore nella connessione all'indirizzo ftp {}:{}", server, port);
                throw new FtpException(String.format("Errore nella connessione all'indirizzo ftp %s:%d", server, port));
            }
        }
        catch ( IOException e )
        {
            _log.error("Errore nell'apertura del socket all'indirizzo ftp {}:{}", server, port, e);
            throw e;
        }
        try
        {
            ftp.execPBSZ(0);
            ftp.execPROT("P");
            ftp.enterLocalPassiveMode();
            if ( !ftp.login(user, pass) )
            {
                ftp.logout();
                _log.error("Errore nel login con utente: {} e password: {}", user, pass);
                throw new FtpException("Errore nel login con utente: " + user + " e password: " + pass);
            }
        }
        catch ( IOException e )
        {
            _log.error("Errore nel login al server ftp.", e);
            throw e;
        }
    }

    private void disconnect(FTPSClient ftp) throws FtpException
    {
        if ( ftp != null )
        {
            try
            {
                if ( ftp.isConnected() )
                {
                    ftp.disconnect();
                }
            }
            catch ( IOException e )
            {
                _log.error("Errore nella disconnessione dal server ftp", e);
                throw new FtpException(e);
            }
        }
    }
}

