package vdhxi.catalogservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.Recipe;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByShopId(Long shopId);
    List<Recipe> findByProductVariantId(Long productVariantId);
    Page<Recipe> findByProductVariantId(@Param("productVariantId") Long productVariantId, Pageable pageable);
    List<Recipe> findByToppingId(Long toppingId);
}

