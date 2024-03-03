package com.Mr.fix.it.Exception.ExceptionType;

public class NotAuthorizedException extends RuntimeException
{
    public NotAuthorizedException(String message) {
        super(message);
    }

    public NotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthorizedException(Throwable cause) {
        super(cause);
    }
}