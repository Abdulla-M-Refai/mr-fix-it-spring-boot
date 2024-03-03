package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.Ads;

public interface AdsRepository extends JpaRepository<Ads, Long>
{
    @Query(
        value =
            "SELECT ad.* FROM ads ad " +
            "INNER JOIN worker w ON w.id = ad.worker_id " +
            "INNER JOIN users u ON w.user_id = u.id " +
            "WHERE u.is_active = true AND u.is_verified = true " +
            "AND ad.expiry_date > :time " +
            "ORDER BY RAND()",
        nativeQuery = true
    )
    List<Ads> findActiveAds(LocalDateTime time);

    List<Ads> findByExpiryDateBefore(LocalDateTime dateTime);
}