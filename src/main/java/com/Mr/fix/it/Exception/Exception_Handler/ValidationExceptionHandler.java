package com.Mr.fix.it.Exception.Exception_Handler;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.core.Ordered;

import com.Mr.fix.it.Exception.ExceptionResponse.ExceptionResponse;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler
{
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponse> handleException(ValidationException validationException)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                validationException.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(exceptionResponse,HttpStatus.BAD_REQUEST);
    }
}