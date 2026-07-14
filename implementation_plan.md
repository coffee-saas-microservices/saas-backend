# Tích hợp Kafka cho luồng Order trong kiến trúc Microservices

## Tổng quan hiện trạng

Hệ thống SaaS Coffee hiện tại gồm **7 microservices** giao tiếp đồng bộ qua **OpenFeign + Eureka**:

| Service | Vai trò | Giao tiếp hiện tại |
|---------|---------|-------------------|
| `api-gateway` | Spring Cloud Gateway, routing | - |
| `discovery-server` | Eureka Server | - |
| `identity-service` | Auth (Keycloak), User, Membership | - |
| `shop-service` | Shop, Subscription Plan, Purchase | Feign → `payment-service` |
| `payment-service` | Payment Order, Confirm | Feign callback → `shop-service` |
| `inventory-service` | Raw Ingredient, Batch, Invoice, Stock | Feign → `catalog-service` |
| `catalog-service` | Product catalog | - |
| `common-service` | Shared lib (DTO, Exception, Multitenancy) | Library (không phải service chạy độc lập) |

### Vấn đề hiện tại (Synchronous Feign Calls)

```
shop-service --Feign--> payment-service --Feign callback--> shop-service
```

- **Tight coupling**: Service A gọi trực tiếp Service B, nếu B down → A cũng fail
- **Cascading failure**: 1 service chậm → kéo chậm toàn bộ chuỗi
- **Không retry tự động**: Feign call thất bại → mất luôn request
- **Blocking thread**: Thread bị giữ trong khi chờ response từ service khác

---

## Luồng Order cần xử lý bất đồng bộ

Khi khách đặt order tại một shop, chuỗi xử lý cần thực hiện:

```
[1] Tạo Order (validate, lưu DB)
    ↓
[2] Thanh toán (gọi Payment Gateway bên thứ 3)
    ↓
[3] Trừ kho (deduct stock theo recipe)
    ↓  
[4] Upload hóa đơn lên Cloudinary
    ↓
[5] Gửi notification cho shop owner
```

> [!WARNING]
> **Các bước 2-3-4-5 không nên chạy đồng bộ** vì:
> - Payment gateway response có thể mất 2-10s
> - Cloudinary upload có thể timeout
> - Nếu 1 bước fail, không nên rollback toàn bộ mà cần retry riêng

---

## Proposed Architecture: Event-Driven with Kafka

### Kiến trúc tổng thể đề xuất

```mermaid
graph TB
    subgraph "Client Layer"
        POS["POS / Mobile App"]
    end

    subgraph "Gateway"
        GW["API Gateway"]
    end

    subgraph "Core Services"
        OS["order-service<br/>(NEW - Orchestrator)"]
        PS["payment-service"]
        IS["inventory-service"]
        NS["notification-service<br/>(NEW)"]
    end

    subgraph "Message Broker"
        K["Apache Kafka"]
    end

    subgraph "External Services"
        PG["Payment Gateway<br/>(VNPay/MoMo/ZaloPay)"]
        CL["Cloudinary"]
    end

    subgraph "Observability"
        ZK["Zipkin / Jaeger<br/>(Distributed Tracing)"]
    end

    POS --> GW
    GW --> OS

    OS -- "order.created" --> K
    K -- "order.created" --> PS
    K -- "order.created" --> IS

    PS -- "payment.completed" --> K
    PS --> PG

    K -- "payment.completed" --> OS
    K -- "payment.completed" --> NS

    IS -- "stock.deducted" --> K
    K -- "stock.deducted" --> OS

    OS -- "order.completed" --> K
    K -- "order.completed" --> NS
    NS --> CL
end
```

### Kafka Topics & Event Flow

| Topic | Producer | Consumer(s) | Mô tả |
|-------|----------|-------------|-------|
| `order.created` | `order-service` | `payment-service`, `inventory-service` | Order mới được tạo |
| `order.status-changed` | `order-service` | `notification-service` | Trạng thái order thay đổi |
| `payment.initiated` | `payment-service` | `order-service` | Link thanh toán đã tạo |
| `payment.completed` | `payment-service` | `order-service`, `notification-service` | Thanh toán thành công |
| `payment.failed` | `payment-service` | `order-service` | Thanh toán thất bại |
| `stock.deducted` | `inventory-service` | `order-service` | Đã trừ kho thành công |
| `stock.insufficient` | `inventory-service` | `order-service` | Không đủ hàng trong kho |
| `invoice.uploaded` | `notification-service` | `order-service` | Hóa đơn đã upload Cloudinary |

---

## Công nghệ & Tool được gợi ý

### 1. Message Broker: Apache Kafka

> [!IMPORTANT]
> **Tại sao chọn Kafka thay vì RabbitMQ?**
> - **Durability**: Message được persist trên disk → không mất dữ liệu
> - **Replay**: Có thể đọc lại message cũ (rất hữu ích khi debug/fix bug)
> - **High throughput**: Xử lý hàng triệu message/s
> - **Consumer Groups**: Nhiều instance cùng service chia nhau xử lý
> - **Phù hợp với Event Sourcing** cho luồng order phức tạp

**Spring Boot integration:**
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### 2. Distributed Tracing: Micrometer Tracing + Zipkin

Khi chuyển sang async, **tracing trở nên cực kỳ quan trọng** để theo dõi một order đi qua nhiều service.

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

- **Zipkin**: Lightweight, dễ setup, visualize latency từng service
- **Trace ID propagation**: Tự động truyền trace-id qua Kafka headers → biết order đi qua đâu

### 3. Schema Registry: Confluent Schema Registry (Avro/JSON Schema)

- Đảm bảo **contract** giữa producer và consumer
- Tránh lỗi deserialization khi schema thay đổi
- Hỗ trợ **schema evolution** (backward/forward compatibility)

### 4. Dead Letter Topic (DLT) Pattern

Khi consumer xử lý message thất bại sau N lần retry → chuyển sang **Dead Letter Topic** để xử lý riêng:

```
order.created → [consumer fail] → order.created.DLT
```

Spring Kafka hỗ trợ sẵn:
```java
@RetryableTopic(
    attempts = "3",
    backoff = @Backoff(delay = 1000, multiplier = 2.0),
    dltStrategy = DltStrategy.FAIL_ON_ERROR
)
@KafkaListener(topics = "order.created")
public void handleOrderCreated(OrderCreatedEvent event) { ... }
```

### 5. Saga Pattern (Choreography-based)

Luồng order có nhiều bước liên quan nhiều service → cần **Saga Pattern** để đảm bảo data consistency:

```
[order-service] → order.created
    ↓
[payment-service] listens → process payment → payment.completed / payment.failed
    ↓
[inventory-service] listens payment.completed → deduct stock → stock.deducted / stock.insufficient
    ↓
[order-service] listens → nếu cả payment + stock OK → order.completed
                         → nếu fail → trigger compensating transaction (refund, restore stock)
```

> [!TIP]
> **Choreography vs Orchestration Saga:**
> - **Choreography** (recommended cho hệ thống hiện tại): Mỗi service tự biết mình cần làm gì khi nhận event → đơn giản hơn, ít single point of failure
> - **Orchestration**: Có một Saga Coordinator điều phối → phức tạp hơn, nhưng dễ debug flow
>
> Với quy mô hiện tại, **Choreography** là phù hợp nhất.

### 6. Resilience: Resilience4j

Thay vì chỉ dùng Kafka retry, cần thêm **circuit breaker** cho các external call:

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
```

- **Circuit Breaker**: Ngắt kết nối khi service bên thứ 3 (VNPay, Cloudinary) down liên tục
- **Rate Limiter**: Giới hạn số request gửi đến payment gateway
- **Retry**: Tự động retry với exponential backoff
- **Bulkhead**: Cô lập thread pool để 1 service chậm không ảnh hưởng service khác

### 7. Monitoring & Alerting

| Tool | Vai trò |
|------|---------|
| **Prometheus + Grafana** | Metrics dashboard (latency, throughput, error rate) |
| **Kafka UI (Conduktor/AKHQ)** | Quản lý topics, consumer lag, message inspect |
| **Spring Boot Actuator** | Health check endpoints cho từng service |

---

## Đề xuất service mới cần tạo

### 1. `order-service` (NEW)

**Vai trò**: Orchestrator chính cho luồng đặt hàng

```
Responsibilities:
├── Nhận request tạo order từ POS/Mobile
├── Validate order (kiểm tra sản phẩm, giá, shop)
├── Lưu order với trạng thái PENDING
├── Publish event "order.created" lên Kafka
├── Listen các event từ payment & inventory
├── Cập nhật trạng thái order theo kết quả
└── Xử lý compensating transaction khi cần
```

### 2. `notification-service` (NEW)

**Vai trò**: Xử lý mọi notification & external upload

```
Responsibilities:
├── Listen event "payment.completed" → gửi notification cho shop
├── Listen event "order.completed" → upload hóa đơn lên Cloudinary
├── Gửi email/SMS/push notification
└── Quản lý template notification
```

---

## Cập nhật Docker Compose

```yaml
services:
  # === Existing ===
  keycloak:
    # ... giữ nguyên

  # === NEW: Kafka Infrastructure ===
  zookeeper:
    image: confluentinc/cp-zookeeper:7.6.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.6.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    ports:
      - "8090:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092

  # === NEW: Distributed Tracing ===
  zipkin:
    image: openzipkin/zipkin:latest
    ports:
      - "9411:9411"
```

---

## Migration Plan (từ Sync → Async)

### Phase 1: Thêm Kafka Infrastructure
- [ ] Thêm Kafka, Zookeeper, Kafka-UI vào Docker Compose
- [ ] Thêm `spring-kafka` dependency vào `common-service`
- [ ] Tạo shared event DTOs trong `common-service`
- [ ] Cấu hình Kafka producer/consumer trong mỗi service

### Phase 2: Tạo `order-service`
- [ ] Tạo service mới với đầy đủ entity: `Order`, `OrderItem`, `OrderStatus`
- [ ] Implement Kafka producer: publish `order.created`
- [ ] Implement Kafka consumers: listen `payment.completed`, `stock.deducted`
- [ ] Implement Saga state machine để track order lifecycle

### Phase 3: Refactor `payment-service`
- [ ] Thêm Kafka consumer: listen `order.created`
- [ ] Thay Feign callback bằng Kafka producer: publish `payment.completed`
- [ ] Giữ Feign Client cho backward compatibility (dual-write period)
- [ ] Tích hợp real Payment Gateway (VNPay/MoMo)

### Phase 4: Refactor `inventory-service`
- [ ] Thêm Kafka consumer: listen `payment.completed`
- [ ] Uncomment + refactor `deductStock()` method (hiện đang comment)
- [ ] Publish `stock.deducted` event sau khi trừ kho

### Phase 5: Tạo `notification-service`
- [ ] Upload hóa đơn lên Cloudinary sau `order.completed`
- [ ] Gửi push notification / WebSocket event đến POS
- [ ] Tích hợp email service (tùy chọn)

### Phase 6: Observability
- [ ] Thêm Zipkin + Micrometer Tracing
- [ ] Thêm Prometheus + Grafana dashboards
- [ ] Setup Kafka consumer lag monitoring

---

## User Review Required

> [!IMPORTANT]
> **Bạn cần xác nhận các điểm sau trước khi tôi bắt đầu implement:**

1. **Order Service**: Bạn đã có sẵn `order-service` nào chưa, hay cần tạo mới hoàn toàn?

2. **Payment Gateway thực tế**: Bạn dự định dùng cổng thanh toán nào? (VNPay, MoMo, ZaloPay, Stripe?) Hiện tại đang dùng mock payment.

3. **Notification**: Bạn muốn notify bằng gì? (Push notification, WebSocket real-time, Email, SMS?)

4. **Phạm vi implement**: Bạn muốn tôi implement phase nào trước?
   - Chỉ Phase 1 (setup Kafka infrastructure + shared events)
   - Phase 1 + 2 (setup + tạo order-service)
   - Full Phase 1-5 (toàn bộ)

5. **Saga Pattern**: Nếu thanh toán thành công nhưng trừ kho thất bại (hết hàng), bạn muốn xử lý thế nào?
   - Hoàn tiền tự động (compensating transaction)
   - Đánh dấu order cần xử lý thủ công
   - Vẫn cho order thành công, cảnh báo shop owner

6. **Cloudinary**: Bạn đã có Cloudinary account/API key chưa? Hóa đơn cần upload là PDF hay image?

## Verification Plan

### Automated Tests
- Unit tests cho Kafka producer/consumer với `@EmbeddedKafka`
- Integration tests cho Saga flow end-to-end
- `docker-compose up` để verify toàn bộ infrastructure

### Manual Verification
- Tạo order qua API Gateway → verify message trên Kafka-UI
- Kiểm tra distributed tracing trên Zipkin
- Simulate failure scenarios (service down, timeout)
