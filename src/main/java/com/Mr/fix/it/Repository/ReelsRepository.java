package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.Mr.fix.it.Entity.Reels;

public interface ReelsRepository extends JpaRepository<Reels, Long>
{
    @Query(
        value =
            "SELECT reel FROM Reels reel " +
            "ORDER BY reel.postDate DESC"
    )
    List<Reels> getAllReelsRandomly();

    @Query(
        value =
            "SELECT reel FROM Reels reel " +
            "WHERE reel.worker.id = :id"
    )
    List<Reels> findAllByWorkerId(Long id);
}
