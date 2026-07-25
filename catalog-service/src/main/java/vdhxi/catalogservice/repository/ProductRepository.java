package vdhxi.catalogservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.Product;
import vdhxi.catalogservice.enums.Status;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByShopId(Long shopId);

    List<Product> findByCategoryIdAndStatusOrderByName(Long categoryId, Status status);

    @Query("SELECT p FROM Product p WHERE p.shopId = :shopId " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (CAST(:search AS string) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
    Page<Product> findByFilter(@Param("shopId") Long shopId,
                               @Param("categoryId") Long categoryId,
                               @Param("search") String search,
                               Pageable pageable);
}
