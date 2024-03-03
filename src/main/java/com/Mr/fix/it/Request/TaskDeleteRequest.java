package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDeleteRequest
{
    @NotNull(message = "missing task id")
    @Min(value = 1, message = "invalid task id")
    Long id;
}
