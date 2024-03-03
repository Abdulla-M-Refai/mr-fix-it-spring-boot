package com.Mr.fix.it.Validator.Implementation;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import com.Mr.fix.it.Validator.Annotation.ValidVideoFile;

@Component
public class ValidVideoFileValidator implements ConstraintValidator<ValidVideoFile, MultipartFile>
{
    @Override
    public boolean isValid(MultipartFile video, ConstraintValidatorContext constraintValidatorContext)
    {
        return video!=null && !video.isEmpty() && Objects.requireNonNull(video.getContentType()).startsWith("video/");
    }
}