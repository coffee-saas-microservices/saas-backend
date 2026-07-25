package vdhxi.catalogservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.ProductVariant;
import vdhxi.catalogservice.enums.Status;
import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByShopId(Long shopId);
    List<ProductVariant> findByProductId(Long productId);
    List<ProductVariant> findByProductIdAndStatusOrderByPrice(Long productId, Status status);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.shopId = :shopId " +
           "AND (:productId IS NULL OR pv.product.id = :productId) " +
           "AND (CAST(:search AS string) IS NULL OR LOWER(pv.code) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
    Page<ProductVariant> findByFilter(@Param("shopId") Long shopId,
                                      @Param("productId") Long productId,
                                      @Param("search") String search,
                                      Pageable pageable);
}
