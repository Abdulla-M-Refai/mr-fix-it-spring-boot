package com.Mr.fix.it.Response;

import lombok.*;

import com.Mr.fix.it.DTO.UserDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse
{
    private UserDTO user;

    private TokensResponse tokens;
}