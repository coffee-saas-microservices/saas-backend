package org.mss301.subscriptionservice.entity.enumeration;

public enum ShopSubscriptionStatus {
    PENDING,   // vừa tạo, chờ thanh toán
    ACTIVE,    // đã thanh toán, đang hiệu lực
    EXPIRED,
    CANCELLED
}
