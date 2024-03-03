package com.Mr.fix.it.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.io.IOException;

import com.Mr.fix.it.Service.UserUpdateService;

import com.Mr.fix.it.Request.ImageRequest;
import com.Mr.fix.it.Request.UserUpdateRequest;
import com.Mr.fix.it.Request.UserUpdatePasswordRequest;

import com.Mr.fix.it.Response.GenericResponse;

import com.Mr.fix.it.Exception.ExceptionType.UniqueException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;

@RestController
@RequestMapping({"/api/client", "/api/worker"})
@RequiredArgsConstructor
@Validated
public class UpdateUserController
{
    private final UserUpdateService userUpdateService;

    @PostMapping("/update-user")
    public ResponseEntity<GenericResponse> updateUser(
        @Valid
        @RequestBody
        UserUpdateRequest userUpdateRequest,
        BindingResult result,
        @RequestHeader(name="Authorization")
        String token
    ) throws
        ValidationException,
        UniqueException
    {
        return ResponseEntity.ok(
            userUpdateService.updateUser(
                userUpdateRequest,
                result,
                token.substring(7)
            )
        );
    }

    @PostMapping("/update-user-img")
    public ResponseEntity<GenericResponse> updateUserImg(
        @Valid
        @ModelAttribute
        ImageRequest imageRequest,
        BindingResult result,
        @RequestHeader(name="Authorization")
        String token
    ) throws
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(
            userUpdateService.updateUserImg(
                imageRequest,
                result,
                token.substring(7)
            )
        );
    }

    @PostMapping("/update-user-password")
    public ResponseEntity<GenericResponse> updateUserPassword(
        @Valid
        @RequestBody
        UserUpdatePasswordRequest userUpdatePasswordRequest,
        BindingResult result,
        @RequestHeader(name="Authorization")
        String token
    ) throws ValidationException
    {
        return ResponseEntity.ok(
            userUpdateService.updateUserPassword(
                userUpdatePasswordRequest,
                result,
                token.substring(7)
            )
        );
    }
}
