package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "reels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reels
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(name = "video", nullable = false)
    private String video;

    @Column(name = "post_date", nullable = false)
    private LocalDateTime postDate;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "reel",
        cascade = CascadeType.ALL
    )
    private List<Like> likes;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "reel",
        cascade = CascadeType.ALL
    )
    private List<Comment> comments;
}
