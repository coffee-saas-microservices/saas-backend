package org.mss301.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service")
public interface IdentityServiceClient {

    @GetMapping("/api/internal/users/{id}/point-rate")
    ResponseEntity<Float> getUserPointRate(@PathVariable("id") Long id);
}
