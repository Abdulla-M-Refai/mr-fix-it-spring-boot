package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelTaskRequest
{
    @NotBlank(message = "missing reason")
    private String reason;
}
