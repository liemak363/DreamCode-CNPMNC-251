package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.AssetUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;



import java.util.Optional;

@Repository
public interface AssetUsageLogRepository extends JpaRepository<AssetUsageLog, Integer> {
    
    @Query("""
        SELECT aul FROM AssetUsageLog aul
        JOIN aul.users u
        WHERE u.id = :userId
        ORDER BY aul.beginTime DESC
        """)
    Page<AssetUsageLog> findByUserIdOrderByBeginTimeDesc(
        @Param("userId") Integer userId,
        Pageable pageable
    );
    
    @Query("""
        SELECT CASE WHEN COUNT(aul) > 0 THEN true ELSE false END
        FROM AssetUsageLog aul
        JOIN aul.users u
        WHERE aul.asset.id = :assetId
        AND u.id = :userId
        AND aul.beginTime <= :timestamp
        AND (aul.endTime IS NULL OR aul.endTime >= :timestamp)
        """)
    boolean existsActiveUsageByAssetAndUser(
        @Param("assetId") Integer assetId,
        @Param("userId") Integer userId,
        @Param("timestamp") LocalDateTime timestamp
    );
    Optional<AssetUsageLog> findByAssetIdAndEndTimeIsNull(Integer assetId);
}
