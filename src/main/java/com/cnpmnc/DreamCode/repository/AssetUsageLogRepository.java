package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.AssetUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetUsageLogRepository extends JpaRepository<AssetUsageLog, Integer> {
    Optional<AssetUsageLog> findByAssetIdAndEndTimeIsNull(Integer assetId);
}
