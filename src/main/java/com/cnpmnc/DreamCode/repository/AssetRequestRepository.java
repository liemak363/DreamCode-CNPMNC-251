package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.AssetRequest;
import com.cnpmnc.DreamCode.model.enumType.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRequestRepository extends JpaRepository<AssetRequest, Integer> {
    
    Page<AssetRequest> findByRequesterId(Integer requesterId, Pageable pageable);
    
    Page<AssetRequest> findByRequesterIdAndStatus(
        Integer requesterId, 
        ApprovalStatus status, 
        Pageable pageable
    );
    
    Page<AssetRequest> findByDepartmentIdAndStatus(
        Integer departmentId, 
        ApprovalStatus status, 
        Pageable pageable
    );
}