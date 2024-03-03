package com.Mr.fix.it.Response;

import lombok.*;
import com.Mr.fix.it.DTO.PreviousWorkDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviousWorkResponse
{
    private PreviousWorkDTO previousWork;
}
