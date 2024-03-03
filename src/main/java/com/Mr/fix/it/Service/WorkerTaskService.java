package com.Mr.fix.it.Service;

import com.Mr.fix.it.Request.CancelTaskRequest;
import com.Mr.fix.it.Response.NotificationMessage;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import com.Mr.fix.it.Repository.*;

import com.Mr.fix.it.DTO.*;
import com.Mr.fix.it.Entity.*;
import com.Mr.fix.it.Entity.Enum.TaskStatus;

import com.Mr.fix.it.Request.OfferTaskRequest;
import com.Mr.fix.it.Request.RequestedTaskStatusRequest;

import com.Mr.fix.it.Response.TaskResponse;
import com.Mr.fix.it.Response.TasksResponse;
import com.Mr.fix.it.Response.GenericResponse;

import com.Mr.fix.it.Config.Security.JwtService;

import com.Mr.fix.it.Util.Helper;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@Service
@RequiredArgsConstructor
public class WorkerTaskService
{
    private final UserRepository userRepository;

    private final WorkerRepository workerRepository;

    private final TaskRepository taskRepository;

    private final OfferRepository offerRepository;

    private final FavoriteRepository favoriteRepository;

    private final JwtService jwtService;

    private final NotificationService notificationService;

    private final CanceledTasksRepository canceledTasksRepository;

    public TasksResponse getPostedTasks(
        String token
    ) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        Category category = worker.getCategory();
        List<String> workingLocations = worker
            .getWorkingLocations()
                .stream()
                .map(WorkingLocation::getLocality)
                .toList();

        List<Task> tasks = taskRepository
            .findPostedTasksForWorker(
                category.getType(),
                workingLocations
            );

        List<TaskDTO> userTasksDTOList = buildTaskDtoList(tasks);

        return TasksResponse
            .builder()
            .tasks(userTasksDTOList)
            .build();
    }

    public TasksResponse getWorkerTasks(
        String token
    ) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        List<TaskDTO> taskDTOS = buildTaskDtoList(worker.getTasks());

        return TasksResponse
            .builder()
            .tasks(taskDTOS)
            .build();
    }

    private List<TaskDTO> buildTaskDtoList(List<Task> tasks)
    {
        return tasks.stream()
            .map(this::buildTaskDTO)
            .toList();
    }

    private TaskDTO buildTaskDTO(Task task)
    {
        return TaskDTO.builder()
            .id(task.getId())
            .client(getUserDto(task.getUser()))
            .worker(task.getWorker() != null ? getWorkerDto(task.getWorker()) : null)
            .category(CategoryDTO.builder().type(task.getCategory().getType()).build())
            .locality(task.getLocality())
            .latitude(task.getLatitude())
            .longitude(task.getLongitude())
            .title(task.getTitle())
            .description(task.getDescription())
            .price(task.getPrice() != null ? task.getPrice() : -1)
            .startDate(task.getStartDate())
            .expiryDate(task.getExpiryDate())
            .type(task.getType())
            .status(task.getStatus())
            .taskImgs(task.getTaskImgs().stream()
                .map(taskImg -> ImageDTO.builder().img(taskImg.getImg()).build())
                .toList())
            .offers(task.getOffers().stream()
                .map(offer -> OfferDTO.builder()
                    .id(offer.getId())
                    .taskID(offer.getTask().getId())
                    .worker(offer.getWorker() != null ? getWorkerDto(offer.getWorker()) : null)
                    .price(offer.getPrice())
                    .build())
                .toList())
            .feedback(task.getFeedback())
            .build();
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

    @Transactional
    public GenericResponse offerTask(
        OfferTaskRequest request,
        BindingResult result,
        String token
    ) throws
        NotFoundException,
        ValidationException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var task = taskRepository.findById(request.getParsedTaskID())
            .orElseThrow(() -> new NotFoundException("task not found"));

        var offer = offerRepository.findByWorkerAndTask(worker.getId(), task.getId())
            .or(() ->
                Optional.of(
                    Offer
                        .builder()
                        .task(task)
                        .worker(worker)
                        .price(request.getParsedPrice())
                        .build()
                )
            ).get();

        offer.setPrice(request.getParsedPrice());
        offerRepository.save(offer);

        if(offer.getTask().getUser().getFcm() != null)
            notificationService.sendNotificationByToken(
                NotificationMessage
                    .builder()
                    .title("New offer for task " + task.getTitle())
                    .body(worker.getUser().getFirstName() + " " + worker.getUser().getLastName() + " inserted new offer")
                    .recipientToken(offer.getTask().getUser().getFcm())
                    .data(new HashMap<>())
                    .build()
            );

        return GenericResponse
            .builder()
            .state("success")
            .message("offer submitted successfully")
            .build();
    }

    private UserDTO getUserDto(User user)
    {
        List<Favorite> favorites = favoriteRepository.findAllByUserId(user.getId());
        List<WorkerDTO> favoriteDTOS = favorites
            .stream()
            .map(favorite -> getWorkerDto(favorite.getWorker()))
            .toList();

        return UserDTO
            .builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .dob(user.getDob())
            .gender(user.getGender().name())
            .city(user.getCity())
            .email(user.getEmail())
            .phone(user.getPhone())
            .img(user.getImg())
            .type(user.getType())
            .favorites(favoriteDTOS)
            .build();
    }

    @Transactional
    public TaskResponse setRequestedTaskStatus(
        RequestedTaskStatusRequest request,
        BindingResult result,
        String token
    )throws
        NotFoundException,
        ValidationException,
        NotAuthorizedException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var task = taskRepository.findById(request.getParsedTaskID())
            .orElseThrow(() -> new NotFoundException("task not found"));

        if(!task.getWorker().getId().equals(worker.getId()))
            throw new NotAuthorizedException("unauthorized worker");

        TaskStatus status = request.getParsedState() ? TaskStatus.ASSIGNED : TaskStatus.DECLINED;
        task.setStatus(status);

        if(request.getParsedState())
            task.setStartDate(LocalDateTime.now());

        taskRepository.save(task);

        if(task.getUser().getFcm() != null)
            notificationService.sendNotificationByToken(
                NotificationMessage
                    .builder()
                    .title("Task " + task.getTitle() + " has been " + (status == TaskStatus.ASSIGNED ? "approved" : "declined"))
                    .body(worker.getUser().getFirstName() + " " + worker.getUser().getLastName() + (status == TaskStatus.ASSIGNED ? " approved" : " declined") + " your task")
                    .recipientToken(task.getUser().getFcm())
                    .data(new HashMap<>())
                    .build()
            );

        return TaskResponse
            .builder()
            .task(buildTaskDTO(task))
            .build();
    }

    @Transactional
    public TaskResponse setTaskCompleted(
        long id,
        String token
    )throws
        NotFoundException,
        NotAuthorizedException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var task = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("task not found"));

        if(!task.getWorker().getId().equals(worker.getId()))
            throw new NotAuthorizedException("unauthorized worker");

        task.setStatus(TaskStatus.COMPLETED);
        task.setExpiryDate(LocalDateTime.now());
        taskRepository.save(task);

        if(task.getUser().getFcm() != null)
            notificationService.sendNotificationByToken(
                NotificationMessage
                    .builder()
                    .title("Task " + task.getTitle() + " is completed")
                    .body(worker.getUser().getFirstName() + " " + worker.getUser().getLastName() + " completed your task")
                    .recipientToken(task.getUser().getFcm())
                    .data(new HashMap<>())
                    .build()
            );

        return TaskResponse
            .builder()
            .task(buildTaskDTO(task))
            .build();
    }

    @Transactional
    public GenericResponse cancelTask(
        Long id,
        CancelTaskRequest request,
        BindingResult result,
        String token
    ) throws
        ValidationException,
        NotFoundException,
        NotAuthorizedException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var task = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("task not found"));

        if(task.getWorker() == null || !task.getWorker().getUser().getId().equals(user.getId()))
            throw new NotAuthorizedException("unauthorized user");

        task.setStatus(TaskStatus.CANCELED);
        taskRepository.save(task);

        var canceledTask = CanceledTasks
            .builder()
            .user(user)
            .task(task)
            .reason(request.getReason())
            .build();

        canceledTasksRepository.save(canceledTask);

        return GenericResponse
            .builder()
            .state("success")
            .message("task canceled successfully")
            .build();
    }
}
