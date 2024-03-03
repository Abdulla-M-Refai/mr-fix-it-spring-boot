package com.Mr.fix.it.Exception.Exception_Handler;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.core.Ordered;

import com.Mr.fix.it.Exception.ExceptionResponse.ExceptionResponse;
import com.Mr.fix.it.Exception.ExceptionType.UniqueException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UniqueExceptionHandler
{
    @ExceptionHandler(UniqueException.class)
    public ResponseEntity<ExceptionResponse> handleException(UniqueException uniqueException)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.CONFLICT.value(),
                uniqueException.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(exceptionResponse,HttpStatus.CONFLICT);
    }
}