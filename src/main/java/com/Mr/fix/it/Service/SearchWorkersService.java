package com.Mr.fix.it.Service;

import com.Mr.fix.it.DTO.*;
import com.Mr.fix.it.Entity.Category;
import com.Mr.fix.it.Entity.Featured;
import com.Mr.fix.it.Entity.WorkingLocation;
import com.Mr.fix.it.Request.SearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.Mr.fix.it.Entity.Worker;
import com.Mr.fix.it.Repository.WorkerRepository;

import com.Mr.fix.it.Response.WorkersResponse;

@Service
@RequiredArgsConstructor
public class SearchWorkersService
{
    private final WorkerRepository workerRepository;

    public WorkersResponse searchWorkers(String key, String category, String workingLocation)
    {
        List<Worker> workers;

        if(
                (key.isEmpty() || key.isBlank()) &&
                        (
                                (!category.isEmpty() && !category.isBlank()) ||
                                        (!workingLocation.isEmpty() && !workingLocation.isBlank())
                        )
        )
            workers = workerRepository.findActiveWorkers();
        else
            workers = workerRepository.searchWorkers(key);

        if(!category.isEmpty() && !category.isBlank())
            workers = workers.stream()
                    .filter(w -> w
                            .getCategory()
                            .getType()
                            .equals(category)
                    ).toList();

        if(!workingLocation.isEmpty() && !workingLocation.isBlank())
            workers = workers.stream()
                    .filter(worker -> !worker
                            .getWorkingLocations()
                            .stream()
                            .filter(workingLoc -> workingLoc.getLocality().equals(workingLocation))
                            .toList()
                            .isEmpty()
                    ).toList();

        return buildWorkerDtoListFromWorkers(workers);
    }

    private WorkerDTO getWorkerDto(Worker worker)
    {
        return WorkerDTO
                .builder()
                .id(worker.getUser().getId())
                .img(worker.getUser().getImg())
                .type(worker.getUser().getType())
                .phone(worker.getUser().getPhone())
                .email(worker.getUser().getEmail())
                .dob(worker.getUser().getDob())
                .city(worker.getUser().getCity())
                .gender(worker.getUser().getGender().name())
                .lastName(worker.getUser().getLastName())
                .firstName(worker.getUser().getFirstName())
                .createdAt(worker.getUser().getCreatedAt())
                .workerID(worker.getId())
                .enabled(worker.getUser().getIsActive())
                .rate(worker.getRate())
                .favorites(new ArrayList<>())
                .category(CategoryDTO.builder().id(worker.getCategory().getId()).type(worker.getCategory().getType()).totalWorkers(workerRepository.findWorkersByCategoryType(worker.getCategory().getType()).size()).build())
                .ads(worker.getAds().stream().map(ad -> new AdsDTO(ad.getId(), ad.getWorker().getId(), ad.getWorker().getUser().getFirstName(), ad.getWorker().getUser().getLastName(), ad.getPosterImg(), ad.getStartDate(), ad.getExpiryDate())).toList())
                .previousWorks(worker.getPreviousWorks().stream().map(previousWork -> new PreviousWorkDTO(previousWork.getId(), previousWork.getDescription(), previousWork.getPreviousWorkImgs().stream().map(img -> ImageDTO.builder().img(img.getImg()).build()).toList())).toList())
                .workingLocations(worker.getWorkingLocations().stream().map(workingLocation -> new WorkingLocationDTO(workingLocation.getId(), workingLocation.getLocality(), workingLocation.getLatitude(), workingLocation.getLongitude())).toList())
                .build();
    }

    private WorkersResponse buildWorkerDtoListFromWorkers(List<Worker> workers)
    {
        List<WorkerDTO> workerDTOS = workers
                .stream()
                .map(this::getWorkerDto)
                .toList();

        return WorkersResponse
                .builder()
                .workers(workerDTOS)
                .build();
    }

    public WorkersResponse search(SearchRequest searchRequest)
    {
        String key = "%" + searchRequest.getKey() + "%";
        List<Worker> workers = workerRepository.searchWorkers(key);

        if(!searchRequest.getCategories().isEmpty())
        {
            workers = workers.stream()
                    .filter(worker -> searchRequest.getCategories().contains(worker.getCategory().getType()))
                    .toList();
        }

        if(!searchRequest.getCities().isEmpty())
        {
            workers = workers.stream()
                    .filter(worker -> worker.getWorkingLocations().stream()
                            .anyMatch(location -> searchRequest.getCities().contains(location.getLocality())))
                    .collect(Collectors.toList());
        }

        return buildWorkerDtoListFromWorkers(workers);
    }
}
