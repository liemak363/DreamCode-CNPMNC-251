package com.cnpmnc.DreamCode.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class InventoryCheckResponse {
    private Integer id;
    private String campaignName;
    private String status; // PENDING, OK, MISSING, DAMAGED
    private String notes;
    
    // Thông tin asset
    private Integer assetId;
    private String assetName;
    private String assetLocation;
    
    // Thông tin department
    private Integer departmentId;
    private String departmentName;
    
    // Thông tin checker
    private Integer checkedById;
    private String checkedByName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}