package com.Mr.fix.it.Response;

import lombok.*;

import com.Mr.fix.it.DTO.ReelDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReelResponse
{
    private ReelDTO reel;
}
