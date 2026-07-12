package org.mss301.orderservice.client;

import org.mss301.commonservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "catalog-service")
public interface CatalogServiceClient {

    @GetMapping("/api/product-variants/{id}")
    ApiResponse<Map<String, Object>> getProductVariantById(@PathVariable("id") Long id);

    @GetMapping("/api/toppings/{id}")
    ApiResponse<Map<String, Object>> getToppingById(@PathVariable("id") Long id);

    @GetMapping("/api/products/{id}")
    ApiResponse<Map<String, Object>> getProductById(@PathVariable("id") Long id);
}
