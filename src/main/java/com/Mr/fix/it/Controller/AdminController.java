package com.Mr.fix.it.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.io.IOException;

import com.Mr.fix.it.Service.AdminService;

import com.Mr.fix.it.Request.*;
import com.Mr.fix.it.Response.*;

import com.Mr.fix.it.Exception.ExceptionType.UniqueException;
import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@Validated
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController
{
    private final AdminService adminService;

    @PostMapping("/auth")
    public ResponseEntity<AuthenticationResponse> authenticate(
        @Valid
        @RequestBody
        AuthenticationRequest request,
        BindingResult result
    ) throws
        ValidationException,
        NotFoundException
    {
        return ResponseEntity.ok(
            adminService.authenticate(request,result)
        );
    }

    @GetMapping("/get-statistics")
    public ResponseEntity<StatisticsResponse> getStatistics()
    {
        return ResponseEntity.ok(
            adminService.getStatistics()
        );
    }

    @GetMapping("/get-clients")
    public ResponseEntity<ClientsResponse> getClients()
    {
        return ResponseEntity.ok(
            adminService.getClients()
        );
    }

    @PostMapping("/delete-client/{id}")
    public ResponseEntity<GenericResponse> deleteClient(
        @PathVariable
        String id
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            adminService.deleteClient(
                Long.parseLong(id)
            )
        );
    }

    @PostMapping("/update-user/{id}")
    public ResponseEntity<ClientResponse> updateUser(
        @PathVariable
        String id,
        @Valid
        @RequestBody
        UserUpdateRequest userUpdateRequest,
        BindingResult result
    ) throws
        ValidationException,
        UniqueException
    {
        return ResponseEntity.ok(
            adminService.updateUser(
                Long.parseLong(id),
                userUpdateRequest,
                result
            )
        );
    }

    @PostMapping("/register-user")
    public ResponseEntity<ClientResponse> createClient(
        @Valid
        @RequestBody
        AdminCreateClientRequest request,
        BindingResult result
    ) throws ValidationException
    {
        return ResponseEntity.ok(
            adminService.createClient(request,result)
        );
    }

    @GetMapping("/get-workers")
    public ResponseEntity<WorkersResponse> getWorkers()
    {
        return ResponseEntity.ok(
            adminService.getWorkers()
        );
    }

    @GetMapping("/get-not-featured-workers")
    public ResponseEntity<WorkersResponse> getNotFWorkers()
    {
        return ResponseEntity.ok(
            adminService.getNotFWorkers()
        );
    }

    @GetMapping("/get-new-comers")
    public ResponseEntity<ClientsResponse> getNewComers()
    {
        return ResponseEntity.ok(
            adminService.getNewComers()
        );
    }

    @PostMapping("/delete-working-location/{id}")
    public ResponseEntity<GenericResponse> deleteWorkingLocation(
        @PathVariable
        String id
    ) throws
        NotAuthorizedException,
        NotFoundException
    {
        return ResponseEntity.ok(
            adminService.deleteWorkingLocation(
                Long.parseLong(id)
            )
        );
    }

    @PostMapping("/add-working-location/{id}")
    public ResponseEntity<WorkingLocationResponse> addWorkingLocation(
        @PathVariable
        String id,
        @Valid
        @RequestBody
        WorkingLocationRequest workingLocationRequest,
        BindingResult bindingResult
    ) throws
        ValidationException,
        NotFoundException
    {
        return ResponseEntity.ok(
            adminService.addWorkingLocation(
                Long.parseLong(id),
                workingLocationRequest,
                bindingResult
            )
        );
    }

    @GetMapping("/get-tasks")
    public ResponseEntity<TasksResponse> getTasks()
    {
        return ResponseEntity.ok(adminService.getTasks());
    }

    @PostMapping("/update-task-status/{id}")
    public ResponseEntity<TaskResponse> updateTaskStatus(
        @PathVariable
        String id,
        @RequestBody
        UpdateStatusRequest request,
        BindingResult result
    )
    {
        return ResponseEntity.ok(
            adminService.updateTaskStatus(
                Long.parseLong(id),
                request,
                result
            )
        );
    }

    @PostMapping("/delete-task/{id}")
    public ResponseEntity<GenericResponse> deleteTask(
        @PathVariable
        String id
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            adminService.deleteTask(
                Long.parseLong(id)
            )
        );
    }

    @GetMapping("/get-categories")
    public ResponseEntity<CategoriesResponse> getCategories()
    {
        return ResponseEntity.ok(adminService.getCategories());
    }

    @PostMapping("/create-category")
    public ResponseEntity<CategoryResponse> createCategory(
        @RequestBody
        CreateCategoryRequest request,
        BindingResult result
    )
    {
        return ResponseEntity.ok(
            adminService.createCategory(
                request,
                result
            )
        );
    }

    @PostMapping("/delete-category/{id}")
    public ResponseEntity<GenericResponse> deleteCategory(
        @PathVariable
        String id
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            adminService.deleteCategory(
                Long.parseLong(id)
            )
        );
    }

    @GetMapping("/get-ads")
    public ResponseEntity<AdsResponse> getAds()
    {
        return ResponseEntity.ok(adminService.getAds());
    }

    @PostMapping("/create-ad/{id}")
    public ResponseEntity<AdResponse> createAd(
        @PathVariable
        String id,
        @Valid
        @ModelAttribute
        ImageRequest imageRequest,
        BindingResult result
    ) throws
        NotFoundException,
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(
            adminService.createAd(
                Long.parseLong(id),
                imageRequest,
                result
            )
        );
    }

    @PostMapping("/delete-ad/{id}")
    public ResponseEntity<GenericResponse> deleteAd(
        @PathVariable
        String id
    ) throws NotFoundException
    {
        return ResponseEntity.ok(adminService.deleteAd(Long.parseLong(id)));
    }

    @GetMapping("/get-featured-workers")
    public ResponseEntity<FeaturedsResponse> getFeaturedWorkers()
    {
        return ResponseEntity.ok(adminService.getFeaturedWorkers());
    }

    @PostMapping("/create-featured/{id}")
    public ResponseEntity<FeaturedWorkerResponse> createFeatured(
        @PathVariable
        String id
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            adminService.createFeatured(
                Long.parseLong(id)
            )
        );
    }

    @PostMapping("/delete-featured/{id}")
    public ResponseEntity<GenericResponse> deleteFeatured(
        @PathVariable
        String id
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            adminService.deleteFeatured(
                Long.parseLong(id)
            )
        );
    }

    @GetMapping("/get-donations")
    public ResponseEntity<DonationsResponse> getDonations()
    {
        return ResponseEntity.ok(adminService.getDonations());
    }
}
