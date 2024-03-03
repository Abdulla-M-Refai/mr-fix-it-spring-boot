package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequestedTaskRequest extends TaskRequest
{
    @NotBlank(message = "missing worker id")
    @Pattern(regexp = "\\d+", message = "worker id must be a valid number")
    private String workerID;

    @NotBlank(message = "missing price")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "price must be a valid number")
    private String price;

    public Long getParsedWorkerID()
    {
        return Long.parseLong(workerID);
    }

    public Double getParsedPrice()
    {
        return Double.parseDouble(price);
    }
}
