package org.mss301.shopservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.shopservice.dto.request.SubscriptionPlanRequest;
import org.mss301.shopservice.dto.response.SubscriptionPlanResponse;
import org.mss301.shopservice.entity.SubscriptionPlan;
import org.mss301.shopservice.entity.enumeration.SubscriptionPlanStatus;
import org.mss301.shopservice.mapper.SubscriptionPlanMapper;
import org.mss301.shopservice.repository.SubscriptionPlanRepository;
import org.mss301.shopservice.service.SubscriptionPlanService;
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
    public SubscriptionPlanResponse createSubscriptionPlan(SubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanMapper.toEntity(request);
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
        return subscriptionPlanMapper.toResponse(findPlanOrThrow(id));
    }

    @Override
    public SubscriptionPlanResponse updateSubscriptionPlan(SubscriptionPlanRequest request, Long id) {
        SubscriptionPlan plan = findPlanOrThrow(id);
        subscriptionPlanMapper.updateEntityFromRequest(request, plan);

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

    private SubscriptionPlan findPlanOrThrow(Long id) {
        return subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Gói dịch vụ không tồn tại"));
    }
}
