package org.mss301.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

//tự động tạo các kafka topic khi order-service khởi động
//nếu topic tồn tại -> kafka bỏ qua
//nếu không khai báo ở đây -> topic vẫn được tạo tự động khi có message đầu tiên
//nhưng với cấu hình mặc định (1 partition, 1 replica) -> ko tối ưu
//khai báo tường minh = kiểm soát số partition và replica ngay từ đầu

@Configuration
public class KafkaTopicConfig {
    //3 partitions: cho phép tối đa 3 consumer instances xử lý song song
    //replica = 1: chỉ có 1 broker trong dev, ko > 1
    //trong production replicas > 2 để fault tolerance

    //topic nhận event mới khi tạo: order.created
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name("order.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    //topic nhận kết quả xử lý thanh toán: payment.status
    @Bean
    public NewTopic paymentStatusTopic() {
        return TopicBuilder.name("payment.status")
                .partitions(3)
                .replicas(1)
                .build();
    }

    //topic nhận kết quả trừ kho: inventory.status
    @Bean
    public NewTopic inventoryStatusTopic() {
        return TopicBuilder.name("inventory.status")
                .partitions(3)
                .replicas(1)
                .build();
    }

    //topic yêu cầu hoàn tiền khi inventory thất bại: order.refund.requested
    @Bean
    public NewTopic orderRefundRequestedTopic() {
        return TopicBuilder.name("order.refund.requested")
                .partitions(3)
                .replicas(1)
                .build();
    }

    //topic nhận payUrl reply từ payment-service: payment.url
    @Bean
    public NewTopic paymentUrlTopic() {
        return TopicBuilder.name("payment.url")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
