package org.mss301.identityservice.repository;

import org.mss301.identityservice.entity.MembershipRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MembershipRankRepository extends JpaRepository<MembershipRank, Long>, JpaSpecificationExecutor<MembershipRank> {
    Optional<MembershipRank> findFirstByShopIdOrderByRequiredPointsAsc(Long shopId);
}
