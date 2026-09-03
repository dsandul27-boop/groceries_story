package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor

public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "street", nullable = false, length = 100)
    private String street;

    @Column(name = "house_number", length = 20)
    private String houseNumber;

    @Column(name = "apartment", length = 20)
    private String  apartment;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

}
