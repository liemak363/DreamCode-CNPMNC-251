package com.cnpmnc.DreamCode.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class AssetRequestResponse {
    private Integer id;
    private String type;
    private String status;
    private String reason;
    private Integer priority;
    private String notes;
    
    // Thông tin requester
    private Integer requesterId;
    private String requesterName;
    
    // Thông tin department
    private Integer departmentId;
    private String departmentName;
    
    // Thông tin asset (nếu có)
    private Integer assetId;
    private String assetName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
