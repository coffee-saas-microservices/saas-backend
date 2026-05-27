package org.mss301.commonservice.multitenancy;

import java.util.concurrent.ConcurrentHashMap;

public class CachingTenantResolver implements TenantResolver {

    private final TenantResolver delegate;
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public CachingTenantResolver(TenantResolver delegate, long ttlMillis) {
        this.delegate = delegate;
        this.ttlMillis = ttlMillis;
    }

    @Override
    public Long resolveShopId(String domain) {
        cache.entrySet().removeIf(e -> e.getValue().isExpired());

        CacheEntry entry = cache.get(domain);

        if (entry != null) {
            return entry.shopId();
        }

        Long shopId = delegate.resolveShopId(domain);

        if (shopId != null) {
            cache.put(domain, new CacheEntry(shopId, System.currentTimeMillis() + ttlMillis));
        }
        return shopId;
    }

    private record CacheEntry(Long shopId, long expiresAt) {
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
