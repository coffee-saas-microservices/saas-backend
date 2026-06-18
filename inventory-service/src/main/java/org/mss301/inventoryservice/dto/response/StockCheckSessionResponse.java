package org.mss301.inventoryservice.dto.response;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockCheckSessionResponse {
    Long id;
    String code;
    LocalDateTime startedAt;
    LocalDateTime completedAt;
    InventoryStatus inventoryStatus;
    String createdByName;
    Boolean isApproved;
    List<StockCheckDetailResponse> details;
}
