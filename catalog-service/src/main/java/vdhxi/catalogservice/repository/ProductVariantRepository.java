package vdhxi.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.ProductVariant;
import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByShopId(Long shopId);
    List<ProductVariant> findByProductId(Long productId);
}
