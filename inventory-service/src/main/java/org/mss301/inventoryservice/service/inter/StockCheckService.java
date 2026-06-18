package org.mss301.inventoryservice.service.inter;

import org.mss301.inventoryservice.dto.filter.StockCheckSessionFilter;
import org.mss301.inventoryservice.dto.request.StockCheckApproveRequest;
import org.mss301.inventoryservice.dto.request.StockCheckStartRequest;
import org.mss301.inventoryservice.dto.request.StockCheckUpdateRequest;
import org.mss301.inventoryservice.dto.response.StockCheckSessionResponse;
import org.springframework.data.domain.Page;

public interface StockCheckService {
    StockCheckSessionResponse startSession(StockCheckStartRequest request);
    StockCheckSessionResponse updateCount(StockCheckUpdateRequest request);
    StockCheckSessionResponse approveSession(StockCheckApproveRequest request);
    Page<StockCheckSessionResponse> getAll(StockCheckSessionFilter filter);
}
