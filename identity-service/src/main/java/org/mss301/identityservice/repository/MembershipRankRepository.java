package org.mss301.identityservice.repository;

import org.mss301.identityservice.entity.MembershipRank;
import org.mss301.identityservice.entity.enumeration.MembershipRankStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MembershipRankRepository
        extends JpaRepository<MembershipRank, Long>, JpaSpecificationExecutor<MembershipRank> {
    Optional<MembershipRank> findFirstByShopIdOrderByRequiredPointsAsc(Long shopId);

    Optional<MembershipRank> findByIdAndShopIdAndStatusNot(Long id, Long shopId, MembershipRankStatus status);
}
