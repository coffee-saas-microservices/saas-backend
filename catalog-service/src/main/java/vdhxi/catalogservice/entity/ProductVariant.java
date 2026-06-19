package vdhxi.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;
import vdhxi.catalogservice.common.entity.BaseEntity;
import vdhxi.catalogservice.enums.Status;

@Entity
@Table(name = "product_variants", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "price", nullable = false)
    private Float price;

    @Column(name = "code", length = 10, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
