package com.Mr.fix.it.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationDTO
{
    private long id;

    private UserDTO user;

    private int amount;

    private LocalDateTime donationDate;
}
