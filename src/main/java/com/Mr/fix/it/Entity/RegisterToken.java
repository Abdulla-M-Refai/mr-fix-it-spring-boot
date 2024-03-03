package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "register_token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterToken
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}