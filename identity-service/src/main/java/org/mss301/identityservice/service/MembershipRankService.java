package org.mss301.identityservice.service;

import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.identityservice.dto.request.MembershipRankFilter;
import org.mss301.identityservice.dto.request.MembershipRankRequest;
import org.mss301.identityservice.dto.response.MembershipRankResponse;

public interface MembershipRankService {
    MembershipRankResponse createMembershipRank(MembershipRankRequest request);
    PageResponse<MembershipRankResponse> getMembershipRanks(MembershipRankFilter filter);
    MembershipRankResponse getRankById(Long id);
    MembershipRankResponse updateRank(Long id, MembershipRankRequest request);
    void deleteRank(Long id);
}
