package com.Mr.fix.it.Service;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import org.springframework.validation.BindingResult;

import com.Mr.fix.it.Config.Security.JwtService;

import com.Mr.fix.it.Repository.UserRepository;
import com.Mr.fix.it.Repository.WorkerRepository;
import com.Mr.fix.it.Repository.FavoriteRepository;

import com.Mr.fix.it.Entity.Favorite;

import com.Mr.fix.it.Request.UpdateFavoriteRequest;
import com.Mr.fix.it.Response.GenericResponse;

import com.Mr.fix.it.Util.Helper;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;

@Service
@RequiredArgsConstructor
public class FavoriteService
{
    private final UserRepository userRepository;

    private final WorkerRepository workerRepository;

    private final FavoriteRepository favoriteRepository;

    private final JwtService jwtService;

    @Transactional
    public GenericResponse updateFavorite(
        UpdateFavoriteRequest updateFavoriteRequest,
        BindingResult result,
        String token
    ) throws NotFoundException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findById(updateFavoriteRequest.getParsedWorkerID())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        boolean favoriteState = updateFavoriteRequest.getParsedFavoriteState();

        if(favoriteState)
        {
            var favorite = Favorite
                .builder()
                .user(user)
                .worker(worker)
                .build();

            favoriteRepository.save(favorite);
        }
        else
        {
            var favorite = favoriteRepository.findByUserAndWorkerID(user.getId(), worker.getId())
                .orElseThrow(() -> new NotFoundException("this favorite not found"));

            favoriteRepository.delete(favorite);
        }

        return GenericResponse
            .builder()
            .state("success")
            .message("favorite updated successfully")
            .build();
    }
}
