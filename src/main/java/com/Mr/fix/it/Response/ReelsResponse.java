package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

import com.Mr.fix.it.DTO.ReelDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReelsResponse
{
    private List<ReelDTO> reels;
}
