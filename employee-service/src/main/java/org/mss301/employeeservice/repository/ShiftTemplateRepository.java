package org.mss301.employeeservice.repository;

import org.mss301.employeeservice.entity.ShiftTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftTemplateRepository extends JpaRepository<ShiftTemplate, Long>,
        JpaSpecificationExecutor<ShiftTemplate> {
    boolean existsByShopIdAndName(Long shopId, String name);
}
