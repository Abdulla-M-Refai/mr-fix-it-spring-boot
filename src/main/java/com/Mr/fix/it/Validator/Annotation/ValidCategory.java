package com.Mr.fix.it.Validator.Annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;

import jakarta.validation.Payload;

import com.Mr.fix.it.Validator.Implementation.CategoryValidator;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryValidator.class)
public @interface ValidCategory
{
    String message() default "invalid category";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}