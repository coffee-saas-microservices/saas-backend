package vdhxi.catalogservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vdhxi.catalogservice.common.dto.response.ApiResponse;
import vdhxi.catalogservice.dto.response.menu.MenuCategoryResponse;
import vdhxi.catalogservice.service.MenuService;

import java.util.List;

/**
 * API công khai hiển thị menu cho khách hàng.
 * Không yêu cầu xác thực JWT — được permit trong SecurityConfig.
 * Dữ liệu được scope tự động theo shop (tenant) qua TenantFilter.
 */
@RestController
@RequestMapping("/api/public/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * Lấy toàn bộ menu của shop, gom theo danh mục.
     * Chỉ trả về danh mục và sản phẩm đang ACTIVE.
     *
     * GET /api/public/menu
     */
    @GetMapping
    public ApiResponse<List<MenuCategoryResponse>> getFullMenu() {
        return ApiResponse.success(
                HttpStatus.OK,
                "Lấy menu thành công",
                menuService.getFullMenu(),
                null
        );
    }

    /**
     * Lấy sản phẩm của một danh mục cụ thể.
     *
     * GET /api/public/menu/categories/{categoryId}
     */
    @GetMapping("/categories/{categoryId}")
    public ApiResponse<MenuCategoryResponse> getMenuByCategory(@PathVariable Long categoryId) {
        return ApiResponse.success(
                HttpStatus.OK,
                "Lấy danh mục thành công",
                menuService.getMenuByCategory(categoryId),
                null
        );
    }
}
