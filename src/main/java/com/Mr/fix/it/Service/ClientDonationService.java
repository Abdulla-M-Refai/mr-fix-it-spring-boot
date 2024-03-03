package com.Mr.fix.it.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.User;
import com.Mr.fix.it.Entity.Donation;

import com.Mr.fix.it.Repository.UserRepository;
import com.Mr.fix.it.Repository.DonationRepository;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;

@Service
@RequiredArgsConstructor
public class ClientDonationService
{
    private final UserRepository userRepository;

    private final DonationRepository donationRepository;

    public void saveClientDonation(Long clientID, int amount)
    {
        User user = userRepository
            .findById(clientID)
            .orElseThrow(() -> new NotFoundException("user not found"));

        Donation donation = Donation
            .builder()
            .user(user)
            .amount(amount)
            .donationDate(LocalDateTime.now())
            .build();

        donationRepository.save(donation);
    }
}
