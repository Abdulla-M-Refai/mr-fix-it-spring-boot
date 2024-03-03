package com.Mr.fix.it.Controller;

import com.Mr.fix.it.Request.SearchRequest;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import com.Mr.fix.it.Service.SearchWorkersService;
import com.Mr.fix.it.Service.WorkingLocationService;

import com.Mr.fix.it.Response.WorkersResponse;
import com.Mr.fix.it.Response.WorkingLocationsResponse;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class SearchWorkersController
{
    private final SearchWorkersService searchWorkersService;

    private final WorkingLocationService workingLocationService;

    @GetMapping("/search-workers")
    public ResponseEntity<WorkersResponse> searchWorkers(
            @NotEmpty(message = "missing key")
            @RequestParam String key,
            @RequestParam String category,
            @RequestParam String workingLocation
    )
    {
        return ResponseEntity.ok(searchWorkersService.searchWorkers(key, category, workingLocation));
    }

    @PostMapping("/search")
    public ResponseEntity<WorkersResponse> search(
            @RequestBody
            SearchRequest searchRequest
    )
    {
        return ResponseEntity.ok(searchWorkersService.search(searchRequest));
    }

    @GetMapping("/get-working-locations")
    public ResponseEntity<WorkingLocationsResponse> getWorkingLocations()
    {
        return ResponseEntity.ok(workingLocationService.getWorkingLocations());
    }
}
