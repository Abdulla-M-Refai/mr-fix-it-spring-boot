package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

import com.Mr.fix.it.DTO.UserDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientsResponse
{
    private List<UserDTO> users;
}
