package com.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "person")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "secret_key")
    private String secretKey;

    private String email;

    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime updated;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private Boolean filled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Individual individual;

    @PrePersist
    public void onCreate() {
        created = updated = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updated = LocalDateTime.now();
    }
}
