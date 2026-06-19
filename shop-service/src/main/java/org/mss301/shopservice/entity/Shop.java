package org.mss301.shopservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.shopservice.entity.enumeration.ShopStatus;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "shops")
@AllArgsConstructor
@NoArgsConstructor
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "domain", unique = true)
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShopStatus shopStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // --- Gói dịch vụ hiện tại của shop (được subscription-service cập nhật sau khi thanh toán) ---
    @Column(name = "current_plan_id")
    private Long currentPlanId;

    @Column(name = "current_plan_name")
    private String currentPlanName;

    @Column(name = "subscription_status")
    private String subscriptionStatus;

    @Column(name = "subscription_ended_at")
    private LocalDateTime subscriptionEndedAt;
}
