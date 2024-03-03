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
public class FeaturedWorkerDTO
{
    private Long id;

    private WorkerDTO worker;

    private LocalDateTime startDate;

    private LocalDateTime expiryDate;
}
