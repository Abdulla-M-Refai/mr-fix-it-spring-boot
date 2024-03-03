package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

import com.Mr.fix.it.DTO.WorkerDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkersResponse
{
    private List<WorkerDTO> workers;
}
