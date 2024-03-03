package com.Mr.fix.it.Request;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest
{
    private String key;

    private List<String> categories;

    private List<String> cities;
}
