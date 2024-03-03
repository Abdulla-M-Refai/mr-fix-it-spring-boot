package com.Mr.fix.it.Response;

import lombok.*;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage
{
    private String recipientToken;
    
    private String title;

    private String body;

    private Map<String, String> data;
}
