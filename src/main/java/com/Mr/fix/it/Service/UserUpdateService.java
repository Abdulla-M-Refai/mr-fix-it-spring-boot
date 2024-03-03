package com.Mr.fix.it.Service;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.validation.BindingResult;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

import com.Mr.fix.it.Config.Security.JwtService;

import com.Mr.fix.it.Entity.User;
import com.Mr.fix.it.Entity.Enum.Gender;

import com.Mr.fix.it.Repository.UserRepository;

import com.Mr.fix.it.Request.UserUpdateRequest;
import com.Mr.fix.it.Request.ImageRequest;
import com.Mr.fix.it.Request.UserUpdatePasswordRequest;

import com.Mr.fix.it.Response.GenericResponse;

import com.Mr.fix.it.Util.Helper;

import com.Mr.fix.it.Exception.ExceptionType.UniqueException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;

@Service
@RequiredArgsConstructor
public class UserUpdateService
{
    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final ImageUploadingService imageUploadingService;

    @Transactional
    public GenericResponse updateUser(
        UserUpdateRequest userUpdateRequest,
        BindingResult result,
        String token
    ) throws
        ValidationException,
        UniqueException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        String phone = user.getPhone();

        if(
            userRepository.findByEmail(userUpdateRequest.getEmail()).isPresent() &&
            !email.equalsIgnoreCase(userUpdateRequest.getEmail())
        ) throw new UniqueException("email already exists");

        if(
            userRepository.findByPhone(userUpdateRequest.getPhone()).isPresent() &&
            !phone.equalsIgnoreCase(userUpdateRequest.getPhone())
        ) throw new UniqueException("phone already exists");

        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());
        user.setDob(Helper.getLocalDate(userUpdateRequest.getDob()));
        user.setGender(Gender.valueOf(userUpdateRequest.getGender().toUpperCase()));
        user.setCity(userUpdateRequest.getCity());
        user.setEmail(userUpdateRequest.getEmail());
        user.setPhone(userUpdateRequest.getPhone());

        userRepository.save(user);

        return GenericResponse
            .builder()
            .state("success")
            .message("user updated successfully")
            .build();
    }

    @Transactional
    public GenericResponse updateUserImg(
        ImageRequest request,
        BindingResult result,
        String token
    ) throws
        ValidationException,
        IOException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        String fileName = Helper.generateFileName(request.getImg());
        File file = imageUploadingService.convertToFile(request.getImg(), fileName);
        String uri = imageUploadingService.uploadFile(file, fileName);

        user.setImg(uri);
        userRepository.save(user);

        return GenericResponse
            .builder()
            .state("success")
            .message(uri)
            .build();
    }

    @Transactional
    public GenericResponse updateUserPassword(
        UserUpdatePasswordRequest userUpdatePasswordRequest,
        BindingResult result,
        String token
    ) throws ValidationException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        if(!passwordEncoder
            .matches(
                userUpdatePasswordRequest.getOldPassword(),
                user.getPassword()
            )
        ) throw new NotFoundException("user not found");

        user.setPassword(passwordEncoder.encode(userUpdatePasswordRequest.getNewPassword()));
        userRepository.save(user);

        return GenericResponse
            .builder()
            .state("success")
            .message("password updated successfully")
            .build();
    }
}
