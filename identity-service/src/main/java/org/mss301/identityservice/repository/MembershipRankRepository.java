package org.mss301.identityservice.repository;

import org.mss301.identityservice.entity.MembershipRank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRankRepository extends JpaRepository<MembershipRank, Long> {
    Optional<MembershipRank> findFirstByShopIdOrderByRequiredPointsAsc(Long shopId);
}
