package org.mss301.shopservice.entity.enumeration;

public enum ShopSubscriptionStatus {
    PENDING,    // vừa tạo, chờ thanh toán
    ACTIVE,     // đã thanh toán, đang hiệu lực
    EXPIRED,
    CANCELLED
}
