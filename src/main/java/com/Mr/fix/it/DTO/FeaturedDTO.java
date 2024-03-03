package com.Mr.fix.it.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedDTO
{
    private Long id;

    private Long workerID;

    private LocalDateTime startDate;

    private LocalDateTime expiryDate;
}
