package org.mss301.employeeservice.repository;

import org.mss301.employeeservice.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>,
        JpaSpecificationExecutor<Schedule> {
    Optional<Schedule> findByScheduleIdAndShopId(Long id, Long shopId);
    List<Schedule> findAllByEmployeeEmployeeIdAndShopId(Long employeeId, Long shopId);
}
