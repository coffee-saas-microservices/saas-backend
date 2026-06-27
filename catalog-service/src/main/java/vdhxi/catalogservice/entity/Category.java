package vdhxi.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;
import vdhxi.catalogservice.common.entity.BaseEntity;
import vdhxi.catalogservice.enums.Status;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
