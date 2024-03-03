package com.Mr.fix.it.Exception.Exception_Handler;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.core.Ordered;

import com.Mr.fix.it.Exception.ExceptionResponse.ExceptionResponse;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler
{
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(NotFoundException notFoundException)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.NOT_FOUND.value(),
                notFoundException.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(exceptionResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleException(NotAuthorizedException notAuthorizedException)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
            HttpStatus.UNAUTHORIZED.value(),
            notAuthorizedException.getMessage(),
            System.currentTimeMillis()
        );

        return new ResponseEntity<>(exceptionResponse,HttpStatus.UNAUTHORIZED);
    }
}