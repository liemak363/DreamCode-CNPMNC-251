package com.cnpmnc.DreamCode.model;

import com.cnpmnc.DreamCode.model.enumType.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class AssetRequest extends BaseEntity {
    
    @Column(nullable = false)
    private String type; // "PROVISION", "RECALL", "TRANSFER"
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status;
    
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset; // null nếu là PROVISION
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    private Integer priority; // 1, 2, 3
    
    @Column(columnDefinition = "TEXT")
    private String notes;
}