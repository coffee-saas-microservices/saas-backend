package vdhxi.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.Recipe;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByShopId(Long shopId);
    List<Recipe> findByProductVariantId(Long productVariantId);
}
