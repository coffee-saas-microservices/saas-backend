package vdhxi.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.Size;
import java.util.List;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    List<Size> findByShopId(Long shopId);
}
