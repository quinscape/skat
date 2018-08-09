package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.runtime.SkatRuntimeException;

public class ChannelUpdateException
    extends SkatRuntimeException
{
    private static final long serialVersionUID = -3882916679284381814L;


    public ChannelUpdateException(String message)
    {
        super(message);
    }


    public ChannelUpdateException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public ChannelUpdateException(Throwable cause)
    {
        super(cause);
    }


    public ChannelUpdateException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    )
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
