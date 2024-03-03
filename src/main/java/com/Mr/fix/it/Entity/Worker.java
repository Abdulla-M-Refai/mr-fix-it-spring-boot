package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "worker")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Worker
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "rate", nullable = false)
    private Float rate;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "worker",
        cascade = CascadeType.ALL
    )
    private List<WorkingLocation> workingLocations;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "worker",
        cascade = CascadeType.ALL
    )
    private List<PreviousWork> previousWorks;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "worker",
        cascade = CascadeType.ALL
    )
    private List<Task> tasks;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "worker",
        cascade = CascadeType.ALL
    )
    private List<Offer> offers;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "worker",
        cascade = CascadeType.ALL
    )
    private List<Ads> ads;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "worker",
        cascade = CascadeType.ALL
    )
    private List<Reels> reels;
}