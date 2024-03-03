package com.Mr.fix.it.Response;

import lombok.*;

import com.Mr.fix.it.DTO.FeaturedDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedResponse
{
    private FeaturedDTO featured;
}
