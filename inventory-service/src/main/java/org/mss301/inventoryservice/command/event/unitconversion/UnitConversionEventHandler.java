package org.mss301.inventoryservice.command.event.unitconversion;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.mss301.inventoryservice.command.data.entity.RawIngredient;
import org.mss301.inventoryservice.command.data.entity.UnitConversion;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;
import org.mss301.inventoryservice.command.data.repository.RawIngredientRepository;
import org.mss301.inventoryservice.command.data.repository.UnitConversionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnitConversionEventHandler {

    private final UnitConversionRepository unitConversionRepository;
    private final RawIngredientRepository rawIngredientRepository;

    @EventHandler
    public void on(UnitConversionCreatedEvent event) {
        UnitConversion entity = new UnitConversion();
        // Copy các trường cơ bản (String, Double, Boolean)
        BeanUtils.copyProperties(event, entity);

        // Map khóa ngoại bằng tay
        RawIngredient ingredientRef = rawIngredientRepository.getReferenceById(event.getIngredientId());
        entity.setIngredient(ingredientRef);

        // Set trạng thái mặc định (vì trong entity cột này nullable = false)
        entity.setInventoryStatus(InventoryStatus.ACTIVE); // Hoặc enum tương ứng của bạn

        unitConversionRepository.save(entity);
    }
}
