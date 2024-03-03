package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.Enum.TaskType;
import com.Mr.fix.it.Entity.Enum.TaskStatus;

@Entity
@Table(name = "task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "locality", nullable = false)
    private String locality;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskType type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "task",
        cascade = CascadeType.ALL
    )
    private List<TaskImg> taskImgs;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "task",
        cascade = CascadeType.ALL
    )
    private List<Offer> offers;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;
}