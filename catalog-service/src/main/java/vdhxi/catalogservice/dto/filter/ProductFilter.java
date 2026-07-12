package vdhxi.catalogservice.dto.filter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.mss301.commonservice.dto.request.BaseFilter;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilter extends BaseFilter {
    String search;
    Long categoryId;
}
