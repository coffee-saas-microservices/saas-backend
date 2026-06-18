package org.mss301.inventoryservice.repository;

import org.mss301.inventoryservice.entity.StockCheckDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockCheckDetailRepository extends JpaRepository<StockCheckDetail, Long> {

    Optional<StockCheckDetail> findBySessionIdAndIngredientId(Long sessionId, Long ingredientId);

    List<StockCheckDetail> findAllBySessionId(Long sessionId);
}
