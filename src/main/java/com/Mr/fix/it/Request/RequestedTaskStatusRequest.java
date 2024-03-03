package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestedTaskStatusRequest
{
    @NotBlank(message = "missing task id")
    @Pattern(regexp = "\\d+", message = "task id must be a valid number")
    private String taskID;

    @NotBlank(message = "missing state")
    @Pattern(regexp = "\\b(?:true|false)\\b", message = "invalid state")
    private String state;

    public Long getParsedTaskID()
    {
        return Long.parseLong(taskID);
    }

    public Boolean getParsedState()
    {
        return Boolean.parseBoolean(state);
    }
}
