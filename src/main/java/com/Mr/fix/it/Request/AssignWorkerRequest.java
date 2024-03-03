package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignWorkerRequest
{
    @NotBlank(message = "missing task id")
    @Pattern(regexp = "\\d+", message = "task id must be a valid number")
    private String taskID;

    @NotBlank(message = "missing worker id")
    @Pattern(regexp = "\\d+", message = "worker id must be a valid number")
    private String workerID;

    @NotBlank(message = "missing price")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "price must be a valid number")
    private String price;

    public Long getParsedTaskID()
    {
        return Long.parseLong(taskID);
    }

    public Long getParsedWorkerID()
    {
        return Long.parseLong(workerID);
    }

    public Double getParsedPrice()
    {
        return Double.parseDouble(price);
    }
}
