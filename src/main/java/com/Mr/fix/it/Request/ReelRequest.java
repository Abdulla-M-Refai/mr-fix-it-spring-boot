package com.Mr.fix.it.Request;

import lombok.*;

import org.springframework.web.multipart.MultipartFile;

import com.Mr.fix.it.Validator.Annotation.ValidVideoFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReelRequest
{
    @ValidVideoFile(message = "invalid or missing video file")
    private MultipartFile video;
}
