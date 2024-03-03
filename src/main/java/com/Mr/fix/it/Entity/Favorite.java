package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "favorite")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorite
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;
}
