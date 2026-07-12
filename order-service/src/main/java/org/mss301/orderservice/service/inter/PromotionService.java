package org.mss301.orderservice.service.inter;

import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.orderservice.dto.request.PromotionRequest;
import org.mss301.orderservice.dto.response.PromotionResponse;
import org.mss301.orderservice.entity.enumeration.PromotionStatus;
import org.springframework.data.domain.Page;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest request);
    Page<PromotionResponse> getAllPromotions(BaseFilter filter);
    PromotionResponse getPromotionById(Long id);
    PromotionResponse updatePromotion(Long id, PromotionRequest request);
    PromotionResponse changeStatus(Long id, PromotionStatus status);
    void deletePromotion(Long id);
}
