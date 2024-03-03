package com.Mr.fix.it.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.io.IOException;

import com.Mr.fix.it.Service.AdsService;

import com.Mr.fix.it.Request.ImageRequest;

import com.Mr.fix.it.Response.AdResponse;
import com.Mr.fix.it.Response.GenericResponse;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;

@Validated
@RestController
@RequestMapping("/api/worker")
@RequiredArgsConstructor
public class AdsController
{
    private final AdsService adsService;

    @PostMapping("/share-ad")
    public ResponseEntity<AdResponse> shareAd(
        @Valid
        @ModelAttribute
        ImageRequest imageRequest,
        BindingResult result,
        @RequestHeader(name="Authorization")
        String token
    ) throws
        NotFoundException,
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(
            adsService.shareAd(
                imageRequest,
                result,
                token.substring(7)
            )
        );
    }

    @PostMapping("/delete-ad/{id}")
    public ResponseEntity<GenericResponse> deleteAd(
        @PathVariable
        String id,
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            adsService.deleteAd(
                Long.parseLong(id),
                token.substring(7)
            )
        );
    }
}
