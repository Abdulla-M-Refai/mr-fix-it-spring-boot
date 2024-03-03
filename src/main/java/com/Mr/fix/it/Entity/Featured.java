package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "featured")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Featured
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}