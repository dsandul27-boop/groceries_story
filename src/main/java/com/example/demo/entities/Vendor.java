package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import lombok.NoArgsConstructor;


@Entity
@Table(name = "vendors")
@Data
@NoArgsConstructor

public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}
