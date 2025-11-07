package com.cnpmnc.DreamCode.dto.response;

import com.cnpmnc.DreamCode.model.enumType.AssetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Nested objects
    private DepartmentInfo department;
    private CategoryInfo category;
    
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
