package com.cnpmnc.DreamCode.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryCheck extends BaseEntity {
    
    private String campaignName; 
    
    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    private String status; // "PENDING", "OK", "MISSING", "DAMAGED"
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne
    @JoinColumn(name = "checked_by_id")
    private User checkedBy;
}