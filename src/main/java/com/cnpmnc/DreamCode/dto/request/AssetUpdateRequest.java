package com.cnpmnc.DreamCode.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetUpdateRequest {
    private String name;
    private String location;
    private String description;
    private List<String> imageKeys;
    private Date purchaseDate;
    
    @Positive(message = "Value must be positive")
    private Double value;
    
    private Integer departmentId;
    private Integer categoryId;
    private Integer supplierId;
}
