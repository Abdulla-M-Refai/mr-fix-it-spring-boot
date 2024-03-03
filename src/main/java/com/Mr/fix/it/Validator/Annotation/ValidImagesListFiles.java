package com.Mr.fix.it.Validator.Annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;

import jakarta.validation.Payload;

import com.Mr.fix.it.Validator.Implementation.ValidImagesFilesValidator;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImagesFilesValidator.class)
public @interface ValidImagesListFiles
{
    String message() default "invalid images files";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
