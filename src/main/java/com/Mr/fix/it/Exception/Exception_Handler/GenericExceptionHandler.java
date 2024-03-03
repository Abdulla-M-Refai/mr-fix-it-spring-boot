package com.Mr.fix.it.Exception.Exception_Handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.Mr.fix.it.Exception.ExceptionResponse.ExceptionResponse;

@ControllerAdvice
public class GenericExceptionHandler
{
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
            HttpStatus.BAD_REQUEST.value(),
            exception.getMessage(),
            System.currentTimeMillis()
        );

        return new ResponseEntity<>(exceptionResponse,HttpStatus.BAD_REQUEST);
    }
}