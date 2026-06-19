package vdhxi.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vdhxi.catalogservice.entity.Topping;
import java.util.List;

@Repository
public interface ToppingRepository extends JpaRepository<Topping, Long> {
    List<Topping> findByShopId(Long shopId);
}
