package org.mss301.commonservice.multitenancy;

public interface TenantResolver {
    Long resolveShopId(String domain);
}
