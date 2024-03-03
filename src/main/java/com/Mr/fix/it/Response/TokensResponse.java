package com.Mr.fix.it.Response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokensResponse
{
    private String token;

    private String refreshToken;
}