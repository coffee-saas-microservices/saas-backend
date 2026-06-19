package org.mss301.subscriptionservice.entity.enumeration;

public enum SubscriptionTransactionStatus {
    PENDING,   // đã tạo, chờ thanh toán
    SUCCESS,   // thanh toán thành công
    FAILED,
    CANCELLED
}
