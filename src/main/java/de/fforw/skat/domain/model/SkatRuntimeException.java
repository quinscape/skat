package de.fforw.skat.domain.model;

public class SkatRuntimeException
    extends RuntimeException
{
    private static final long serialVersionUID = 2074553315181786207L;

    public SkatRuntimeException(String message)
    {
        super(message);
    }


    public SkatRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public SkatRuntimeException(Throwable cause)
    {
        super(cause);
    }


    public SkatRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
