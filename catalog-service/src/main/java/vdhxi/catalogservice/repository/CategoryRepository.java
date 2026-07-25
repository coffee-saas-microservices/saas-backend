package vdhxi.catalogservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.Category;
import vdhxi.catalogservice.enums.Status;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByShopId(Long shopId);

    List<Category> findByShopIdAndStatus(Long shopId, Status status);

    @Query("SELECT c FROM Category c WHERE c.shopId = :shopId " +
           "AND (CAST(:search AS string) IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
    Page<Category> findByFilter(@Param("shopId") Long shopId,
                                @Param("search") String search,
                                Pageable pageable);
}
