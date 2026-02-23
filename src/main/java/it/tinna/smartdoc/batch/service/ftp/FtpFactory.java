package it.tinna.smartdoc.batch.service.ftp;

import lombok.Data;

@Data
public class FtpFactory
{
    private String protocol;

    private String server;

    private int    port;

    private String user;

    private String pass;

    public IFtpClient getFtpClient()
    {
        if ( null == protocol || !protocol.equals("TLS") )
        {
            // Client Ftp normale.
            return new FtpClient(server, port, user, pass);
        }
        else
        {
            // Client sFtp.
            return new FtpTlsClient(server, port, user, pass);
        }

    }

}

