package vdhxi.catalogservice.common.multitenancy;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

public class TenantContext {

    public static final String SHOP_ID_HEADER = "X-Shop-Id";

    public static Long getCurrentShopId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String shopIdStr = request.getHeader(SHOP_ID_HEADER);
            if (shopIdStr != null && !shopIdStr.isEmpty()) {
                return Long.parseLong(shopIdStr);
            }
        }
        // Throwing an exception here might be better, but returning null or a default handles cases where it's not required.
        // For standard microservice practice, if shop_id is mandatory, we should throw.
        throw new IllegalStateException("Shop ID not found in request headers");
    }
}
