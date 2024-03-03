package com.Mr.fix.it.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;

import com.Mr.fix.it.Entity.Enum.UserType;

@Data
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO
{
    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate dob;

    private String gender;

    private String city;

    private String email;

    private String phone;

    private String img;
    
    private UserType type;

    private List<WorkerDTO> favorites;

    private Boolean enabled;

    private LocalDateTime createdAt;
}