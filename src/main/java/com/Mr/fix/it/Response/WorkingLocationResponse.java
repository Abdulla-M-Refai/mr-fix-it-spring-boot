package com.Mr.fix.it.Response;

import lombok.*;

import com.Mr.fix.it.DTO.WorkingLocationDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkingLocationResponse
{
    private WorkingLocationDTO workingLocation;
}
