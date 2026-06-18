package org.mss301.inventoryservice.query.query;

import lombok.Data;
import org.mss301.commonservice.dto.request.BaseFilter;

@Data
public class GetAllRawIngredientsQuery extends BaseFilter {
    private String shopId;
    private String name;
}
