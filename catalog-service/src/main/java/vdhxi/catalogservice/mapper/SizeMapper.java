package vdhxi.catalogservice.mapper;

import org.springframework.stereotype.Component;
import vdhxi.catalogservice.dto.request.SizeRequest;
import vdhxi.catalogservice.dto.response.SizeResponse;
import vdhxi.catalogservice.entity.Size;

@Component
public class SizeMapper {
    public SizeResponse toResponse(Size entity) {
        if (entity == null) return null;
        SizeResponse response = new SizeResponse();
        response.setId(entity.getId());
        response.setShopId(entity.getShopId());
        response.setName(entity.getName());
        response.setStatus(entity.getStatus());
        return response;
    }

    public void updateEntity(Size entity, SizeRequest request) {
        if (request.getName() != null) entity.setName(request.getName());
    }
}
