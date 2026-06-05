package org.mss301.employeeservice.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import org.mss301.employeeservice.dto.request.EmployeeFilter;
import org.mss301.employeeservice.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class EmployeeSpecification {
    public static Specification<Employee> filter(Long shopId, EmployeeFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("shopId"), shopId));
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
