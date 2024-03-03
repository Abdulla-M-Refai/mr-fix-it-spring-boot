package com.Mr.fix.it.DTO;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviousWorkDTO
{
    private long id;

    private String description;

    private List<ImageDTO> previousWorkImgs;
}
