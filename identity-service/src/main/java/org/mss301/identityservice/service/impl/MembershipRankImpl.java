package org.mss301.identityservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.identityservice.dto.request.MembershipRankFilter;
import org.mss301.identityservice.dto.request.MembershipRankRequest;
import org.mss301.identityservice.dto.response.MembershipRankResponse;
import org.mss301.identityservice.entity.MembershipRank;
import org.mss301.identityservice.entity.enumeration.MembershipRankStatus;
import org.mss301.identityservice.mapper.MembershipRankMapper;
import org.mss301.identityservice.repository.MembershipRankRepository;
import org.mss301.identityservice.service.MembershipRankService;
import org.mss301.identityservice.specification.MembershipRankSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MembershipRankImpl implements MembershipRankService {

    private final MembershipRankRepository rankRepository;
    private final MembershipRankMapper rankMapper;

    @Override
    @Transactional
    public MembershipRankResponse createMembershipRank(MembershipRankRequest request) {
        Long shopId = requireShopId();

        if (isDuplicateName(shopId, request.getRankName(), null)) {
            throw new BusinessException("Tên hạng thành viên này đã tồn tại trong cửa hàng của bạn!");
        }

        MembershipRank rank = rankMapper.toEntity(request);
        rank.setShopId(shopId);
        if (rank.getStatus() == null) {
            rank.setStatus(MembershipRankStatus.ACTIVE);
        }
        return rankMapper.toResponse(rankRepository.save(rank));
    }

    @Override
    public PageResponse<MembershipRankResponse> getMembershipRanks(MembershipRankFilter filter) {
        Long shopId = requireShopId();

        Specification<MembershipRank> spec = MembershipRankSpecification.filter(shopId, filter)
                .and((root, query, cb) -> cb.notEqual(root.get("status"), MembershipRankStatus.DELETED));
        Page<MembershipRankResponse> page = rankRepository
                .findAll(spec, filter.toPageable())
                .map(rankMapper::toResponse);

        return PageResponse.of(page);
    }

    @Override
    @Transactional
    public MembershipRankResponse updateRank(Long id, MembershipRankRequest request) {
        Long shopId = requireShopId();

        MembershipRank rank = rankRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hạng thành viên"));

        if (!rank.getShopId().equals(shopId)) {
            throw new BusinessException("Bạn không có quyền chỉnh sửa hạng thành viên này");
        }

        if (isDuplicateName(shopId, request.getRankName(), id)) {
            throw new BusinessException("Tên hạng thành viên này đã tồn tại trong cửa hàng của bạn!");
        }

        rankMapper.updateRankFromRequest(request, rank);
        return rankMapper.toResponse(rankRepository.save(rank));
    }

    @Override
    @Transactional
    public void deleteRank(Long id) {
        Long shopId = requireShopId();

        MembershipRank rank = rankRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hạng thành viên"));

        if (!rank.getShopId().equals(shopId)) {
            throw new BusinessException("Bạn không có quyền xóa hạng thành viên này");
        }

        if (rank.getStatus() == MembershipRankStatus.DELETED) {
            throw new BusinessException("Hạng thành viên này đã bị xóa trước đó");
        }

        boolean hasMembers = rank.getUsers() != null && !rank.getUsers().isEmpty();
        if (hasMembers) {
            throw new BusinessException(
                    "Không thể xóa hạng thành viên đang được sử dụng bởi " + rank.getUsers().size() + " khách hàng");
        }

        rank.setStatus(MembershipRankStatus.DELETED);
        rankRepository.save(rank);
    }

    private Long requireShopId() {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null)
            throw new BusinessException("Không tìm thấy cửa hàng");
        return shopId;
    }

    private boolean isDuplicateName(Long shopId, String rankName, Long excludeId) {
        Specification<MembershipRank> spec = (root, query, cb) -> {
            var predicate = cb.and(
                    cb.equal(root.get("shopId"), shopId),
                    cb.equal(cb.lower(root.get("rankName")), rankName.trim().toLowerCase()));
            if (excludeId != null) {
                return cb.and(predicate, cb.notEqual(root.get("id"), excludeId));
            }
            return predicate;
        };
        return rankRepository.exists(spec);
    }
}
