package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.AssetRetireLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRetireLogRepository extends JpaRepository<AssetRetireLog, Integer> {
    Page<AssetRetireLog> findByAssetIdOrderByCreatedAtDesc(Integer assetId, Pageable pageable);
}