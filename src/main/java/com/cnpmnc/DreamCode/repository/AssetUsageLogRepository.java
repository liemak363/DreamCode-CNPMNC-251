package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.AssetUsageLog;
import com.cnpmnc.DreamCode.model.enumType.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetUsageLogRepository extends JpaRepository<AssetUsageLog, Integer> {
    
    // Lấy lịch sử theo asset
    Page<AssetUsageLog> findByAssetIdOrderByCreatedAtDesc(Integer assetId, Pageable pageable);
    
    // Lấy lịch sử theo user
    @Query("SELECT aul FROM AssetUsageLog aul JOIN aul.users u WHERE u.id = :userId ORDER BY aul.createdAt DESC")
    Page<AssetUsageLog> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);
    
    // Lấy các assignment đang active (chưa thu hồi)
    @Query("SELECT aul FROM AssetUsageLog aul WHERE aul.asset.id = :assetId AND aul.endTime IS NULL")
    List<AssetUsageLog> findActiveAssignmentsByAssetId(@Param("assetId") Integer assetId);
    
    // Lấy các assignment đang chờ duyệt
    Page<AssetUsageLog> findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus status, Pageable pageable);
}
