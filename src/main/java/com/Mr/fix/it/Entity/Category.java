package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type" , unique = true, nullable = false)
    private String type;
}