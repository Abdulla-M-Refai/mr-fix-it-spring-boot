package com.Mr.fix.it.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO
{
    private long id;

    private long taskID;

    private WorkerDTO worker;

    private double price;
}