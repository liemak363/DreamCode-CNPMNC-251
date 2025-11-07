package com.cnpmnc.DreamCode.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class AccidentReportRequest {
    
    @NotNull(message = "Asset ID is required")
    private Integer assetId;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private List<String> imageKeys; // Optional
}
