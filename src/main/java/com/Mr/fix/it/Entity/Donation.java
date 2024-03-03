package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "donation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donation
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "donation_date", nullable = false)
    private LocalDateTime donationDate;
}