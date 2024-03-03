package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

import com.Mr.fix.it.DTO.TaskDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TasksResponse
{
    private List<TaskDTO> tasks;
}
