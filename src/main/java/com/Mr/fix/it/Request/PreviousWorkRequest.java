package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import com.Mr.fix.it.Validator.Annotation.ValidImagesListFiles;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviousWorkRequest
{
    @NotBlank(message = "missing description")
    private String description;

    @ValidImagesListFiles(message = "invalid or missing images files")
    private List<MultipartFile> workImgs;
}
