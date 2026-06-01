package org.mss301.identityservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.mss301.identityservice.dto.request.MembershipRankFilter;
import org.mss301.identityservice.entity.MembershipRank;
import org.mss301.identityservice.entity.enumeration.MembershipRankStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MembershipRankSpecification {

    private MembershipRankSpecification() {
    }

    public static Specification<MembershipRank> filter(Long shopId, MembershipRankFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Luôn luôn filter theo shopId để đảm bảo Multi-tenancy
            predicates.add(cb.equal(root.get("shopId"), shopId));

            if (StringUtils.hasText(filter.getKeyword())) {
                predicates.add(cb.like(cb.lower(root.get("rankName")), "%" + filter.getKeyword().trim().toLowerCase() + "%"));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
