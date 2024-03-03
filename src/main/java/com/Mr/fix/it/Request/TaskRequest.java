package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.Mr.fix.it.Validator.Annotation.ValidImagesListFiles;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest
{
    @NotBlank(message = "missing user id")
    @Pattern(regexp = "\\d+", message = "user id must be a valid number")
    private String userID;

    @NotBlank(message = "missing locality")
    @Size(max = 50, message = "locality exceeds maximum length of 50 characters")
    private String locality;

    @NotBlank(message = "missing latitude")
    @Pattern(regexp = "^-?\\d+(\\.\\d+)?$", message = "latitude must be a valid number")
    private String latitude;

    @NotBlank(message = "missing longitude")
    @Pattern(regexp = "^-?\\d+(\\.\\d+)?$", message = "longitude must be a valid number")
    private String longitude;

    @NotBlank(message = "missing title")
    @Size(max = 50, message = "title exceeds maximum length of 50 characters")
    private String title;

    @NotBlank(message = "missing description")
    private String description;

    @ValidImagesListFiles(message = "invalid or missing images files")
    private List<MultipartFile> taskImg;

    public Long getParsedUserID()
    {
        return Long.parseLong(userID);
    }

    public Double getParsedLatitude()
    {
        return Double.parseDouble(latitude);
    }

    public Double getParsedLongitude()
    {
        return Double.parseDouble(longitude);
    }
}
