package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import com.Mr.fix.it.Validator.Annotation.ValidImageFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest
{
    @NotBlank(message = "missing first name")
    @Size(max = 50, message = "first name exceeds maximum length of 50 character")
    private String firstName;

    @NotBlank(message = "missing last name")
    @Size(max = 50, message = "last name exceeds maximum length of 50 character")
    private String lastName;

    @NotBlank(message = "missing dob")
    @Pattern(
        regexp = "^\\d{4}-\\d{1,2}-\\d{1,2}$",
        message = "invalid dob"
    )
    private String dob;

    @NotBlank(message = "missing gender")
    @Pattern(
        regexp = "^(MALE|FEMALE)$",
        message = "invalid gender"
    )
    private String gender;

    @NotBlank(message = "missing city")
    @Size(max = 50, message = "city exceeds maximum length of 50 character")
    private String city;

    @NotBlank(message = "missing email")
    @Email(message = "invalid email")
    @Size(max = 50, message = "email exceeds maximum length of 50 character")
    private String email;

    @NotBlank(message = "missing password")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "invalid password"
    )
    private String password;

    @NotBlank(message = "missing phone")
    @Pattern(
        regexp = "^(?:\\+972|\\+970)?[0-9]{9}$|^[0-9]{10}$",
        message = "invalid phone"
    )
    private String phone;

    @ValidImageFile(message = "invalid or missing image file")
    private MultipartFile profilePicture;
}