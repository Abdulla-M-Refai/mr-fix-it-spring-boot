package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferTaskRequest
{
    @NotBlank(message = "missing task id")
    @Pattern(regexp = "\\d+", message = "task id must be a valid number")
    private String taskID;

    @NotBlank(message = "missing price")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "price must be a valid number")
    private String price;

    public Long getParsedTaskID()
    {
        return Long.parseLong(taskID);
    }

    public Double getParsedPrice()
    {
        return Double.parseDouble(price);
    }
}
