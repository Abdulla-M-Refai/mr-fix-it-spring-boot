package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ads
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(name = "poster_img", nullable = false)
    private String posterImg;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}