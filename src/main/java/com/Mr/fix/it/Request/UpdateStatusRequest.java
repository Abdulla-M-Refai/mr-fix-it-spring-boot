package com.Mr.fix.it.Request;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusRequest
{
    @NotBlank(message = "missing task status")
    private String status;
}
