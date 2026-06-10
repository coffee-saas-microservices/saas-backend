package org.mss301.employeeservice.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import org.mss301.employeeservice.dto.request.EmployeeUnavailabilityFilter;
import org.mss301.employeeservice.entity.EmployeeUnavailability;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class EmployeeUnavailabilitySpecification {

    public static Specification<EmployeeUnavailability> filter(Long shopId, EmployeeUnavailabilityFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Shop ID is mandatory for multi-tenancy
            predicates.add(cb.equal(root.get("shopId"), shopId));

            if (filter.getEmployeeId() != null) {
                predicates.add(cb.equal(root.get("employee").get("employeeId"), filter.getEmployeeId()));
            }

            if (filter.getIsRecurring() != null) {
                predicates.add(cb.equal(root.get("isRecurring"), filter.getIsRecurring()));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), filter.getStartTime()));
            }

            if (filter.getEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), filter.getEndTime()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
