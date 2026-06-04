package org.mss301.employeeservice.repository;

import org.mss301.employeeservice.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByShopIdAndUserId(Long shopId, Long userId);
}
