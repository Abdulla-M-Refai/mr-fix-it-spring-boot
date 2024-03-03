package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "perfection_rate", nullable = false)
    private Float perfectionRate;

    @Column(name = "treatment_rate", nullable = false)
    private Float treatmentRate;

    @Column(name = "additional_info", nullable = false)
    private String additionalInfo;
}