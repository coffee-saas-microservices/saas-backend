package vdhxi.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.ProductAllowedTopping;
import java.util.List;

@Repository
public interface ProductAllowedToppingRepository extends JpaRepository<ProductAllowedTopping, Long> {
    List<ProductAllowedTopping> findByProductId(Long productId);
}
