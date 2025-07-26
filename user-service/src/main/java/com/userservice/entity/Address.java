package com.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "addresses", schema = "person")
@Getter
@Setter
@NoArgsConstructor
@Audited
@AuditTable(value = "addresses_aud", schema = "history")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private LocalDateTime created;

    private LocalDateTime updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    private String address;

    @Column(name = "zip_code")
    private String zipCode;

    private LocalDateTime archived;

    private String city;

    private String state;

    @PrePersist
    public void onCreate() {
        created = updated = LocalDateTime.now();
        archived = LocalDateTime.of(2999, 12, 31, 0, 0);
    }

    @PreUpdate
    public void onUpdate() {
        updated = LocalDateTime.now();
    }
}
