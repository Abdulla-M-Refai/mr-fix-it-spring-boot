package com.Mr.fix.it.Request;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest
{
    @NotBlank(message = "missing category")
    private String category;
}
