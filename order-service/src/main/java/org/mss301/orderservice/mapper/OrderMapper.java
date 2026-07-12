package org.mss301.orderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mss301.orderservice.dto.request.OrderItemRequest;
import org.mss301.orderservice.dto.request.OrderRequest;
import org.mss301.orderservice.dto.request.ToppingItemRequest;
import org.mss301.orderservice.dto.response.OrderItemResponse;
import org.mss301.orderservice.dto.response.OrderResponse;
import org.mss301.orderservice.dto.response.ToppingPerOrderItemResponse;
import org.mss301.orderservice.entity.Order;
import org.mss301.orderservice.entity.OrderItem;
import org.mss301.orderservice.entity.ToppingPerOrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {


    @Mapping(target = "orderStatus", source = "status")
    @Mapping(target = "orderItems", source = "items")
    @Mapping(target = "productQuantity",
             expression = "java(order.getProductQuantity() != null ? order.getProductQuantity() : 0)")
    @Mapping(target = "payUrl", ignore = true)
    OrderResponse toResponse(Order order);

    @Mapping(target = "orderItemStatus", source = "status")
    @Mapping(target = "toppingPerOrderItems", source = "toppings")
    @Mapping(target = "unitPrice", source = "unitPrice")
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "sizeName", ignore = true)
    OrderItemResponse toItemResponse(OrderItem item);

    @Mapping(target = "price", source = "unitPrice")
    @Mapping(target = "quantity",
             expression = "java(topping.getQuantity() != null ? topping.getQuantity() : 0)")
    @Mapping(target = "toppingName", source = "toppingName")
    ToppingPerOrderItemResponse toToppingResponse(ToppingPerOrderItem topping);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "basePrice", ignore = true)
    @Mapping(target = "paidPrice", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "productQuantity", ignore = true)
    @Mapping(target = "invoiceUrl", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "items", ignore = true)
    Order toEntity(OrderRequest request);

    @Mapping(target = "orderItemId", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "toppings", ignore = true)
    OrderItem toEntity(OrderItemRequest request);

    @Mapping(target = "toppingPerOrderItemId", ignore = true)
    @Mapping(target = "orderItem", ignore = true)
    @Mapping(target = "toppingName", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ToppingPerOrderItem toEntity(ToppingItemRequest request);
}
