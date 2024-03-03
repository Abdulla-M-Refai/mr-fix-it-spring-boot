package com.Mr.fix.it.Validator.Implementation;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import com.Mr.fix.it.Validator.Annotation.ValidImageFile;

@Component
public class ValidImageFileValidator implements ConstraintValidator<ValidImageFile, MultipartFile>
{
    @Override
    public boolean isValid(MultipartFile image, ConstraintValidatorContext constraintValidatorContext)
    {
        return image!=null && !image.isEmpty() && Objects.requireNonNull(image.getContentType()).startsWith("image/");
    }
}