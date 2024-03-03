package com.Mr.fix.it.Response;

import com.Mr.fix.it.DTO.FeaturedWorkerDTO;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedsResponse
{
    private List<FeaturedWorkerDTO> featureds;
}
