package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.Asset;
import com.cnpmnc.DreamCode.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.cnpmnc.DreamCode.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByName(String name);

    List<Department> findByManager(User manager);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
           "FROM Department d WHERE d.id = :deptId AND d.manager.id = :managerId")
    boolean existsByIdAndManagerId(@Param("deptId") Integer deptId, 
                                    @Param("managerId") Integer managerId);

    boolean existsByName(String name);

    boolean existsByIdAndIsActiveTrue(Integer id);

    boolean existsByNameAndIsActiveTrue(String name);

    List<Department> findAllByIsActiveTrue();

    Page<Department> findAllByIsActiveTrue(Pageable pageable);
}
