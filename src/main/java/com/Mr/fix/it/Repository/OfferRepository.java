package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Mr.fix.it.Entity.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long>
{
    @Query(
        value =
            "SELECT offer FROM Offer offer " +
            "WHERE offer.worker.id = :workerID " +
            "AND offer.task.id = :taskID"
    )
    Optional<Offer> findByWorkerAndTask(long workerID, long taskID);
}