package org.mss301.subscriptionservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.subscriptionservice.dto.request.SubscriptionPlanRequest;
import org.mss301.subscriptionservice.dto.response.SubscriptionPlanResponse;
import org.mss301.subscriptionservice.entity.SubscriptionPlan;
import org.mss301.subscriptionservice.entity.enumeration.SubscriptionPlanStatus;
import org.mss301.subscriptionservice.mapper.SubscriptionPlanMapper;
import org.mss301.subscriptionservice.repository.SubscriptionPlanRepository;
import org.mss301.subscriptionservice.service.SubscriptionPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public SubscriptionPlanResponse createSubscriptionPlan(SubscriptionPlanRequest subscriptionPlanRequest) {
        SubscriptionPlan plan = subscriptionPlanMapper.toEntity(subscriptionPlanRequest);
        plan.setSubscriptionPlanStatus(SubscriptionPlanStatus.ACTIVE);

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        log.info("Đã tạo subscription plan: {}", saved.getSubscriptionPlanName());

        return subscriptionPlanMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getAllSubscriptionPlan() {
        return subscriptionPlanRepository.findAllBySubscriptionPlanStatus(SubscriptionPlanStatus.ACTIVE)
                .stream()
                .map(subscriptionPlanMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionPlanResponse getSubscriptionPlanById(Long id) {
        SubscriptionPlan plan = findPlanOrThrow(id);
        return subscriptionPlanMapper.toResponse(plan);
    }

    @Override
    public SubscriptionPlanResponse updateSubscriptionPlan(SubscriptionPlanRequest subscriptionPlanRequest, Long id) {
        SubscriptionPlan plan = findPlanOrThrow(id);

        subscriptionPlanMapper.updateEntityFromRequest(subscriptionPlanRequest, plan);

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        log.info("Đã cập nhật subscription plan: {}", saved.getSubscriptionPlanName());

        return subscriptionPlanMapper.toResponse(saved);
    }

    @Override
    public void deleteSubscriptionPlan(Long id) {
        SubscriptionPlan plan = findPlanOrThrow(id);

        plan.setSubscriptionPlanStatus(SubscriptionPlanStatus.INACTIVE);
        subscriptionPlanRepository.save(plan);
        log.info("Đã vô hiệu hóa subscription plan: {}", plan.getSubscriptionPlanName());
    }

    // Chỉ lấy gói đang ACTIVE -> gói đã xóa mềm (INACTIVE) coi như không tồn tại,
    // giúp getById/update/delete nhất quán với getAll.
    private SubscriptionPlan findPlanOrThrow(Long id) {
        return subscriptionPlanRepository
                .findBySubscriptionPlanIdAndSubscriptionPlanStatus(id, SubscriptionPlanStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("Gói dịch vụ không tồn tại"));
    }
}
