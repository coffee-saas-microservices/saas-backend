package org.mss301.shopservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopResponse {
    private Long id;
    private String shopName;
    private String domain;
    private String status;
}
