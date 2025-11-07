package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.cnpmnc.DreamCode.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByName(String name);

    Optional<Department> findByManager(User manager);

    boolean existsByName(String name);

    boolean existsByIdAndIsActiveTrue(Integer id);

    boolean existsByNameAndIsActiveTrue(String name);

    List<Department> findAllByIsActiveTrue();

    Page<Department> findAllByIsActiveTrue(Pageable pageable);
}
