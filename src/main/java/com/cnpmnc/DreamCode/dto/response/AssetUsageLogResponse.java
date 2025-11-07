package com.cnpmnc.DreamCode.dto.response;

import com.cnpmnc.DreamCode.model.enumType.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetUsageLogResponse {
    private Integer id;
    private Integer assetId;
    private String assetName;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String notes;
    private String approvalStatus;
    private String approvalNotes;
    private LocalDateTime createdAt;
    
    // Nested objects
    private AssetInfo asset;
    private List<UserInfo> users;
    private UserInfo approvedBy;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssetInfo {
        private Integer id;
        private String name;
        private String location;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Integer id;
        private String userName;
    }
}
