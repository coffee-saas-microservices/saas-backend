package org.mss301.commonservice.multitenancy;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class TenantFilter implements Filter {

    private final TenantResolver tenantResolver;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        if (isExcludedPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        String host = httpRequest.getHeader("X-Forwarded-Host");
        if (host == null || host.isEmpty()) {
            host = httpRequest.getHeader("Host");
        }

        if (host == null || host.isEmpty()) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Missing tenant domain. Provide 'X-Forwarded-Host' or 'Host' header.");
            return;
        }

        String domain = host.split(":")[0].toLowerCase().trim();

        if (isLocalOrIp(domain)) {
            domain = "abc-shop.com";
        }

        Long shopId = tenantResolver.resolveShopId(domain);

        if (shopId == null) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Shop not found for domain: " + domain);
            return;
        }

        TenantContext.setCurrentShopId(shopId);

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clearCurrentShopId();
        }
    }

    private boolean isExcludedPath(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator")
                || path.startsWith("/api/system/");
    }

    private boolean isLocalOrIp(String domain) {
        return domain.equals("localhost")
                || domain.equals("127.0.0.1")
                || domain.equals("::1")
                || domain.equals("0:0:0:0:0:0:0:1")
                || domain.matches("\\d+\\.\\d+\\.\\d+\\.\\d+");
    }
}
