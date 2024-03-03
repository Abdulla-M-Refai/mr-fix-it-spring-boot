package com.Mr.fix.it.Response;

import com.Mr.fix.it.DTO.FeaturedWorkerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedWorkerResponse
{
    private FeaturedWorkerDTO featured;
}
