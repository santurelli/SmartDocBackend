package it.tinna.smartdoc.batch.service.ftp;

import java.io.PrintStream;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ProtocolCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4jCommandListener extends PrintCommandListener
{

    private static Logger _log = LoggerFactory.getLogger(Log4jCommandListener.class);

    public Log4jCommandListener(PrintStream stream)
    {
        super(stream);
    }

    @Override
    public void protocolCommandSent(ProtocolCommandEvent event)
    {
        _log.debug(">");
        _log.debug("");
        _log.debug(event.getMessage());
    }

    @Override
    public void protocolReplyReceived(ProtocolCommandEvent event)
    {
        _log.debug("< ");
        _log.debug(event.getMessage());
    }

}

