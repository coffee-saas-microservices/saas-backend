package vdhxi.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;
import vdhxi.catalogservice.common.entity.BaseEntity;
import vdhxi.catalogservice.enums.Status;

@Entity
@Table(name = "recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "raw_ingredient_id", nullable = false)
    private Long rawIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topping_id")
    private Topping topping;

    @Column(name = "quantity_required")
    private Integer quantityRequired;

    @Column(name = "note", length = 255)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
