package com.Mr.fix.it.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkerDTO extends UserDTO
{
    private long workerID;

    private CategoryDTO category;

    private float rate;

    private List<WorkingLocationDTO> workingLocations;

    private List<PreviousWorkDTO> previousWorks;

    private List<AdsDTO> ads;
}
