package com.Mr.fix.it.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FcmRequest
{
    @NotBlank(message = "missing fcm")
    @Size(max = 255, message = "fcm exceeds maximum length of 255 character")
    private String fcm;
}
