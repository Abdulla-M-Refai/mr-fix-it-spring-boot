package com.Mr.fix.it.Response;

import lombok.*;

import com.Mr.fix.it.DTO.CategoryDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse
{
    private CategoryDTO category;
}
