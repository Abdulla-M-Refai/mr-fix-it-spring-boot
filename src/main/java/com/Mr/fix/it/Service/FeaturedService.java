package com.Mr.fix.it.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.LocalDateTime;

import com.Mr.fix.it.Config.Security.JwtService;

import com.Mr.fix.it.Entity.Featured;
import com.Mr.fix.it.DTO.FeaturedDTO;

import com.Mr.fix.it.Repository.UserRepository;
import com.Mr.fix.it.Repository.WorkerRepository;
import com.Mr.fix.it.Repository.FeaturedRepository;

import com.Mr.fix.it.Response.FeaturedResponse;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;

@Service
@RequiredArgsConstructor
public class FeaturedService 
{
    private final FeaturedRepository featuredRepository;

    private final UserRepository userRepository;

    private final WorkerRepository workerRepository;

    private final JwtService jwtService;

    public FeaturedResponse getFeatured(
        String token
    ) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var worker = workerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var featured = featuredRepository.findByWorkerID(worker.getId());

        var featuredDTO = featured
            .map(value -> FeaturedDTO
                .builder()
                .id(value.getId())
                .workerID(value.getWorker().getId())
                .startDate(value.getStartDate())
                .expiryDate(value.getExpiryDate())
                .build()
            ).orElse(null);

        return FeaturedResponse
            .builder()
            .featured(featuredDTO)
            .build();
    }

    public void subscribeFeatured(
        long workerID
    ) throws NotFoundException
    {
        var worker = workerRepository.findById(workerID)
            .orElseThrow(() ->  new NotFoundException("worker not found"));

        featuredRepository.findByWorkerID(worker.getId())
            .or(() ->
                Optional.ofNullable(
                    Featured
                        .builder()
                        .worker(worker)
                        .startDate(LocalDateTime.now())
                        .expiryDate(LocalDateTime.now().plusDays(30))
                        .build()
                )
            )
            .ifPresent(featuredRepository::save);
    }
}
