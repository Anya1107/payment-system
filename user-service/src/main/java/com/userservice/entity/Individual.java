package com.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "individuals", schema = "person")
@Getter
@Setter
@NoArgsConstructor
public class Individual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    private String status;

    @PrePersist
    public void onCreate() {
        verifiedAt = LocalDateTime.now();
        archivedAt = LocalDateTime.of(2999, 12, 31, 0, 0);
        status = IndividualStatus.ACTIVE.name();
    }
}
