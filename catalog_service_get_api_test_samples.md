# Catalog Service — JSON mẫu GET APIs

> Không truyền `sort` → mặc định sort theo `createdAt:desc`.
> Nếu muốn sort, nhập trực tiếp `name:asc` vào ô `sort` trên Swagger (không dùng JSON array).

---

## GET /api/categories

```json
{
  "page": 1,
  "pageSize": 10,
  "search": "cà phê"
}
```

## GET /api/products

```json
{
  "page": 1,
  "pageSize": 10,
  "search": "cà phê sữa",
  "categoryId": 1
}
```

## GET /api/product-variants

```json
{
  "page": 1,
  "pageSize": 10,
  "search": "CPSD",
  "productId": 1
}
```

## GET /api/sizes

```json
{
  "page": 1,
  "pageSize": 10,
  "search": "Nhỏ"
}
```

## GET /api/toppings

```json
{
  "page": 1,
  "pageSize": 10,
  "search": "trân châu"
}
```

## GET /api/recipes

```json
{
  "page": 1,
  "pageSize": 10,
  "productVariantId": 1
}
```

## GET /api/combo-items

```json
{
  "page": 1,
  "pageSize": 10,
  "productId": 5
}
```

## GET by ID

| API | `id` |
|-----|------|
| `GET /api/categories/{id}` | `1` |
| `GET /api/products/{id}` | `1` |
| `GET /api/product-variants/{id}` | `1` |
| `GET /api/sizes/{id}` | `1` |
| `GET /api/toppings/{id}` | `1` |

## GET Menu (Public)

| API | Param |
|-----|-------|
| `GET /api/public/menu` | Không cần |
| `GET /api/public/menu/categories/{categoryId}` | `categoryId` = `1` |

## GET Internal Recipes

| API | Param |
|-----|-------|
| `GET /api/internal/recipes/variant/{variantId}` | `variantId` = `1` |
| `GET /api/internal/recipes/topping/{toppingId}` | `toppingId` = `1` |
