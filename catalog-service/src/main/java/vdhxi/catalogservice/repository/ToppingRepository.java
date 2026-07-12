package vdhxi.catalogservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.Topping;
import java.util.List;

@Repository
public interface ToppingRepository extends JpaRepository<Topping, Long> {
    List<Topping> findByShopId(Long shopId);

    @Query("SELECT t FROM Topping t WHERE t.shopId = :shopId " +
           "AND (:search IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Topping> findByFilter(@Param("shopId") Long shopId,
                               @Param("search") String search,
                               Pageable pageable);
}
