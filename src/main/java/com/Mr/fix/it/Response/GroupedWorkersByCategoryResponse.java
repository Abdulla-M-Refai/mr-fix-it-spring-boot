package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupedWorkersByCategoryResponse
{
    private List<WorkersGroupResponse> workersGroups;
}
