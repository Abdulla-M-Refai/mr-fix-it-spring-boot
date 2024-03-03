package com.Mr.fix.it.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCategoryStatisticsDTO
{
    private CategoryDTO category;

    private int total;

    private int totalCompleted;
}
