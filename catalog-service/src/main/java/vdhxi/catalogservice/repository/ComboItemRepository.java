package vdhxi.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.ComboItem;
import java.util.List;

@Repository
public interface ComboItemRepository extends JpaRepository<ComboItem, Long> {
    List<ComboItem> findByProductId(Long productId);
}
