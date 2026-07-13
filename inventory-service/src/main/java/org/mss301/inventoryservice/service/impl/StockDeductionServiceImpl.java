package org.mss301.inventoryservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.OrderCreatedEvent;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.inventoryservice.client.CatalogServiceClient;
import org.mss301.inventoryservice.entity.IngredientBatch;
import org.mss301.inventoryservice.entity.InventoryTransaction;
import org.mss301.inventoryservice.entity.RawIngredient;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;
import org.mss301.inventoryservice.entity.enumeration.TransactionType;
import org.mss301.inventoryservice.repository.IngredientBatchRepository;
import org.mss301.inventoryservice.repository.InventoryTransactionRepository;
import org.mss301.inventoryservice.repository.RawIngredientRepository;
import org.mss301.inventoryservice.service.inter.StockDeductionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockDeductionServiceImpl implements StockDeductionService {


    private final RawIngredientRepository rawIngredientRepository;
    private final IngredientBatchRepository ingredientBatchRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final CatalogServiceClient catalogServiceClient;

    @Override
    @Transactional
    public void deductStockForOrder(OrderCreatedEvent event, Long orderId, Long shopId) {
        log.info("[StockDeduction] Bắt đầu trừ kho cho orderId={}, shopId={}, {} items",
                orderId, shopId, event.getItems().size());

        for (OrderCreatedEvent.OrderItemEventDto item : event.getItems()) {
            List<Map<String, Object>> variantRecipes = fetchRecipeFromCatalog(
                    item.getProductVariantId(), "variant"
            );

            for (Map<String, Object> recipeItem : variantRecipes) {
                Long ingredientId = toLong(recipeItem.get("rawIngredientId"));
                Double quantityPerUnit = toDouble(recipeItem.get("quantityRequired"));

                double totalNeeded = quantityPerUnit * (item.getQuantity() != null ? item.getQuantity() : 1);

                log.debug("[StockDeduction] variantId={} → ingredientId={}, cần trừ: {}",
                        item.getProductVariantId(), ingredientId, totalNeeded);
                deductFromBatches(shopId, ingredientId, totalNeeded, orderId);
            }

            if (item.getToppings() != null) {
                for (OrderCreatedEvent.ToppingDto topping : item.getToppings()) {
                    List<Map<String, Object>> toppingRecipes = fetchRecipeFromCatalog(
                            topping.getToppingId(), "topping"
                    );
                    for (Map<String, Object> recipeItem : toppingRecipes) {
                        Long ingredientId = toLong(recipeItem.get("rawIngredientId"));
                        Double quantityPerUnit = toDouble(recipeItem.get("quantityRequired"));
                        double totalNeeded = quantityPerUnit * topping.getQuantity() * (item.getQuantity() != null ? item.getQuantity() : 1);
                        log.debug("[StockDeduction] toppingId={} → ingredientId={}, cần trừ: {}",
                                topping.getToppingId(), ingredientId, totalNeeded);
                        deductFromBatches(shopId, ingredientId, totalNeeded, orderId);
                    }
                }
            }
            log.info("[StockDeduction] ✅ Hoàn tất trừ kho cho orderId={}", orderId);
        }
    }

    private List<Map<String, Object>> fetchRecipeFromCatalog(Long id, String type) {
        try {
            ResponseEntity<Map<String, Object>> response;
            if ("variant".equals(type)) {
                response = catalogServiceClient.getRecipesByVariant(id);
            } else {
                response = catalogServiceClient.getRecipesByTopping(id);
            }

            if (response.getBody() == null || response.getBody().get("data") == null) {
                log.warn("[StockDeduction] Không có recipe cho {}Id={}. Bỏ qua trừ kho.", type, id);
                return List.of();
            }
            return (List<Map<String, Object>>) response.getBody().get("data");
        } catch (Exception e) {
            log.error("[StockDeduction] Lỗi khi lấy recipe từ catalog-service cho {}Id={}: {}",
                    type, id, e.getMessage());
            return List.of();
        }
    }

    private void deductFromBatches(Long shopId, Long ingredientId, Double totalNeeded, Long orderId) {
        RawIngredient ingredient = rawIngredientRepository.findById(ingredientId)
                .orElseThrow(() -> new BusinessException(
                        "Không tìm thấy nguyên liệu id=" + ingredientId +
                                ". Kiểm tra lại recipe trong catalog-service."));

        //lấy tất cả batch còn hàng của shop
        List<IngredientBatch> batches = ingredientBatchRepository.findAllAvailableBatchesForDeduction(
                shopId, ingredientId, InventoryStatus.ACTIVE
        );

        double totalAvailable = batches.stream()
                .mapToDouble(IngredientBatch::getCurrentQuantity).sum();

        if (totalAvailable < totalNeeded) {
            throw new BusinessException(String.format(
                    "Không đủ kho nguyên liệu '%s': cần %.2f, hiện có %.2f",
                    ingredient.getName(), totalNeeded, totalAvailable));
        }

        double remaining = totalNeeded;
        for (IngredientBatch batch : batches) {
            if (remaining <= 0) break;
            //trừ tối đa quantity trong batch
            double deductAmount = Math.min(batch.getCurrentQuantity(), remaining);

            //cập nhật tồn kho trong batch
            batch.setCurrentQuantity(batch.getCurrentQuantity() - deductAmount);
            ingredientBatchRepository.save(batch);

            InventoryTransaction transaction = InventoryTransaction.builder()
                    .shopId(shopId)
                    .ingredient(ingredient)
                    .batch(batch)
                    .orderId(orderId)
                    .quantityChange(-deductAmount)
                    .quantityAfter(batch.getCurrentQuantity())
                    .transactionType(TransactionType.SALE_DEDUCT)
                    .inventoryStatus(InventoryStatus.ACTIVE)
                    .build();
            inventoryTransactionRepository.save(transaction);
            remaining -= deductAmount;
            log.debug("[FIFO] Trừ batchId={} amount={}, còn lại trong batch={}",
                    batch.getId(), deductAmount, batch.getCurrentQuantity());
        }
        log.info("[FIFO] ✅ Hoàn tất trừ {} {} cho orderId={}",
                totalNeeded, ingredient.getName(), orderId);
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    private Double toDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        return Double.parseDouble(value.toString());
    }
}
