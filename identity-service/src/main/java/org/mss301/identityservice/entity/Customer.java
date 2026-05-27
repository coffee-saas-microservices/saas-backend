package org.mss301.identityservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.mss301.identityservice.entity.enumeration.CustomerStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CustomerStatus status;

    @Column(name = "keycloak_user_id", nullable = false, unique = true, length = 100)
    private String keycloakUserId;

    @Column(name = "total_point")
    private Double totalPoint;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_id")
    private MembershipRank membershipRank;
}
