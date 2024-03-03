package com.Mr.fix.it.Validator.Annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;

import jakarta.validation.Payload;

import com.Mr.fix.it.Validator.Implementation.ValidImageFileValidator;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImageFileValidator.class)
public @interface ValidImageFile
{
    String message() default "invalid image file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}