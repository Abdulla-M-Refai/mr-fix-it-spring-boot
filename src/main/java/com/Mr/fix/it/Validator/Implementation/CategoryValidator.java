package com.Mr.fix.it.Validator.Implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.Mr.fix.it.Repository.CategoryRepository;
import com.Mr.fix.it.Validator.Annotation.ValidCategory;

@Component
@RequiredArgsConstructor
public class CategoryValidator implements ConstraintValidator<ValidCategory, String>
{
    private final CategoryRepository categoryRepository;

    @Override
    public boolean isValid(String type, ConstraintValidatorContext constraintValidatorContext)
    {
        return categoryRepository.findByType(type).isPresent();
    }
}