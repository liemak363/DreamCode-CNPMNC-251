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
}