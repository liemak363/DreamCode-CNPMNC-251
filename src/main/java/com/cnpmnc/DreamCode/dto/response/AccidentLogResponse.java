package com.cnpmnc.DreamCode.dto.response;

import com.cnpmnc.DreamCode.model.enumType.AccidentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AccidentLogResponse {
    private Integer id;
    private String title;
    private String description;
    private List<String> imageKeys;
    private AccidentStatus status;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Asset info
    private Integer assetId;
    private String assetName;
    private String assetLocation;
    
    private Integer reporterUserId;
    private String reporterUserName;
    private String reporterDepartmentName;
}
