package vdhxi.catalogservice.service;

import vdhxi.catalogservice.dto.response.menu.MenuCategoryResponse;

import java.util.List;

public interface MenuService {

    /**
     * Trả về toàn bộ menu của shop hiện tại (theo tenant),
     * bao gồm danh sách danh mục ACTIVE, mỗi danh mục chứa
     * các sản phẩm ACTIVE cùng biến thể (size/giá) và topping cho phép.
     */
    List<MenuCategoryResponse> getFullMenu();

    /**
     * Trả về danh sách sản phẩm ACTIVE của một danh mục cụ thể.
     */
    MenuCategoryResponse getMenuByCategory(Long categoryId);
}
