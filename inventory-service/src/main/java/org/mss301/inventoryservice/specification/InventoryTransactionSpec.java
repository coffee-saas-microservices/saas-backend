package org.mss301.inventoryservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.mss301.inventoryservice.dto.filter.InventoryTransactionFilter;
import org.mss301.inventoryservice.entity.InventoryTransaction;
import org.mss301.inventoryservice.entity.enumeration.TransactionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class InventoryTransactionSpec {

    public static Specification<InventoryTransaction> filter(InventoryTransactionFilter filter, Long shopId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("shopId"), shopId));

            if (filter.getIngredientId() != null) {
                predicates.add(cb.equal(root.get("ingredient").get("id"), filter.getIngredientId()));
            }

            if (filter.getBatchId() != null) {
                predicates.add(cb.equal(root.get("batch").get("id"), filter.getBatchId()));
            }

            if (StringUtils.hasText(filter.getTransactionType())) {
                predicates.add(cb.equal(root.get("transactionType"), TransactionType.valueOf(filter.getTransactionType())));
            }

            if (StringUtils.hasText(filter.getReferenceCode())) {
                Predicate orderIdPred = cb.like(root.get("orderId").as(String.class), "%" + filter.getReferenceCode() + "%");
                predicates.add(orderIdPred);
            }

            if (filter.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getFromDate()));
            }
            if (filter.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getToDate()));
            }

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
