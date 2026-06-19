package vdhxi.catalogservice.mapper;

import org.springframework.stereotype.Component;
import vdhxi.catalogservice.dto.request.ToppingRequest;
import vdhxi.catalogservice.dto.response.ToppingResponse;
import vdhxi.catalogservice.entity.Topping;

@Component
public class ToppingMapper {
    public ToppingResponse toResponse(Topping entity) {
        if (entity == null) return null;
        ToppingResponse response = new ToppingResponse();
        response.setId(entity.getId());
        response.setShopId(entity.getShopId());
        response.setName(entity.getName());
        response.setStatus(entity.getStatus());
        return response;
    }

    public void updateEntity(Topping entity, ToppingRequest request) {
        if (request.getName() != null) entity.setName(request.getName());
    }
}
