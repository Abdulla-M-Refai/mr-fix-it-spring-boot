package com.Mr.fix.it.Response;

import lombok.*;

import com.Mr.fix.it.DTO.UserDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse
{
    private UserDTO user;
}
