package de.quinscape.domainql.skat.util;

import de.quinscape.domainql.skat.runtime.SkatRuntimeException;

public class MaximumGameValueExceedException
    extends SkatRuntimeException
{
    private static final long serialVersionUID = 2481194118380785796L;


    public MaximumGameValueExceedException(String message)
    {
        super(message);
    }


    public MaximumGameValueExceedException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public MaximumGameValueExceedException(Throwable cause)
    {
        super(cause);
    }


    public MaximumGameValueExceedException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    )
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
