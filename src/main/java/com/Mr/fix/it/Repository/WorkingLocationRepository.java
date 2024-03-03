package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.Mr.fix.it.Entity.WorkingLocation;

public interface WorkingLocationRepository extends JpaRepository<WorkingLocation, Long>
{
    void deleteAllByWorkerId(long id);

    List<WorkingLocation> findByWorkerIdIn(List<Long> ids);

    @Query("SELECT wl FROM WorkingLocation wl GROUP BY wl.locality")
    List<WorkingLocation> findAllDistinct();
}