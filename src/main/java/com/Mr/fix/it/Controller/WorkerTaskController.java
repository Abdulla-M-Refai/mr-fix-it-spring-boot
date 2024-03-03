package com.Mr.fix.it.Controller;

import com.Mr.fix.it.Request.CancelTaskRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.Mr.fix.it.Service.WorkerTaskService;

import com.Mr.fix.it.Request.OfferTaskRequest;
import com.Mr.fix.it.Request.RequestedTaskStatusRequest;

import com.Mr.fix.it.Response.TaskResponse;
import com.Mr.fix.it.Response.TasksResponse;
import com.Mr.fix.it.Response.GenericResponse;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@Validated
@RestController
@RequestMapping("/api/worker")
@RequiredArgsConstructor
public class WorkerTaskController
{
    private final WorkerTaskService workerTaskService;

    @GetMapping("/get-posted-tasks")
    public ResponseEntity<TasksResponse> getPostedTasks(
        @RequestHeader("Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            workerTaskService.getPostedTasks(token.substring(7))
        );
    }

    @GetMapping("/get-worker-tasks")
    public ResponseEntity<TasksResponse> getWorkerTasks(
        @RequestHeader("Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            workerTaskService.getWorkerTasks(token.substring(7))
        );
    }

    @PostMapping("/offer-task")
    public ResponseEntity<GenericResponse> offerTask(
        @Valid
        @RequestBody
        OfferTaskRequest request,
        BindingResult result,
        @RequestHeader("Authorization")
        String token
    ) throws
        NotFoundException,
        ValidationException
    {
        return ResponseEntity.ok(
            workerTaskService.offerTask(
                request,
                result,
                token.substring(7)
            )
        );
    }

    @PostMapping("/set-requested-task-status")
    public ResponseEntity<TaskResponse> setRequestedTaskStatus(
        @Valid
        @RequestBody
        RequestedTaskStatusRequest request,
        BindingResult result,
        @RequestHeader("Authorization")
        String token
    ) throws
        NotFoundException,
        ValidationException,
        NotAuthorizedException
    {
        return ResponseEntity.ok(
          workerTaskService.setRequestedTaskStatus(
              request,
              result,
              token.substring(7)
          )
        );
    }

    @PostMapping("/set-task-completed/{id}")
    public ResponseEntity<TaskResponse> setTaskCompleted(
        @PathVariable
        String id,
        @RequestHeader("Authorization")
        String token
    ) throws
        NotFoundException,
        NotAuthorizedException
    {
        return ResponseEntity.ok(
            workerTaskService.setTaskCompleted(
                Long.parseLong(id),
                token.substring(7)
            )
        );
    }

    @PostMapping("cancel-task/{id}")
    public ResponseEntity<GenericResponse> cancelTask(
        @PathVariable
        String id,
        @Valid
        @RequestBody
        CancelTaskRequest request,
        BindingResult result,
        @RequestHeader("Authorization")
        String token
    ) throws
        ValidationException,
        NotFoundException,
        NotAuthorizedException
    {
        return ResponseEntity.ok(
            workerTaskService.cancelTask(
                Long.parseLong(id),
                request,
                result,
                token.substring(7)
            )
        );
    }
}
