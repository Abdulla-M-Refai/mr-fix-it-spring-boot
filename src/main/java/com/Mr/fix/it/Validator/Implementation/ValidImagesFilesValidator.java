package com.Mr.fix.it.Validator.Implementation;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.List;

import com.Mr.fix.it.Validator.Annotation.ValidImagesListFiles;

@Component
public class ValidImagesFilesValidator implements ConstraintValidator<ValidImagesListFiles, List<MultipartFile>>
{
    @Override
    public boolean isValid(List<MultipartFile> images, ConstraintValidatorContext constraintValidatorContext)
    {
        if(images == null || images.isEmpty())
            return false;

        for(MultipartFile image : images)
            if(!Objects.requireNonNull(image.getContentType()).startsWith("image/"))
                return false;

        return true;
    }
}
