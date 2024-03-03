package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.Mr.fix.it.Entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>
{
    @Query(
        value =
            "SELECT f.* FROM favorite f " +
            "WHERE f.user_id = :userID " ,
        nativeQuery = true
    )
    List<Favorite> findAllByUserId(long userID);

    @Query(
        value =
            "SELECT f.* FROM favorite f " +
            "WHERE f.user_id = :userID " +
            "AND f.worker_id = :workerID" ,
        nativeQuery = true
    )
    Optional<Favorite> findByUserAndWorkerID(Long userID, Long workerID);
}