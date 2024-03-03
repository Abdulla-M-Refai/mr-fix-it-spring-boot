package com.Mr.fix.it.Scheduled;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.time.LocalDateTime;

import com.Mr.fix.it.Repository.UserRepository;
import com.Mr.fix.it.Repository.WorkerRepository;
import com.Mr.fix.it.Repository.RegisterTokenRepository;
import com.Mr.fix.it.Repository.WorkingLocationRepository;

import com.Mr.fix.it.Entity.User;
import com.Mr.fix.it.Entity.Worker;
import com.Mr.fix.it.Entity.RegisterToken;
import com.Mr.fix.it.Entity.WorkingLocation;

@Component
@RequiredArgsConstructor
public class RegisterTokenCleaner
{
    private final UserRepository userRepository;

    private final WorkerRepository workerRepository;

    private final WorkingLocationRepository workingLocationRepository;

    private final RegisterTokenRepository registerTokenRepository;

    @Transactional
    @Scheduled(fixedDelay = 600000)
    public void cleanRegisteredTokens()
    {
        List<RegisterToken> registerTokens = registerTokenRepository.findByExpiryDateBefore(LocalDateTime.now());
        List<Long> userIds = registerTokens.stream().map(token-> token.getUser().getId()).toList();
        List<User> users = userRepository.findAllById(userIds).stream().filter((user -> !user.getIsVerified())).toList();

        userIds = users.stream().map(User::getId).toList();

        List<Worker> workers = workerRepository.findByUserIdIn(userIds);
        List<Long> workerIds = workers.stream().map(Worker::getId).toList();
        List<WorkingLocation> workingLocations = workingLocationRepository.findByWorkerIdIn(workerIds);

        registerTokenRepository.deleteAll(registerTokens);
        workingLocationRepository.deleteAll(workingLocations);
        workerRepository.deleteAll(workers);
        userRepository.deleteAll(users);
    }
}