package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.Worker;

public interface WorkerRepository extends JpaRepository<Worker, Long>
{
    Optional<Worker> findByUserId(long id);

    List<Worker> findByUserIdIn(List<Long> ids);

    @Query(
        value =
            "SELECT w.* FROM worker w " +
            "INNER JOIN users u ON w.user_id = u.id " +
            "WHERE u.is_active = true AND u.is_verified = true " ,
        nativeQuery = true
    )
    List<Worker> findActiveWorkers();

    @Query(
        value =
            "SELECT w.* FROM worker w " +
            "INNER JOIN users u ON w.user_id = u.id " +
            "INNER JOIN category c ON w.category_id = c.id " +
            "WHERE u.is_active = true AND u.is_verified = true " +
            "AND c.type = :categoryType",
        nativeQuery = true
    )
    List<Worker> findWorkersByCategoryType(String categoryType);

    @Query(
        value =
            "SELECT w.* FROM worker w " +
            "INNER JOIN users u ON w.user_id = u.id " +
            "INNER JOIN featured f ON w.id = f.worker_id " +
            "WHERE u.is_active = true AND u.is_verified = true " +
            "AND f.expiry_date > :time " +
            "ORDER BY RAND()",
        nativeQuery = true
    )
    List<Worker> findActiveFeatured(LocalDateTime time);

    @Query(
        value =
            "SELECT * FROM worker w " +
            "WHERE w.rate IN (" +
            "    SELECT DISTINCT rate FROM (" +
            "        SELECT DISTINCT rate FROM worker " +
            "        ORDER BY rate DESC " +
            "        LIMIT 3" +
            "    ) subquery" +
            ") " +
            "AND w.id NOT IN (SELECT worker_id FROM featured) " +
            "AND w.user_id IN (SELECT u.id FROM users u WHERE u.is_active = true AND u.is_verified = true) " +
            "ORDER BY w.rate DESC",
        nativeQuery = true
    )
    List<Worker> findTopRated();

    @Query(
        value =
            "SELECT w.* FROM worker w " +
            "INNER JOIN users u ON w.user_id = u.id " +
            "WHERE u.is_active = true AND u.is_verified = true " +
            "AND u.is_active = true AND u.is_verified = true " +
            "AND u.created_at >= NOW() - INTERVAL 3 DAY " +
            "ORDER BY RAND() " +
            "LIMIT 15",
        nativeQuery = true
    )
    List<Worker> findNewcomersWorkers();

    @Query(
        value =
            "SELECT w.* FROM worker w " +
            "INNER JOIN users u ON w.user_id = u.id " +
            "WHERE (u.first_name LIKE :key " +
            "OR u.last_name LIKE :key " +
            "OR u.email LIKE :key " +
            "OR u.phone LIKE :key) " +
            "AND u.is_active = true AND u.is_verified = true " ,
        nativeQuery = true
    )
    List<Worker> searchWorkers(String key);
}