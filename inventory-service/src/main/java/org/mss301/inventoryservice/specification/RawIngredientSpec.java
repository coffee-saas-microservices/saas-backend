package org.mss301.inventoryservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.mss301.inventoryservice.dto.filter.RawIngredientFilter;
import org.mss301.inventoryservice.entity.RawIngredient;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RawIngredientSpec {

    public static Specification<RawIngredient> filter(RawIngredientFilter filter, Long shopId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("shopId"), shopId));

            if (StringUtils.hasText(filter.getKeyword())) {
                String keyword = "%" + filter.getKeyword().toLowerCase() + "%";
                Predicate nameInfo = cb.like(cb.lower(root.get("name")), keyword);
                Predicate skuInfo = cb.like(cb.lower(root.get("skuCode")), keyword);

                predicates.add(cb.or(nameInfo, skuInfo));
            }

            if (filter.getStorageType() != null) {
                predicates.add(cb.equal(root.get("storageType"), filter.getStorageType()));
            }

            if (filter.getInventoryStatus() != null) {
                predicates.add(cb.equal(root.get("inventoryStatus"), filter.getInventoryStatus()));
            }

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
