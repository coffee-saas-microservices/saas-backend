package org.mss301.inventoryservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockCheckUpdateRequest {
    @NotNull(message = "Id phiếu kiểm kho là bắt buộc")
    Long sessionId;

    @NotEmpty
    List<StockCheckItemRequest> details;
}
