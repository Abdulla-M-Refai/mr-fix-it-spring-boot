package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "canceled_task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanceledTasks
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "reason", nullable = false)
    private String reason;
}
