package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.AssetAccidentLog;
import com.cnpmnc.DreamCode.model.enumType.AccidentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccidentLogRepository extends JpaRepository<AssetAccidentLog, Integer> {
    
    Page<AssetAccidentLog> findByReporterUserName(String reporterUserName, Pageable pageable);
    
    Page<AssetAccidentLog> findByStatus(AccidentStatus status, Pageable pageable);

    Page<AssetAccidentLog> findByAssetId(Integer assetId, Pageable pageable);
    
    Page<AssetAccidentLog> findByReporterDepartmentName(String departmentName, Pageable pageable);
}