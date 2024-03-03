package com.Mr.fix.it.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.Mr.fix.it.Service.WorkerReelService;

import com.Mr.fix.it.Request.ReelRequest;
import com.Mr.fix.it.Request.CommentRequest;

import com.Mr.fix.it.Response.ReelResponse;
import com.Mr.fix.it.Response.ReelsResponse;
import com.Mr.fix.it.Response.CommentResponse;
import com.Mr.fix.it.Response.GenericResponse;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

import java.io.IOException;

@Validated
@RestController
@RequestMapping({"/api/worker","/api/client"})
@RequiredArgsConstructor
public class ReelsController
{
    private final WorkerReelService workerReelService;

    @PostMapping("/post-comment/{id}")
    public ResponseEntity<CommentResponse> postComment(
        @PathVariable
        String id,
        @Valid
        @RequestBody
        CommentRequest request,
        BindingResult result,
        @RequestHeader("Authorization")
        String token
    ) throws
        NotFoundException,
        ValidationException
    {
        return ResponseEntity.ok(
            workerReelService.postComment(
                Long.parseLong(id),
                request,
                result,
                token.substring(7)
            )
        );
    }

    @GetMapping("/get-worker-reels")
    public ResponseEntity<ReelsResponse> getWorkerReels(
        @RequestHeader("Authorization")
        String token
    ) throws
        NotFoundException,
        NotAuthorizedException
    {
        return ResponseEntity.ok(
            workerReelService.getWorkerReels(
                token.substring(7)
            )
        );
    }

    @GetMapping("/get-reels")
    public ResponseEntity<ReelsResponse> getReels(
        @RequestHeader("Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            workerReelService.getReels(token.substring(7))
        );
    }

    @PostMapping("/add-reel")
    public ResponseEntity<ReelResponse> addReel(
        @Valid
        @ModelAttribute
        ReelRequest request,
        BindingResult result,
        @RequestHeader("Authorization")
        String token
    ) throws
        NotFoundException,
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(
            workerReelService.addReel(
              request,
              result,
              token.substring(7)
            )
        );
    }

    @PostMapping("/delete-reel/{id}")
    public ResponseEntity<GenericResponse> deleteReel(
        @PathVariable
        String id,
        @RequestHeader("Authorization")
        String token
    ) throws
        NotFoundException,
        NotAuthorizedException
    {
        return ResponseEntity.ok(
            workerReelService.deleteReel(
                Long.parseLong(id),
                token.substring(7)
            )
        );
    }
}
