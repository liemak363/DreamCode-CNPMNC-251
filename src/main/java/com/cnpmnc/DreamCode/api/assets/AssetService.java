package com.cnpmnc.DreamCode.api.assets;

import com.cnpmnc.DreamCode.constant.AssetStatus;
import com.cnpmnc.DreamCode.dto.request.AssignAssetRequest;
import com.cnpmnc.DreamCode.dto.request.RevokeAssetRequest;
import com.cnpmnc.DreamCode.dto.response.AssignAssetResponse;
import com.cnpmnc.DreamCode.dto.response.RevokeAssetResponse;
import com.cnpmnc.DreamCode.mapper.AssignAssetMapper;
import com.cnpmnc.DreamCode.model.Asset;
import com.cnpmnc.DreamCode.model.AssetUsageLog;
import com.cnpmnc.DreamCode.model.User;
import com.cnpmnc.DreamCode.repository.AssetRepository;
import com.cnpmnc.DreamCode.repository.AssetUsageLogRepository;
import com.cnpmnc.DreamCode.repository.UserRepository;
import com.cnpmnc.DreamCode.security.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AssetService {
    AssetUsageLogRepository assetUsageLogRepository;
    AssignAssetMapper assignAssetMapper;
    AssetRepository assetRepository;
    UserRepository userRepository;


    public AssignAssetResponse assignAsset(AssignAssetRequest request) {

        AssetUsageLog assetUsageLog = assignAssetMapper.toAssetUsageLog(request);

        // Check if asset exists
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("Asset with ID " + request.getAssetId() + " does not exist."));
        assetUsageLog.setAsset(asset);

        // Check if asset status is IN_STOCK
        if (!asset.getStatus().equals(AssetStatus.IN_STOCK)) {
            throw new IllegalArgumentException("Asset with ID " + request.getAssetId() + " is not available for assignment.");
        }

        // Set asset status to IN_USE
        asset.setStatus(AssetStatus.IN_USE);

        // Check if all user IDs exist
        List<User> users = userRepository.findAllById(request.getUserIds());
        if (users.size() != request.getUserIds().size()) {
            throw new IllegalArgumentException("Some user IDs do not exist.");
        }
        assetUsageLog.setUsers(users);

        // Retrieve user ID from access token
        Integer approvedById = getCurrentUserId();
        User approvedBy = userRepository.findById(approvedById)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found."));
        assetUsageLog.setApprovedBy(approvedBy);

        // Set the current time as beginTime
        if (request.getBeginTime() == null) {
            assetUsageLog.setBeginTime(LocalDateTime.now());
        }

        // Save the asset usage log
        assetUsageLog = assetUsageLogRepository.save(assetUsageLog);

        // Map asset ID and user IDs to the response
        AssignAssetResponse response = assignAssetMapper.toAssignAssetResponse(assetUsageLog);
        response.setAssetId(asset.getId());
        response.setUserIds(users.stream().map(User::getId).collect(Collectors.toSet()));
        // Map beginTime to the response
        response.setBeginTime(assetUsageLog.getBeginTime());

        return response;
    }

    public RevokeAssetResponse revokeAsset(RevokeAssetRequest request) {
        // Find the active asset usage log for the given asset ID
        AssetUsageLog assetUsageLog = assetUsageLogRepository.findByAssetIdAndEndTimeIsNull(request.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("No active assignment found for asset ID " + request.getAssetId()));

        // Set the end time to the current time
        assetUsageLog.setEndTime(LocalDateTime.now());

        // Update the asset status to IN_STOCK
        Asset asset = assetUsageLog.getAsset();
        asset.setStatus(AssetStatus.IN_STOCK);

        // Save the updated asset usage log
        assetUsageLog = assetUsageLogRepository.save(assetUsageLog);

        // Map to RevokeAssetResponse
        return RevokeAssetResponse.builder()
                .id(assetUsageLog.getId())
                .assetId(asset.getId())
                .endTime(assetUsageLog.getEndTime())
                .Status(asset.getStatus())
                .build();
    }

    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }


}
