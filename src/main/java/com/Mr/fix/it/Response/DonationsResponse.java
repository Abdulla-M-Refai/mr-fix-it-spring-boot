package com.Mr.fix.it.Response;

import com.Mr.fix.it.DTO.DonationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationsResponse
{
    private List<DonationDTO> donations;
}
