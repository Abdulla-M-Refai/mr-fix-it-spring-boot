package com.Mr.fix.it.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import com.Mr.fix.it.Service.WorkersGetterService;

import com.Mr.fix.it.Response.AdsResponse;
import com.Mr.fix.it.Response.WorkersResponse;
import com.Mr.fix.it.Response.GroupedWorkersByCategoryResponse;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class WorkersGetterController
{
    private final WorkersGetterService workerService;

    @GetMapping("/get-workers-ads")
    public ResponseEntity<AdsResponse> getWorkersAds()
    {
        return ResponseEntity.ok(workerService.getWorkersAds());
    }

    @GetMapping("/get-featured-workers")
    public ResponseEntity<WorkersResponse> getFeaturedWorkers()
    {
        return ResponseEntity.ok(workerService.getFeaturedWorkers());
    }

    @GetMapping("/get-top-rated-workers")
    public ResponseEntity<WorkersResponse> getTopRatedWorkers()
    {
        return ResponseEntity.ok(workerService.getTopRatedWorkers());
    }

    @GetMapping("/get-newcomers")
    public ResponseEntity<WorkersResponse> getMoreWorkers()
    {
        return ResponseEntity.ok(workerService.getNewcomers());
    }

    @GetMapping("/get-workers-grouped-by-categories")
    public ResponseEntity<GroupedWorkersByCategoryResponse> getWorkersGroupedByCategories()
    {
        return ResponseEntity.ok(workerService.getWorkersGroupedByCategories());
    }
}