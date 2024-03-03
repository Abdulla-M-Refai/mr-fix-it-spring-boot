package com.Mr.fix.it.Service;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

import com.Mr.fix.it.Entity.PreviousWork;
import com.Mr.fix.it.Entity.PreviousWorkImg;

import com.Mr.fix.it.DTO.ImageDTO;
import com.Mr.fix.it.DTO.PreviousWorkDTO;

import com.Mr.fix.it.Config.Security.JwtService;

import com.Mr.fix.it.Repository.UserRepository;
import com.Mr.fix.it.Repository.WorkerRepository;
import com.Mr.fix.it.Repository.PreviousWorkRepository;

import com.Mr.fix.it.Request.PreviousWorkRequest;

import com.Mr.fix.it.Response.GenericResponse;
import com.Mr.fix.it.Response.PreviousWorkResponse;

import com.Mr.fix.it.Util.Helper;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@Service
@RequiredArgsConstructor
public class PreviousWorkService
{
    private final PreviousWorkRepository previousWorkRepository;

    private final UserRepository userRepository;

    private final WorkerRepository workerRepository;

    private final JwtService jwtService;

    private final ImageUploadingService imageUploadingService;

    public PreviousWorkResponse addPreviousWork(
        PreviousWorkRequest request,
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

        List<String> filesNames = request
            .getWorkImgs()
            .stream()
            .map(Helper::generateFileName)
            .toList();

        List<File> files = new ArrayList<>();
        for(int i = 0 ; i < filesNames.size() ; i++)
            files.add(imageUploadingService.convertToFile(request.getWorkImgs().get(i), filesNames.get(i)));

        List<String> uris = new ArrayList<>();
        for(int i = 0 ; i < files.size() ; i++)
            uris.add(imageUploadingService.uploadFile(files.get(i), filesNames.get(i)));

        var previousWork = PreviousWork
            .builder()
            .worker(worker)
            .description(request.getDescription())
            .build();

        List<PreviousWorkImg> previousWorkImgs = uris
            .stream()
            .map(img ->
                PreviousWorkImg
                    .builder()
                        .img(img)
                        .previousWork(previousWork)
                    .build()
            )
            .toList();

        previousWork.setPreviousWorkImgs(previousWorkImgs);

        previousWorkRepository.save(previousWork);
        previousWorkRepository.flush();

        var previousWorkDTO = PreviousWorkDTO
            .builder()
            .id(previousWork.getId())
            .description(previousWork.getDescription())
            .previousWorkImgs(
                previousWork
                    .getPreviousWorkImgs()
                    .stream()
                    .map(img ->
                        ImageDTO
                            .builder()
                            .img(img.getImg())
                            .build()
                    )
                    .toList()
            )
            .build();

        return PreviousWorkResponse
            .builder()
            .previousWork(previousWorkDTO)
            .build();
    }

    @Transactional
    public GenericResponse deletePreviousWork(
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

        var previousWork = previousWorkRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("previous work not found"));

        if (worker
            .getPreviousWorks()
            .stream()
            .filter(pv -> Objects.equals(pv.getId(), previousWork.getId()))
            .toList()
            .isEmpty()
        ) throw new NotAuthorizedException("unauthorized user");

        worker
            .getPreviousWorks()
            .set(worker.getPreviousWorks().indexOf(previousWork), null);

        previousWorkRepository.delete(previousWork);

        return GenericResponse
            .builder()
            .state("success")
            .message("previous work deleted successfully")
            .build();
    }
}
