package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "task_img")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskImg
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "img", nullable = false)
    private String img;
}