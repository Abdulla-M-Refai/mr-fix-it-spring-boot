package com.Mr.fix.it.Exception.ExceptionType;

public class NotFoundException extends RuntimeException
{
    public NotFoundException(String message)
    {
        super(message);
    }

    public NotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NotFoundException(Throwable cause)
    {
        super(cause);
    }
}