package com.Mr.fix.it.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdsDTO
{
    private long id;

    private long workerID;

    private String firstName;

    private String lastName;

    private String poster;

    private LocalDateTime startDate;

    private LocalDateTime expiryDate;
}
