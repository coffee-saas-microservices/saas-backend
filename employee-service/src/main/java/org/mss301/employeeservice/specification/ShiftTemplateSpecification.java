package org.mss301.employeeservice.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import org.mss301.employeeservice.dto.request.ShiftTemplateFilter;
import org.mss301.employeeservice.entity.ShiftTemplate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ShiftTemplateSpecification {

    public static Specification<ShiftTemplate> filter(Long shopId, ShiftTemplateFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("shopId"), shopId));
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + filter.getKeyword().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
