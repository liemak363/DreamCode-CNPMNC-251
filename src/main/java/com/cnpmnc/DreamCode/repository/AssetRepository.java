package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.Asset;
import com.cnpmnc.DreamCode.model.Department;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
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

    // Tìm kiếm theo tên
    Page<Asset> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Tìm theo department
    Page<Asset> findByDepartmentId(Integer departmentId, Pageable pageable);
    
    // Tìm theo category
    Page<Asset> findByCategoryId(Integer categoryId, Pageable pageable);
    
    // Tìm kiếm phức hợp
    @Query(value = "SELECT * FROM asset a WHERE " +
           "(:name IS NULL OR a.name ILIKE CONCAT('%', CAST(:name AS TEXT), '%')) AND " +
           "(:departmentId IS NULL OR a.department_id = :departmentId) AND " +
           "(:categoryId IS NULL OR a.category_id = :categoryId)",
           countQuery = "SELECT COUNT(*) FROM asset a WHERE " +
           "(:name IS NULL OR a.name ILIKE CONCAT('%', CAST(:name AS TEXT), '%')) AND " +
           "(:departmentId IS NULL OR a.department_id = :departmentId) AND " +
           "(:categoryId IS NULL OR a.category_id = :categoryId)",
           nativeQuery = true)
    Page<Asset> searchAssets(
        @Param("name") String name,
        @Param("departmentId") Integer departmentId,
        @Param("categoryId") Integer categoryId,
        Pageable pageable
    );
}