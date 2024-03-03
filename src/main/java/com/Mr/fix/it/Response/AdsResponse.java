package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

import com.Mr.fix.it.DTO.AdsDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdsResponse
{
    private List<AdsDTO> ads;
}
