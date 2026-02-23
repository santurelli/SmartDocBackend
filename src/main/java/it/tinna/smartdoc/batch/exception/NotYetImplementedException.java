package it.tinna.smartdoc.batch.exception;

@SuppressWarnings("serial")
public class NotYetImplementedException extends RuntimeException
{

    public NotYetImplementedException()
    {
        this("Not yet implemented!");
    }

    public NotYetImplementedException(String msg,
                                      Throwable root)
    {
        super(msg, root);
    }

    public NotYetImplementedException(Throwable root)
    {
        super(root);
    }

    public NotYetImplementedException(String s)
    {
        super(s);
    }

}

