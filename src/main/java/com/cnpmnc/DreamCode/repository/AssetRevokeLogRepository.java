package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.AssetRevokeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRevokeLogRepository extends JpaRepository<AssetRevokeLog, Integer> {
    Page<AssetRevokeLog> findByAssetIdOrderByCreatedAtDesc(Integer assetId, Pageable pageable);
}