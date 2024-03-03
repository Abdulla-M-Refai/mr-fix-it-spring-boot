package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.RegisterToken;

public interface RegisterTokenRepository  extends JpaRepository<RegisterToken, Long>
{
    Optional<RegisterToken> findByToken(String token);

    List<RegisterToken> findByExpiryDateBefore(LocalDateTime dateTimeParam);
}