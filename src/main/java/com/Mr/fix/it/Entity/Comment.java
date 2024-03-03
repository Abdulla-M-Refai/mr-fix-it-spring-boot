package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reel_id", nullable = false)
    private Reels reel;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "comment_date")
    private LocalDateTime commentDate;
}
