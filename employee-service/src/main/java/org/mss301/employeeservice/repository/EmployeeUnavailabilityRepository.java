package org.mss301.employeeservice.repository;

import org.mss301.employeeservice.entity.EmployeeUnavailability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeUnavailabilityRepository extends JpaRepository<EmployeeUnavailability, Long>, JpaSpecificationExecutor<EmployeeUnavailability> {
    Optional<EmployeeUnavailability> findByEmployeeUnavailabilityIdAndShopId(Long id, Long shopId);
    Page<EmployeeUnavailability> findAllByShopId(Long shopId, Pageable pageable);
}
