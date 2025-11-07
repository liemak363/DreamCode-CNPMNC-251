package com.cnpmnc.DreamCode.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class AssetResponse {
    private Integer id;
    private String name;
    private String location;
    private String description;
    private Date purchaseDate;
    private Double value;
    
    // Thông tin category
    private Integer categoryId;
    private String categoryName;
    
    // Thông tin supplier
    private Integer supplierId;
    private String supplierName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}