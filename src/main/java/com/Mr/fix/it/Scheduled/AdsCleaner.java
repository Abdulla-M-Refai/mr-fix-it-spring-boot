package com.Mr.fix.it.Scheduled;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.Ads;
import com.Mr.fix.it.Repository.AdsRepository;

@Component
@RequiredArgsConstructor
public class AdsCleaner
{
    private final AdsRepository adsRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanAds()
    {
        List<Ads> adsList = adsRepository.findByExpiryDateBefore(LocalDateTime.now());
        adsRepository.deleteAll(adsList);
    }
}