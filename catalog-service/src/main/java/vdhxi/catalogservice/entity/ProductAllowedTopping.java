package vdhxi.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;
import vdhxi.catalogservice.common.entity.BaseEntity;
import vdhxi.catalogservice.enums.Status;

@Entity
@Table(name = "product_allowed_toppings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAllowedTopping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topping_id", nullable = false)
    private Topping topping;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
