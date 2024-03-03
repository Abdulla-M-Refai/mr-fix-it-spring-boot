package com.Mr.fix.it.Exception.Exception_Handler;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.core.Ordered;

import io.jsonwebtoken.ExpiredJwtException;
import com.Mr.fix.it.Exception.ExceptionResponse.ExceptionResponse;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExpiredJwtExceptionHandler
{
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleException(ExpiredJwtException expiredJwtException)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.UNAUTHORIZED.value(),
                expiredJwtException.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(exceptionResponse,HttpStatus.UNAUTHORIZED);
    }
}
