package org.mss301.identityservice.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import org.mss301.identityservice.dto.request.UserFilter;
import org.mss301.identityservice.entity.User;
import org.mss301.identityservice.entity.enumeration.UserStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class UserSpecification {
    public static Specification<User> filter(Long shopId, UserFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("shopId"), shopId));
            predicates.add(cb.notEqual(root.get("status"), UserStatus.DELETED));

            if (StringUtils.hasText(filter.getKeyword())) {
                String pattern = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullname")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(root.get("phone"), "%" + filter.getKeyword().trim() + "%")
                ));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
