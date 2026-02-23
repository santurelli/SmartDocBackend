package it.tinna.smartdoc.shared.exception;

@SuppressWarnings("serial")
public class TokenVerificationException extends RuntimeException
{
    public TokenVerificationException(Throwable t)
    {
        super(t);
    }

}

