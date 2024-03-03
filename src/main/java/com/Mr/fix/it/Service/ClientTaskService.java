package com.Mr.fix.it.Service;

import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;
import com.Mr.fix.it.Response.NotificationMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.time.LocalDateTime;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

import com.Mr.fix.it.Config.Security.JwtService;

import com.Mr.fix.it.Repository.*;

import com.Mr.fix.it.Entity.*;
import com.Mr.fix.it.Entity.Enum.TaskStatus;
import com.Mr.fix.it.Entity.Enum.TaskType;

import com.Mr.fix.it.DTO.*;

import com.Mr.fix.it.Request.*;
import com.Mr.fix.it.Response.TasksResponse;
import com.Mr.fix.it.Response.GenericResponse;
import com.Mr.fix.it.Response.TaskCategoryStatisticsResponse;

import com.Mr.fix.it.Util.Helper;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;

@Service
@RequiredArgsConstructor
public class ClientTaskService
{
    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final WorkerRepository workerRepository;

    private final CategoryRepository categoryRepository;

    private final FavoriteRepository favoriteRepository;

    private final JwtService jwtService;

    private final NotificationService notificationService;

    private final CanceledTasksRepository canceledTasksRepository;

    private final ImageUploadingService imageUploadingService;

    public GenericResponse requestTask(
        RequestedTaskRequest request,
        BindingResult result
    ) throws IOException
    {
        Worker worker = workerRepository
            .findById(request.getParsedWorkerID())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var task = createTask(request, result);
        task.setWorker(worker);
        task.setCategory(worker.getCategory());
        task.setPrice(request.getParsedPrice());
        task.setType(TaskType.PRIVATE);
        task.setStatus(TaskStatus.REQUESTED);

        taskRepository.save(task);

        if(worker.getUser().getFcm() != null)
            notificationService.sendNotificationByToken(
                NotificationMessage
                    .builder()
                    .title("New requested task")
                    .body(task.getUser().getFirstName() + " " + task.getUser().getLastName() + " requested new task from you")
                    .recipientToken(worker.getUser().getFcm())
                    .data(new HashMap<>())
                    .build()
            );

        return GenericResponse
            .builder()
            .state("success")
            .message("task requested successfully")
            .build();
    }

    public GenericResponse postTask(
        PostTaskRequest request,
        BindingResult result
    ) throws IOException
    {
        var task = createTask(request, result);

        var category = categoryRepository.findByType(request.getCategory());
        category.ifPresent(task::setCategory);

        task.setPrice(request.getParsedPrice());
        task.setType(TaskType.POST);
        task.setStatus(TaskStatus.POSTED);

        taskRepository.save(task);

        return GenericResponse
            .builder()
            .state("success")
            .message("task requested successfully")
            .build();
    }

    public GenericResponse postTender(
        TenderRequest request,
        BindingResult result
    ) throws IOException
    {
        var task = createTask(request, result);

        var category = categoryRepository.findByType(request.getCategory());
        category.ifPresent(task::setCategory);

        task.setType(TaskType.TENDER);
        task.setStatus(TaskStatus.POSTED);

        taskRepository.save(task);

        return GenericResponse
            .builder()
            .state("success")
            .message("task requested successfully")
            .build();
    }

    private Task createTask(
        TaskRequest request,
        BindingResult result
    ) throws IOException
    {
        Helper.fieldsValidate(result);

        User user = userRepository
                .findById(request.getParsedUserID())
                .orElseThrow(() -> new NotFoundException("user not found"));

        List<String> filesNames = request
            .getTaskImg()
            .stream()
            .map(Helper::generateFileName)
            .toList();

        List<File> files = new ArrayList<>();
        for(int i = 0 ; i < filesNames.size() ; i++)
            files.add(imageUploadingService.convertToFile(request.getTaskImg().get(i), filesNames.get(i)));

        List<String> uris = new ArrayList<>();
        for(int i = 0 ; i < files.size() ; i++)
            uris.add(imageUploadingService.uploadFile(files.get(i), filesNames.get(i)));

        var task = Task
            .builder()
            .user(user)
            .worker(null)
            .category(null)
            .locality(request.getLocality())
            .latitude(request.getParsedLatitude())
            .longitude(request.getParsedLongitude())
            .title(request.getTitle())
            .description(request.getDescription())
            .price(null)
            .startDate(null)
            .expiryDate(null)
            .type(null)
            .status(null)
            .build();

        List<TaskImg> taskImgs = uris
            .stream()
            .map(taskImgUri -> TaskImg.builder().task(task).img(taskImgUri).build())
            .toList();

        task.setTaskImgs(taskImgs);

        return task;
    }

    public TasksResponse getClientTasks(String token) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        List<Task> userTasks = taskRepository.getUserTasks(user.getId());

        List<TaskDTO> userTasksDTOList = userTasks.stream()
            .map(task -> TaskDTO.builder()
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
                .build())
            .toList();

        return TasksResponse
            .builder()
            .tasks(userTasksDTOList)
            .build();
    }

    @Transactional
    public GenericResponse deleteClientTask(
        TaskDeleteRequest taskDeleteRequest,
        BindingResult result,
        String token
    ) throws NotFoundException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var task = taskRepository.findById(taskDeleteRequest.getId());

        if(task.isPresent() && task.get().getUser().getId().longValue() == user.getId().longValue())
            taskRepository.delete(task.get());

        return GenericResponse
            .builder()
            .state("success")
            .message("task deleted successfully")
            .build();
    }

    public TaskCategoryStatisticsResponse getClientTasksCategoriesStatistics(String token) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        List<Task> userTasks = taskRepository.getUserTasks(user.getId());

        HashMap<String, TaskCategoryStatisticsDTO> categoryStatistics = new HashMap<>();

        userTasks.forEach(task -> {
            String categoryType = task.getCategory().getType();

            if(!categoryStatistics.containsKey(categoryType))
            {
                CategoryDTO categoryDTO = CategoryDTO
                    .builder()
                    .type(categoryType)
                    .build();

                int totalCompleted = 0;

                if(task.getStatus() == TaskStatus.COMPLETED)
                    totalCompleted = 1;

                TaskCategoryStatisticsDTO categoryStatisticsDTO = TaskCategoryStatisticsDTO.builder()
                    .category(categoryDTO)
                    .total(1)
                    .totalCompleted(totalCompleted)
                    .build();

                categoryStatistics.put(categoryType, categoryStatisticsDTO);
            }
            else
            {
                TaskCategoryStatisticsDTO categoryStatisticsDTO = categoryStatistics.get(categoryType);
                categoryStatisticsDTO.setTotal(categoryStatisticsDTO.getTotal() + 1);

                if(task.getStatus() == TaskStatus.COMPLETED)
                    categoryStatisticsDTO.setTotalCompleted(categoryStatisticsDTO.getTotalCompleted() + 1);

                categoryStatistics.replace(categoryType, categoryStatisticsDTO);
            }
        });

        return TaskCategoryStatisticsResponse
            .builder()
            .categoryStatistics(new ArrayList<>(categoryStatistics.values()))
            .build();
    }

    @Transactional
    public GenericResponse assignWorker(
        AssignWorkerRequest assignWorkerRequest,
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

        var worker = workerRepository.findById(assignWorkerRequest.getParsedWorkerID())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var task = taskRepository.findByIdAndUserId(assignWorkerRequest.getParsedTaskID(), user.getId())
            .orElseThrow(() -> new NotFoundException("task not found"));

        task.setWorker(worker);
        task.setStatus(TaskStatus.ASSIGNED);
        task.setStartDate(LocalDateTime.now());
        task.setPrice(assignWorkerRequest.getParsedPrice());
        taskRepository.save(task);

        if(worker.getUser().getFcm() != null)
            notificationService.sendNotificationByToken(
                NotificationMessage
                    .builder()
                    .title("Your offer accepted")
                    .body(task.getUser().getFirstName() + " " + task.getUser().getLastName() + " accepted your offer")
                    .recipientToken(worker.getUser().getFcm())
                    .data(new HashMap<>())
                    .build()
            );

        return GenericResponse
            .builder()
            .state("success")
            .message("worker assigned successfully")
            .build();
    }

    @Transactional
    public GenericResponse taskRateSubmission(
        TaskRateSubmissionRequest taskRateSubmissionRequest,
        BindingResult result,
        String token
    ) throws NotFoundException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var task = taskRepository.findByIdAndUserId(taskRateSubmissionRequest.getParsedTaskID(), user.getId())
            .orElseThrow(() -> new NotFoundException("task not found"));

        var worker = workerRepository.findById(task.getWorker().getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var feedback = Feedback
            .builder()
            .perfectionRate(taskRateSubmissionRequest.getParsedPerfection())
            .treatmentRate(taskRateSubmissionRequest.getParsedTreatment())
            .additionalInfo(taskRateSubmissionRequest.getAdditionalInfo())
            .build();

        task.setFeedback(feedback);
        taskRepository.save(task);

        List<Task> tasks = taskRepository.findAllByWorkerId(worker.getId());

        int rateCount = 0;
        float rateSum = 0.0f;

        for(Task workerTask : tasks)
        {
            if(workerTask.getFeedback() != null)
            {
                float perfectionRate = workerTask.getFeedback().getPerfectionRate();
                float treatmentRate = workerTask.getFeedback().getTreatmentRate();

                rateCount++;
                rateSum += (perfectionRate + treatmentRate) / 2;
            }
        }

        worker.setRate(rateSum / rateCount);
        workerRepository.save(worker);

        return GenericResponse
            .builder()
            .state("success")
            .message("feedback submitted successfully")
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

        if(!task.getUser().getId().equals(user.getId()))
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