package org.mss301.identityservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.mss301.identityservice.entity.enumeration.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String username;
    private String fullname;
    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "keycloak_user_id", nullable = false, unique = true, length = 100)
    private String keycloakUserId;

    @Column(name = "total_point", nullable = true)
    private Double totalPoint;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_id", nullable = true)
    private MembershipRank membershipRank;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
