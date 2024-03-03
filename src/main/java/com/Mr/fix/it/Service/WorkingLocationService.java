package com.Mr.fix.it.Service;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.Mr.fix.it.Config.Security.JwtService;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Objects;

import com.Mr.fix.it.DTO.WorkingLocationDTO;
import com.Mr.fix.it.Entity.WorkingLocation;

import com.Mr.fix.it.Repository.UserRepository;
import com.Mr.fix.it.Repository.WorkerRepository;
import com.Mr.fix.it.Repository.WorkingLocationRepository;

import com.Mr.fix.it.Request.WorkingLocationRequest;

import com.Mr.fix.it.Response.GenericResponse;
import com.Mr.fix.it.Response.WorkingLocationsResponse;

import com.Mr.fix.it.Util.Helper;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@Service
@RequiredArgsConstructor
public class WorkingLocationService
{
    private final WorkingLocationRepository workingLocationRepository;

    private final UserRepository userRepository;

    private final WorkerRepository workerRepository;

    private final JwtService jwtService;

    public WorkingLocationsResponse getWorkingLocations()
    {
        List<WorkingLocationDTO> workingLocationDTOS = workingLocationRepository
            .findAllDistinct()
            .stream()
            .map(workingLocation ->
                WorkingLocationDTO
                    .builder()
                    .id(workingLocation.getId())
                    .locality(workingLocation.getLocality())
                    .latitude(workingLocation.getLatitude())
                    .longitude(workingLocation.getLongitude())
                    .build()
            )
            .toList();

        return WorkingLocationsResponse
            .builder()
            .workingLocations(workingLocationDTOS)
            .build();
    }

    @Transactional
    public GenericResponse deleteWorkingLocation(
        long id,
        String token
    ) throws
        NotAuthorizedException,
        NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var workingLocation = workingLocationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("working location not found"));

        if (worker
            .getWorkingLocations()
            .stream()
            .filter(wl -> Objects.equals(wl.getId(), workingLocation.getId()))
            .toList()
            .isEmpty()
        ) throw new NotAuthorizedException("unauthorized user");

        worker
            .getWorkingLocations()
            .set(worker.getWorkingLocations().indexOf(workingLocation), null);

        workingLocationRepository.delete(workingLocation);

        return GenericResponse
            .builder()
            .state("success")
            .message("working location deleted successfully")
            .build();
    }

    public GenericResponse addWorkingLocation(
        WorkingLocationRequest request,
        BindingResult result,
        String token
    ) throws
        ValidationException,
        NotFoundException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var workingLocation = WorkingLocation
            .builder()
            .worker(worker)
            .locality(request.getLocality())
            .latitude(request.getParsedLatitude())
            .longitude(request.getParsedLongitude())
            .build();

        workingLocationRepository.save(workingLocation);

        return GenericResponse
            .builder()
            .state("success")
            .message(workingLocation.getId().toString())
            .build();
    }
}
