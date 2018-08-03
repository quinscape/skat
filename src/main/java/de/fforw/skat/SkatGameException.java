package de.fforw.skat;

public class SkatGameException
    extends RuntimeException
{
    private static final long serialVersionUID = 2808686035488492434L;


    public SkatGameException(String message)
    {
        super(message);
    }


    public SkatGameException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public SkatGameException(Throwable cause)
    {
        super(cause);
    }


    protected SkatGameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
