package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkingLocationRequest
{
    @NotBlank(message = "missing locality")
    @Size(max = 50, message = "locality exceeds maximum length of 50 characters")
    private String locality;

    @NotBlank(message = "missing latitude")
    @Pattern(regexp = "^-?\\d+(\\.\\d+)?$", message = "latitude must be a valid number")
    private String latitude;

    @NotBlank(message = "missing longitude")
    @Pattern(regexp = "^-?\\d+(\\.\\d+)?$", message = "longitude must be a valid number")
    private String longitude;

    public Double getParsedLatitude()
    {
        return Double.parseDouble(latitude);
    }

    public Double getParsedLongitude()
    {
        return Double.parseDouble(longitude);
    }
}
