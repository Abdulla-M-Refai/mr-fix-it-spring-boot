package com.Mr.fix.it.Service;

import com.Mr.fix.it.DTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.Ads;
import com.Mr.fix.it.Entity.Worker;
import com.Mr.fix.it.Entity.Category;

import com.Mr.fix.it.Repository.AdsRepository;
import com.Mr.fix.it.Repository.WorkerRepository;
import com.Mr.fix.it.Repository.CategoryRepository;

import com.Mr.fix.it.Response.AdsResponse;
import com.Mr.fix.it.Response.WorkersResponse;
import com.Mr.fix.it.Response.WorkersGroupResponse;
import com.Mr.fix.it.Response.GroupedWorkersByCategoryResponse;

@Service
@RequiredArgsConstructor
public class WorkersGetterService
{
    private final WorkerRepository workerRepository;

    private final AdsRepository adsRepository;

    private final CategoryRepository categoryRepository;

    public AdsResponse getWorkersAds()
    {
        List<Ads> ads = adsRepository.findActiveAds(LocalDateTime.now());
        return buildAdsDtoListFromAds(ads);
    }

    public WorkersResponse getFeaturedWorkers()
    {
        List<Worker> workers = workerRepository.findActiveFeatured(LocalDateTime.now());
        return buildWorkerDtoListFromWorkers(workers);
    }

    public WorkersResponse getTopRatedWorkers()
    {
        List<Worker> workers = workerRepository.findTopRated();
        return buildWorkerDtoListFromWorkers(workers);
    }

    public WorkersResponse getNewcomers()
    {
        List<Worker> workers = workerRepository.findNewcomersWorkers();
        return buildWorkerDtoListFromWorkers(workers);
    }

    public GroupedWorkersByCategoryResponse getWorkersGroupedByCategories()
    {
        List<Category> categories = categoryRepository.findAllInRandomOrder();
        List<WorkersGroupResponse> workersGroupResponse = buildWorkersGroupResponse(categories);

        return GroupedWorkersByCategoryResponse.builder()
            .workersGroups(workersGroupResponse)
            .build();
    }

    private AdsResponse buildAdsDtoListFromAds(List<Ads> ads)
    {
        List<AdsDTO> adsDtoList = ads
            .stream()
            .map(ad -> new AdsDTO(ad.getId(), ad.getWorker().getId(), ad.getWorker().getUser().getFirstName(), ad.getWorker().getUser().getLastName(), ad.getPosterImg(), ad.getStartDate(), ad.getExpiryDate()))
            .toList();

        return AdsResponse
            .builder()
            .ads(adsDtoList)
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

    private List<WorkersGroupResponse> buildWorkersGroupResponse(List<Category> categories)
    {
        return categories
            .stream()
            .map(category ->
            {
                List<Worker> workers = workerRepository.findWorkersByCategoryType(category.getType());

                var categoryDTO = CategoryDTO
                    .builder()
                    .type(category.getType())
                    .build();

                var workersResponse = buildWorkerDtoListFromWorkers(workers);

                return WorkersGroupResponse.builder()
                    .category(categoryDTO)
                    .workers(workersResponse.getWorkers())
                    .build();

            }).toList();
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
}