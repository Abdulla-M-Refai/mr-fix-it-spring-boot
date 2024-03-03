package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest
{
    @NotBlank(message = "missing comment")
    private String comment;
}
