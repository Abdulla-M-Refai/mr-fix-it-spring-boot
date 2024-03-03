package com.Mr.fix.it.Controller;

import com.Mr.fix.it.Request.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.io.IOException;

import com.Mr.fix.it.Service.ClientTaskService;

import com.Mr.fix.it.Response.TasksResponse;
import com.Mr.fix.it.Response.GenericResponse;
import com.Mr.fix.it.Response.TaskCategoryStatisticsResponse;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Validated
public class ClientTaskController
{
    private final ClientTaskService clientTaskService;

    @PostMapping("/request-task")
    public ResponseEntity<GenericResponse> requestTask(
        @Valid
        @ModelAttribute
        RequestedTaskRequest request,
        BindingResult result
    ) throws
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(clientTaskService.requestTask(request, result));
    }

    @PostMapping("/post-task")
    public ResponseEntity<GenericResponse> postTask(
        @Valid
        @ModelAttribute
        PostTaskRequest request,
        BindingResult result
    ) throws
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(clientTaskService.postTask(request, result));
    }

    @PostMapping("/post-tender")
    public ResponseEntity<GenericResponse> postTender(
        @Valid
        @ModelAttribute
        TenderRequest request,
        BindingResult result
    ) throws
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(clientTaskService.postTender(request, result));
    }

    @GetMapping("/get-client-tasks")
    public ResponseEntity<TasksResponse> getClientTasks(
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(clientTaskService.getClientTasks(token.substring(7)));
    }

    @PostMapping("/delete-client-task")
    public ResponseEntity<GenericResponse> deleteClientTask(
        @Valid
        @RequestBody
        TaskDeleteRequest taskDeleteRequest,
        BindingResult result,
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            clientTaskService.deleteClientTask(
                taskDeleteRequest,
                result,
                token.substring(7)
            )
        );
    }

    @GetMapping("/get-client-tasks-category-statistics")
    public ResponseEntity<TaskCategoryStatisticsResponse> getClientTasksCategoriesStatistics(
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(clientTaskService.getClientTasksCategoriesStatistics(token.substring(7)));
    }

    @PostMapping("/assign-worker")
    public ResponseEntity<GenericResponse> assignWorker(
        @Valid
        @RequestBody
        AssignWorkerRequest assignWorkerRequest,
        BindingResult result,
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            clientTaskService.assignWorker(
                assignWorkerRequest,
                result,
                token.substring(7)
            )
        );
    }

    @PostMapping("/task-rate-submission")
    public ResponseEntity<GenericResponse> taskRateSubmission(
        @Valid
        @RequestBody
        TaskRateSubmissionRequest taskRateSubmissionRequest,
        BindingResult result,
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            clientTaskService.taskRateSubmission(
                taskRateSubmissionRequest,
                result,
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
            clientTaskService.cancelTask(
                Long.parseLong(id),
                request,
                result,
                token.substring(7)
            )
        );
    }
}