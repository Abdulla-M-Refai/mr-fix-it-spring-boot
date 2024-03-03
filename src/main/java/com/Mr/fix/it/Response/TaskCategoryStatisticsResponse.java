package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

import com.Mr.fix.it.DTO.TaskCategoryStatisticsDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCategoryStatisticsResponse
{
    private List<TaskCategoryStatisticsDTO> categoryStatistics;
}
