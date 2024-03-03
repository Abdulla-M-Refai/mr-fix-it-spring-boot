package com.Mr.fix.it.Response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse
{
    private String state;

    private String message;
}