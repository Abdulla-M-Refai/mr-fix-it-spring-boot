package com.Mr.fix.it.Service;

import com.Mr.fix.it.DTO.*;
import com.Mr.fix.it.Response.*;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import org.springframework.validation.BindingResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.time.LocalDateTime;

import java.io.IOException;

import com.Mr.fix.it.Config.Security.JwtService;

import com.Mr.fix.it.Entity.User;
import com.Mr.fix.it.Entity.Reels;
import com.Mr.fix.it.Entity.Worker;
import com.Mr.fix.it.Entity.Comment;

import com.Mr.fix.it.Repository.UserRepository;
import com.Mr.fix.it.Repository.ReelsRepository;
import com.Mr.fix.it.Repository.WorkerRepository;
import com.Mr.fix.it.Repository.CommentRepository;

import com.Mr.fix.it.Request.ReelRequest;
import com.Mr.fix.it.Request.CommentRequest;

import com.Mr.fix.it.Util.Helper;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@Service
@RequiredArgsConstructor
public class WorkerReelService
{
    private final UserRepository userRepository;

    private final WorkerRepository workerRepository;

    private final ReelsRepository reelsRepository;

    private final CommentRepository commentRepository;

    private final JwtService jwtService;

    private final NotificationService notificationService;

    private final ImageUploadingService imageUploadingService;

    public ReelResponse addReel(
        ReelRequest request,
        BindingResult result,
        String token
    ) throws
        NotFoundException,
        ValidationException,
        IOException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        String fileName = Helper.generateFileName(request.getVideo());
        File file = imageUploadingService.convertToFile(request.getVideo(), fileName);
        String uri = imageUploadingService.uploadFile(file, fileName);

        var reel = Reels
            .builder()
            .worker(worker)
            .video(uri)
            .postDate(LocalDateTime.now())
            .build();

        reelsRepository.save(reel);
        reelsRepository.flush();

        var reelDTO = ReelDTO
            .builder()
            .id(reel.getId())
            .worker(getWorkerDto(reel.getWorker()))
            .video(reel.getVideo())
            .postDate(reel.getPostDate())
            .build();

        return ReelResponse
            .builder()
            .reel(reelDTO)
            .build();
    }

    @Transactional
    public GenericResponse deleteReel(
        long id,
        String token
    ) throws
        NotFoundException,
        NotAuthorizedException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var reel = reelsRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("reel not found"));

        if (worker
            .getReels()
            .stream()
            .filter(r -> Objects.equals(r.getId(), reel.getId()))
            .toList()
            .isEmpty()
        ) throw new NotAuthorizedException("unauthorized user");

        worker
            .getReels()
            .set(worker.getReels().indexOf(reel), null);

        reelsRepository.delete(reel);

        return GenericResponse
            .builder()
            .state("success")
            .message("reel deleted successfully")
            .build();
    }

    public ReelsResponse getWorkerReels(
        String token
    ) throws
        NotFoundException,
        NotAuthorizedException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        List<Reels> reels = reelsRepository.findAllByWorkerId(worker.getId());

        List<ReelDTO> reelDTOS = reels
            .stream()
            .map(rl ->
                ReelDTO
                    .builder()
                    .id(rl.getId())
                    .worker(getWorkerDto(rl.getWorker()))
                    .video(rl.getVideo())
                    .postDate(rl.getPostDate())
                    .totalLikes(rl.getLikes().size())
                    .isLiked(rl.getLikes().stream().filter(like -> like.getUser().getId().equals(user.getId())).toList().size() == 1)
                    .comments(buildCommentList(rl))
                    .build()
            )
            .toList();

        return ReelsResponse
            .builder()
            .reels(reelDTOS)
            .build();
    }

    private List<CommentDTO> buildCommentList(Reels reels)
    {
        return reels
            .getComments()
            .stream()
            .map(comment ->
                CommentDTO
                    .builder()
                        .id(comment.getId())
                        .comment(comment.getComment())
                        .user(getUserDto(comment.getUser()))
                        .commentDate(comment.getCommentDate())
                    .build()
            )
            .toList();
    }

    private UserDTO getUserDto(User user)
    {
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
            .favorites(null)
            .build();
    }

    private WorkerDTO getWorkerDto(Worker worker)
    {
        return WorkerDTO
                .builder()
                .id(worker.getId())
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

    public CommentResponse postComment(
        long id,
        CommentRequest request,
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

        var reel = reelsRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("reel not found"));

        var comment = Comment
            .builder()
            .reel(reel)
            .user(user)
            .commentDate(LocalDateTime.now())
            .comment(request.getComment())
            .build();

        commentRepository.save(comment);

        if(reel.getWorker().getUser().getFcm() != null)
            notificationService.sendNotificationByToken(
                NotificationMessage
                    .builder()
                    .title("New reel comment")
                    .body(comment.getComment())
                    .recipientToken(reel.getWorker().getUser().getFcm())
                    .data(new HashMap<>())
                    .build()
            );

        return CommentResponse
            .builder()
            .comment(
                CommentDTO
                    .builder()
                    .id(comment.getId())
                    .comment(comment.getComment())
                    .user(getUserDto(comment.getUser()))
                    .commentDate(comment.getCommentDate())
                    .build()
            )
            .build();
    }

    public ReelsResponse getReels(
        String token
    ) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        List<Reels> reels = reelsRepository.getAllReelsRandomly();

        List<ReelDTO> reelDTOS = reels
            .stream()
            .map(rl ->
                ReelDTO
                    .builder()
                    .id(rl.getId())
                    .worker(getWorkerDto(rl.getWorker()))
                    .video(rl.getVideo())
                    .postDate(rl.getPostDate())
                    .totalLikes(rl.getLikes().size())
                    .isLiked(rl.getLikes().stream().filter(like -> like.getUser().getId().equals(user.getId())).toList().size() == 1)
                    .comments(buildCommentList(rl))
                    .build()
            )
            .toList();

        return ReelsResponse
            .builder()
            .reels(reelDTOS)
            .build();
    }
}
