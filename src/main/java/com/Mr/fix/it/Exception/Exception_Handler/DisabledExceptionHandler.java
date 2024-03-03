package com.Mr.fix.it.Exception.Exception_Handler;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.core.Ordered;

import org.springframework.security.authentication.DisabledException;
import com.Mr.fix.it.Exception.ExceptionResponse.ExceptionResponse;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DisabledExceptionHandler
{
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException disabledException)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.UNAUTHORIZED.value(),
                disabledException.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(exceptionResponse,HttpStatus.UNAUTHORIZED);
    }
}