package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.Mr.fix.it.Entity.User;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    @Query(
        value =
            "SELECT u.* FROM users u " +
            "WHERE u.is_active = true AND u.is_verified = true " +
            "AND u.created_at >= NOW() - INTERVAL 3 DAY " +
            "ORDER BY RAND() " +
            "LIMIT 15",
        nativeQuery = true
    )
    List<User> findNewcomersWorkers();
}