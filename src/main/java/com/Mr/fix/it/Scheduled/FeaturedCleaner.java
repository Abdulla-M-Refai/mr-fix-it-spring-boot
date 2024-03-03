package com.Mr.fix.it.Scheduled;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.Featured;
import com.Mr.fix.it.Repository.FeaturedRepository;

@Component
@RequiredArgsConstructor
public class FeaturedCleaner
{
    private final FeaturedRepository featuredRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanFeatured()
    {
        List<Featured> featuredList = featuredRepository.findByExpiryDateBefore(LocalDateTime.now());
        featuredRepository.deleteAll(featuredList);
    }
}
