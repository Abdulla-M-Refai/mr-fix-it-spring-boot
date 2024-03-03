package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

import com.Mr.fix.it.DTO.WorkingLocationDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkingLocationsResponse
{
    private List<WorkingLocationDTO> workingLocations;
}
