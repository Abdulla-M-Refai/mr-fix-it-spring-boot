package com.Mr.fix.it.Validator.Annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;

import jakarta.validation.Payload;

import com.Mr.fix.it.Validator.Implementation.ValidVideoFileValidator;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidVideoFileValidator.class)
public @interface ValidVideoFile
{
    String message() default "invalid video file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}