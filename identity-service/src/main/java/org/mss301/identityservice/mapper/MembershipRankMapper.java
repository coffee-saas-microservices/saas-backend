package org.mss301.identityservice.mapper;

import org.mapstruct.*;
import org.mss301.identityservice.dto.request.MembershipRankRequest;
import org.mss301.identityservice.dto.response.MembershipRankResponse;
import org.mss301.identityservice.entity.MembershipRank;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MembershipRankMapper {

    MembershipRank toEntity(MembershipRankRequest request);
    MembershipRankResponse toResponse(MembershipRank membershipRank);
    List<MembershipRankResponse> toResponseList(List<MembershipRank> membershipRanks);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRankFromRequest(MembershipRankRequest request, @MappingTarget MembershipRank membershipRank);
}

