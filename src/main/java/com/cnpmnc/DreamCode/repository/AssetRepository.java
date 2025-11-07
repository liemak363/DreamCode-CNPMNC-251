package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.Asset;
import com.cnpmnc.DreamCode.model.Department;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssetRepository extends JpaRepository<Asset, Integer> {
    Page<Asset> findByDepartment(Department department, Pageable pageable);

    @Query("""
        SELECT DISTINCT a FROM Asset a
        JOIN a.usageLogs aul
        JOIN aul.users u
        WHERE u.id = :userId
        AND aul.beginTime <= :now
        AND (aul.endTime IS NULL OR aul.endTime >= :now)
        """)
    Page<Asset> findActiveAssetsByUserId(
        @Param("userId") Integer userId,
        @Param("now") LocalDateTime now,
        Pageable pageable
    );

    // Từ nhánh THANG: Tìm kiếm tài sản
    @Query("""
        SELECT a FROM Asset a 
        WHERE (:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:departmentId IS NULL OR a.department.id = :departmentId)
        AND (:categoryId IS NULL OR a.category.id = :categoryId)
        """)
    Page<Asset> searchAssets(
        @Param("name") String name,
        @Param("departmentId") Integer departmentId,
        @Param("categoryId") Integer categoryId,
        Pageable pageable
    );
}