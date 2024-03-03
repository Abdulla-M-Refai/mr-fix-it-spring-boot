package com.Mr.fix.it.Response;

import lombok.*;

import com.Mr.fix.it.DTO.TaskDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse
{
    TaskDTO task;
}
