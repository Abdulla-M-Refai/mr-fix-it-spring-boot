package com.Mr.fix.it.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO
{
    private long id;

    private String type;

    private int totalWorkers;
}