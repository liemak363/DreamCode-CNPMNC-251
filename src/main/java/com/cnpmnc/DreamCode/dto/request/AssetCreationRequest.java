package com.cnpmnc.DreamCode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AssetCreationRequest {
    @NotBlank(message = "Asset name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    private String description;

    private List<String> imageKeys;

    @NotNull(message = "Purchase date is required")
    private Date purchaseDate;

    @NotNull(message = "Value is required")
    @Positive(message = "Value must be positive")
    private Double value;

    @Positive(message = "Useful life months must be positive")
    private Integer usefulLifeMonths;

    @NotNull(message = "Department ID is required")
    private Integer departmentId = 36;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    @NotNull(message = "Supplier ID is required")
    private Integer supplierId;
}
