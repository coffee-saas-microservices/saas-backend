package org.mss301.inventoryservice.dto.filter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryInvoiceFilter extends BaseFilter {
    String search;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime fromDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime toDate;
    Long createdBy;
    InventoryStatus inventoryStatus;
}
