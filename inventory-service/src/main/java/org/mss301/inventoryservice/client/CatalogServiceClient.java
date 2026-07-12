package vdhxi.catalogservice.client;

import org.apache.catalina.connector.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "catalog-service")
public interface CatalogServiceClient {

    @GetMapping("/api/internal/recipes/variant/{variantId}")
    ResponseEntity<Map<String, Object>> getRecipesByVariant(
            @PathVariable("variantId") Long variantId);

    @GetMapping("/api/internal/recipes/topping/{toppingId}")
    ResponseEntity<Map<String, Object>> getRecipesByTopping(
            @PathVariable("toppingId") Long toppingId);
}
