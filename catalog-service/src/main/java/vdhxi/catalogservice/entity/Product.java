package vdhxi.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;
import vdhxi.catalogservice.common.entity.BaseEntity;
import vdhxi.catalogservice.enums.Status;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "image_url", length = 100, nullable = true)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
