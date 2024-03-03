package com.Mr.fix.it.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkingLocationDTO
{
    private long id;

    private String locality;

    private double latitude;

    private double longitude;
}
