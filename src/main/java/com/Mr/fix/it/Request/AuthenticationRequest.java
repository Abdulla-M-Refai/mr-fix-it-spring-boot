package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest
{
    @NotBlank(message = "missing email")
    @Email(message = "invalid email")
    @Size(max = 50, message = "email exceeds maximum length of 50 character")
    private String email;

    @NotBlank(message = "missing password")
    @Pattern(
        regexp = "^.{8,16}$",
        message = "invalid password"
    )
    private String password;
}