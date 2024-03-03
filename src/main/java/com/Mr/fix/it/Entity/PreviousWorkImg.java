package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "previous_work_img")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviousWorkImg
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_work_id", nullable = false)
    private PreviousWork previousWork;

    @Column(name = "img", nullable = false)
    private String img;
}