package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.Mr.fix.it.Entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long>
{
    @Query(
        value =
            "SELECT t.* FROM task t " +
            "WHERE t.user_id = :userID",
        nativeQuery = true
    )
    List<Task> getUserTasks(long userID);

    @Query(
        value =
            "SELECT t.* FROM task t " +
            "WHERE t.id = :taskID " +
            "AND t.user_id = :userID",
        nativeQuery = true
    )
    Optional<Task> findByIdAndUserId(long taskID, long userID);

    @Query(
        value =
            "SELECT t.* FROM task t " +
            "WHERE t.worker_id = :workerID",
        nativeQuery = true
    )
    List<Task> findAllByWorkerId(long workerID);

    @Query(
        value =
            "SELECT t FROM Task t " +
            "WHERE t.category.type = :categoryType " +
            "AND t.locality IN :workingLocations " +
            "AND (t.type = 'POST' OR  t.type = 'TENDER') " +
            "AND t.status = 'POSTED' "
    )
    List<Task> findPostedTasksForWorker(
        String categoryType,
        List<String> workingLocations
    );
}