package com.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "countries", schema = "person")
@Getter
@Setter
@NoArgsConstructor
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime created;

    private LocalDateTime updated;

    private String name;

    private String alpha2;

    private String alpha3;

    private String status;
}