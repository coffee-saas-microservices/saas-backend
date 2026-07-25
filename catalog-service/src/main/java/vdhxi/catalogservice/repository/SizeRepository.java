package vdhxi.catalogservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.Size;
import java.util.List;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    List<Size> findByShopId(Long shopId);

    @Query("SELECT s FROM Size s WHERE s.shopId = :shopId " +
           "AND (CAST(:search AS string) IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
    Page<Size> findByFilter(@Param("shopId") Long shopId,
                            @Param("search") String search,
                            Pageable pageable);
}
