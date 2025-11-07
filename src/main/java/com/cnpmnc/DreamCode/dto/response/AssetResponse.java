package com.cnpmnc.DreamCode.dto.response;

import com.cnpmnc.DreamCode.model.enumType.AssetStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponse {
    private Integer id;
    private String name;
    private String location;
    private String description;
    private AssetStatus status;
    private List<String> imageKeys;
    private Date purchaseDate;
    private Double value;
    
    // Thông tin category
    private Integer categoryId;
    private String categoryName;
    private CategoryInfo category;
    
    // Thông tin supplier
    private Integer supplierId;
    private String supplierName;
    
    // Thông tin department
    private Integer departmentId;
    private String departmentName;
    private DepartmentInfo department;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentInfo {
        private Integer id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Integer id;
        private String name;
    }
}