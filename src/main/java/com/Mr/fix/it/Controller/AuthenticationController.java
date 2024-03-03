package com.Mr.fix.it.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;

import com.Mr.fix.it.Request.FcmRequest;
import com.Mr.fix.it.Request.UserRegisterRequest;
import com.Mr.fix.it.Request.WorkerRegisterRequest;
import com.Mr.fix.it.Request.AuthenticationRequest;

import com.Mr.fix.it.Response.TokensResponse;
import com.Mr.fix.it.Response.CategoriesResponse;
import com.Mr.fix.it.Response.GenericResponse;
import com.Mr.fix.it.Response.AuthenticationResponse;

import com.Mr.fix.it.Service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController
{
    private final AuthenticationService authenticationService;

    @PostMapping("/register-client")
    public ResponseEntity<GenericResponse> register(
        @Valid
        @ModelAttribute
        UserRegisterRequest request,
        BindingResult result
    ) throws
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(authenticationService.registerClient(request,result));
    }

    @PostMapping("/register-worker")
    public ResponseEntity<GenericResponse> register(
        @Valid
        @ModelAttribute
        WorkerRegisterRequest request,
        BindingResult result
    ) throws
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(authenticationService.registerWorker(request,result));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
        @Valid
        @RequestBody
        AuthenticationRequest request,
        BindingResult result
    ) throws
        ValidationException,
        NotFoundException
    {
        return ResponseEntity.ok(authenticationService.authenticate(request,result));
    }

    @PostMapping("/update-fcm")
    public ResponseEntity<GenericResponse> updateFCM(
        @Valid
        @RequestBody
        FcmRequest request,
        BindingResult result,
        @RequestHeader("Authorization")
        String token
    ) throws
        ValidationException,
        NotFoundException
    {
        return ResponseEntity.ok(
            authenticationService.updateFCM(
                request,
                result,
                token.substring(7)
            )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponse> logout(
        @RequestHeader("Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            authenticationService.logout(
                token.substring(7)
            )
        );
    }

    @GetMapping("/verify")
    public ModelAndView verify(
        @RequestParam("token")
        @NotEmpty(message = "missing token")
        String token,
        ModelAndView modelAndView
    )
    {
        modelAndView.setViewName("response");

        if (authenticationService.verifyUser(token))
        {
            modelAndView.addObject("status", "Succeeded!");
            modelAndView.addObject("message", "You can now log in using your username and password.");
        }
        else
        {
            modelAndView.addObject("status", "Failed!");
            modelAndView.addObject("message", "This link has expired.");
        }

        return modelAndView;
    }

    @GetMapping("/forget-password-request")
    public ResponseEntity<GenericResponse> forgetPasswordRequest(
        @RequestParam("email")
        @Email
        @NotEmpty(message = "missing email")
        @Size(max = 50, message = "email exceeds maximum length of 50 character")
        String email
    ) throws NotFoundException
    {
        return ResponseEntity.ok(authenticationService.forgetPasswordRequest(email));
    }

    @GetMapping("/reset-password-form")
    public ModelAndView resetPassword(
        @RequestParam("token")
        @NotEmpty(message = "missing token")
        String token,
        ModelAndView modelAndView
    )
    {
        if(!authenticationService.checkResetLinkExpiration(token))
        {
            modelAndView.setViewName("reset-password");
            modelAndView.addObject("token", token);
        }
        else
        {
            modelAndView.setViewName("response");
            modelAndView.addObject("title", "Expired Link");
            modelAndView.addObject("status", "Failed!");
            modelAndView.addObject("message", "This link has expired.");
        }

        return modelAndView;
    }

    @PostMapping("/save-new-password")
    public ModelAndView saveNewPassword(
        @NotBlank(message = "missing password")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "invalid password"
        )
        @RequestParam
        String password,
        @NotBlank(message = "missing token")
        @RequestParam
        String token,
        ModelAndView modelAndView
    )
    {
        modelAndView.setViewName("response");

        if(authenticationService.changeUserPassword(token,password))
        {
            modelAndView.addObject("title", "Reset Password");
            modelAndView.addObject("status", "Succeeded!");
            modelAndView.addObject("message", "Password changed successfully.");
        }
        else
        {
            modelAndView.addObject("title", "Reset Password");
            modelAndView.addObject("status", "Failed!");
            modelAndView.addObject("message", "This link has expired, or something went wrong, please try again later.");
        }

        return modelAndView;
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<TokensResponse> refreshToken(@RequestHeader("Authorization") String refreshToken)
    {
        refreshToken = refreshToken.substring(7);
        return ResponseEntity.ok(authenticationService.refreshToken(refreshToken));
    }

    @GetMapping("/get-categories")
    public ResponseEntity<CategoriesResponse> getCategories()
    {
        return ResponseEntity.ok(authenticationService.getCategories());
    }
}