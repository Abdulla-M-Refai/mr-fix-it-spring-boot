package com.Mr.fix.it.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.Mr.fix.it.Response.GenericResponse;
import com.Mr.fix.it.Request.WorkingLocationRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.Mr.fix.it.Service.WorkingLocationService;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@Validated
@RestController
@RequestMapping("/api/worker")
@RequiredArgsConstructor
public class WorkingLocationController
{
    private final WorkingLocationService workingLocationService;

    @PostMapping("delete-working-location/{id}")
    public ResponseEntity<GenericResponse> deleteWorkingLocation(
        @PathVariable
        String id,
        @RequestHeader("Authorization")
        String token
    ) throws
        NotAuthorizedException,
        NotFoundException
    {
        return ResponseEntity.ok(
              workingLocationService.deleteWorkingLocation(
                  Long.parseLong(id),
                  token.substring(7)
              )
        );
    }

    @PostMapping("add-working-location")
    public ResponseEntity<GenericResponse> addWorkingLocation(
        @Valid
        @RequestBody
        WorkingLocationRequest workingLocationRequest,
        BindingResult bindingResult,
        @RequestHeader("Authorization")
        String token
    ) throws
        ValidationException,
        NotFoundException
    {
        return ResponseEntity.ok(
            workingLocationService.addWorkingLocation(
                workingLocationRequest,
                bindingResult,
                token.substring(7)
            )
        );
    }
}
