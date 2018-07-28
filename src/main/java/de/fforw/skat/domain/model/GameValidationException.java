package de.fforw.skat.domain.model;

public class GameValidationException
    extends SkatRuntimeException
{
    private static final long serialVersionUID = 2003216692259169200L;


    public GameValidationException(String message)
    {
        super(message);
    }


    public GameValidationException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public GameValidationException(Throwable cause)
    {
        super(cause);
    }


    public GameValidationException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    )
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
