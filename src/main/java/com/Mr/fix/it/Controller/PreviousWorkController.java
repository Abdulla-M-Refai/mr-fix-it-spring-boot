package com.Mr.fix.it.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.io.IOException;

import com.Mr.fix.it.Service.PreviousWorkService;

import com.Mr.fix.it.Request.PreviousWorkRequest;

import com.Mr.fix.it.Response.GenericResponse;
import com.Mr.fix.it.Response.PreviousWorkResponse;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;

@Validated
@RestController
@RequestMapping("/api/worker")
@RequiredArgsConstructor
public class PreviousWorkController
{
    private final PreviousWorkService previousWorkService;

    @PostMapping("/add-previous-work")
    public ResponseEntity<PreviousWorkResponse> addPreviousWork(
        @Valid
        @ModelAttribute
        PreviousWorkRequest previousWorkRequest,
        BindingResult result,
        @RequestHeader(name="Authorization")
        String token
    ) throws
        NotFoundException,
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(
            previousWorkService.addPreviousWork(
                previousWorkRequest,
                result,
                token.substring(7)
            )
        );
    }

    @PostMapping("/delete-previous-work/{id}")
    public ResponseEntity<GenericResponse> deletePreviousWork(
        @PathVariable
        String id,
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            previousWorkService.deletePreviousWork(
                Long.parseLong(id),
                token.substring(7)
            )
        );
    }
}
