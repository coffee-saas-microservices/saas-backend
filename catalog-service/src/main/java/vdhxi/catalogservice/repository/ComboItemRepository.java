package vdhxi.catalogservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.ComboItem;
import java.util.List;

@Repository
public interface ComboItemRepository extends JpaRepository<ComboItem, Long> {
    List<ComboItem> findByProductId(Long productId);
    Page<ComboItem> findByProductId(@Param("productId") Long productId, Pageable pageable);
}

