package com.Mr.fix.it.Request;

import lombok.*;
import com.Mr.fix.it.Validator.Annotation.ValidImageFile;

import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageRequest
{
    @ValidImageFile(message = "invalid or missing image file")
    private MultipartFile img;
}
