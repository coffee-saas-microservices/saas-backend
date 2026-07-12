package org.mss301.subscriptionservice.mapper;

import javax.annotation.processing.Generated;
import org.mss301.subscriptionservice.dto.request.SubscriptionPlanRequest;
import org.mss301.subscriptionservice.dto.response.SubscriptionPlanResponse;
import org.mss301.subscriptionservice.entity.SubscriptionPlan;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-26T09:16:40+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class SubscriptionPlanMapperImpl implements SubscriptionPlanMapper {

    @Override
    public SubscriptionPlan toEntity(SubscriptionPlanRequest request) {
        if ( request == null ) {
            return null;
        }

        SubscriptionPlan.SubscriptionPlanBuilder subscriptionPlan = SubscriptionPlan.builder();

        subscriptionPlan.configLimit( mapToJsonString( request.getConfigLimit() ) );
        subscriptionPlan.subscriptionPlanName( request.getSubscriptionPlanName() );
        subscriptionPlan.subscriptionPlanDescription( request.getSubscriptionPlanDescription() );
        subscriptionPlan.priceMonthly( request.getPriceMonthly() );
        subscriptionPlan.priceYearly( request.getPriceYearly() );
        subscriptionPlan.subscriptionPlanStatus( request.getSubscriptionPlanStatus() );

        return subscriptionPlan.build();
    }

    @Override
    public SubscriptionPlanResponse toResponse(SubscriptionPlan entity) {
        if ( entity == null ) {
            return null;
        }

        SubscriptionPlanResponse.SubscriptionPlanResponseBuilder subscriptionPlanResponse = SubscriptionPlanResponse.builder();

        subscriptionPlanResponse.configLimit( jsonStringToMap( entity.getConfigLimit() ) );
        subscriptionPlanResponse.subscriptionPlanId( entity.getSubscriptionPlanId() );
        subscriptionPlanResponse.subscriptionPlanName( entity.getSubscriptionPlanName() );
        subscriptionPlanResponse.subscriptionPlanDescription( entity.getSubscriptionPlanDescription() );
        subscriptionPlanResponse.priceMonthly( entity.getPriceMonthly() );
        subscriptionPlanResponse.priceYearly( entity.getPriceYearly() );
        subscriptionPlanResponse.subscriptionPlanStatus( entity.getSubscriptionPlanStatus() );
        subscriptionPlanResponse.createdAt( entity.getCreatedAt() );
        subscriptionPlanResponse.updatedAt( entity.getUpdatedAt() );

        return subscriptionPlanResponse.build();
    }

    @Override
    public void updateEntityFromRequest(SubscriptionPlanRequest request, SubscriptionPlan entity) {
        if ( request == null ) {
            return;
        }

        entity.setConfigLimit( mapToJsonString( request.getConfigLimit() ) );
        entity.setSubscriptionPlanName( request.getSubscriptionPlanName() );
        entity.setSubscriptionPlanDescription( request.getSubscriptionPlanDescription() );
        entity.setPriceMonthly( request.getPriceMonthly() );
        entity.setPriceYearly( request.getPriceYearly() );
        entity.setSubscriptionPlanStatus( request.getSubscriptionPlanStatus() );
    }
}
