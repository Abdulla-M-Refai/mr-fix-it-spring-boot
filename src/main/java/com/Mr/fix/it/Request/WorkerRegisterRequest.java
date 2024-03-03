package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

import com.Mr.fix.it.Validator.Annotation.ValidCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkerRegisterRequest extends UserRegisterRequest
{
    @ValidCategory(message = "invalid or missing category")
    private String category;

    @Size(min=1, message = "missing working locations")
    private List<WorkingLocationRequest> workingLocations;
}