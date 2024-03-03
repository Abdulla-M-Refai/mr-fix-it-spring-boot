package com.Mr.fix.it.Service;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.validation.BindingResult;

import java.io.File;
import java.util.Objects;
import java.time.LocalDateTime;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

import com.Mr.fix.it.Entity.Ads;
import com.Mr.fix.it.DTO.AdsDTO;

import com.Mr.fix.it.Config.Security.JwtService;

import com.Mr.fix.it.Repository.AdsRepository;
import com.Mr.fix.it.Repository.UserRepository;
import com.Mr.fix.it.Repository.WorkerRepository;

import com.Mr.fix.it.Request.ImageRequest;

import com.Mr.fix.it.Response.AdResponse;
import com.Mr.fix.it.Response.GenericResponse;

import com.Mr.fix.it.Util.Helper;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@Service
@RequiredArgsConstructor
public class AdsService
{
    private final AdsRepository adsRepository;

    private final UserRepository userRepository;

    private final WorkerRepository workerRepository;

    private final JwtService jwtService;

    private final ImageUploadingService imageUploadingService;

    public AdResponse shareAd(
        ImageRequest request,
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

        String fileName = Helper.generateFileName(request.getImg());
        File file = imageUploadingService.convertToFile(request.getImg(), fileName);
        String uri = imageUploadingService.uploadFile(file, fileName);

        var ad = Ads
            .builder()
            .posterImg(uri)
            .worker(worker)
            .startDate(LocalDateTime.now())
            .expiryDate(LocalDateTime.now().plusDays(30))
            .build();

        adsRepository.save(ad);
        adsRepository.flush();

        var adDTO = AdsDTO
            .builder()
            .id(ad.getId())
            .poster(ad.getPosterImg())
            .startDate(ad.getStartDate())
            .expiryDate(ad.getExpiryDate())
            .build();

        return AdResponse
            .builder()
            .ad(adDTO)
            .build();
    }

    @Transactional
    public GenericResponse deleteAd(
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

        var ad = adsRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("ad not found"));

        if (worker
            .getAds()
            .stream()
            .filter(workerAd -> Objects.equals(workerAd.getId(), ad.getId()))
            .toList()
            .isEmpty()
        ) throw new NotAuthorizedException("unauthorized user");

        worker
            .getAds()
            .set(worker.getAds().indexOf(ad), null);

        adsRepository.delete(ad);

        return GenericResponse
            .builder()
            .state("success")
            .message("ad deleted successfully")
            .build();
    }
}
