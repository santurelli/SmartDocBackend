package it.tinna.smartdoc.batch.exception;

public class FtpException extends Exception
{
    private static final long serialVersionUID = 1L;

    public FtpException(String msg,
                        Throwable root)
    {
        super(msg, root);
    }

    public FtpException(Throwable root)
    {
        super(root);
    }

    public FtpException(String s)
    {
        super(s);
    }

}

