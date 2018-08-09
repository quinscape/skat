package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.runtime.SkatRuntimeException;

public class GameLogicException
    extends SkatRuntimeException
{
    private static final long serialVersionUID = -4466920519443689950L;


    public GameLogicException(String message)
    {
        super(message);
    }


    public GameLogicException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public GameLogicException(Throwable cause)
    {
        super(cause);
    }


    public GameLogicException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
