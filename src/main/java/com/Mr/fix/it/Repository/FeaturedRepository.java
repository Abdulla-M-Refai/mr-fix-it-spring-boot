package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.Featured;

public interface FeaturedRepository extends JpaRepository<Featured, Long>
{
    List<Featured> findByExpiryDateBefore(LocalDateTime dateTime);

    @Query(
        value =
            "SELECT f FROM Featured f " +
            "WHERE f.worker.id = :workerID"
    )
    Optional<Featured> findByWorkerID(Long workerID);
}