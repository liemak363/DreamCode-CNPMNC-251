package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.Department;
import com.cnpmnc.DreamCode.model.InventoryCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryCheckRepository extends JpaRepository<InventoryCheck, Integer> {
    Page<InventoryCheck> findByDepartment(Department department, Pageable pageable);
}