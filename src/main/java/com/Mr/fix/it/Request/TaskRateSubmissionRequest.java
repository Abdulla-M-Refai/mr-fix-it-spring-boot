package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRateSubmissionRequest
{
    @NotBlank(message = "missing task id")
    @Pattern(regexp = "\\d+", message = "task id must be a valid number")
    private String taskID;

    @NotBlank(message = "missing perfection")
    @Pattern(regexp = "^(?:[0-5](?:\\.\\d+)?|5(?:\\.0)?)$", message = "perfection must be a valid number")
    private String perfection;

    @NotBlank(message = "missing treatment")
    @Pattern(regexp = "^(?:[0-5](?:\\.\\d+)?|5(?:\\.0)?)$", message = "treatment must be a valid number")
    private String treatment;

    @NotBlank(message = "missing additional info")
    private String additionalInfo;

    public Long getParsedTaskID()
    {
        return Long.parseLong(taskID);
    }

    public Float getParsedPerfection()
    {
        return Float.parseFloat(perfection);
    }

    public Float getParsedTreatment()
    {
        return Float.parseFloat(treatment);
    }
}
