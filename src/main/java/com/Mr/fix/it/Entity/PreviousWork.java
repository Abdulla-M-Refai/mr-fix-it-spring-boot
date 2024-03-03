package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "previous_work")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviousWork
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "previousWork",
        cascade = CascadeType.ALL
    )
    private List<PreviousWorkImg> previousWorkImgs;
}