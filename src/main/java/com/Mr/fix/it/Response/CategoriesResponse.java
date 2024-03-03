package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

import com.Mr.fix.it.DTO.CategoryDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesResponse
{
    private List<CategoryDTO> categories;
}