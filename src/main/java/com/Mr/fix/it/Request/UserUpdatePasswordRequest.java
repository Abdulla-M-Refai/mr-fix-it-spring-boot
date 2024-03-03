package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatePasswordRequest
{
    @NotBlank(message = "missing old password")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
        message = "invalid old password"
    )
    private String oldPassword;

    @NotBlank(message = "missing new password")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
        message = "invalid new password"
    )
    private String newPassword;
}
