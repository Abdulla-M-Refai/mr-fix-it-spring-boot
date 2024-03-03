package com.Mr.fix.it.Response;

import lombok.*;

import com.Mr.fix.it.DTO.AdsDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdResponse
{
    private AdsDTO ad;
}
