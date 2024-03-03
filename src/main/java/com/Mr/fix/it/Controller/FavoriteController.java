package com.Mr.fix.it.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.Mr.fix.it.Service.FavoriteService;

import com.Mr.fix.it.Response.GenericResponse;
import com.Mr.fix.it.Request.UpdateFavoriteRequest;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Validated
public class FavoriteController
{
    private final FavoriteService favoriteService;

    @PostMapping("/update-favorite")
    public ResponseEntity<GenericResponse> updateFavorite(
        @Valid
        @RequestBody
        UpdateFavoriteRequest updateFavoriteRequest,
        BindingResult result,
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            favoriteService.updateFavorite(
                updateFavoriteRequest,
                result,
                token.substring(7)
            )
        );
    }
}
