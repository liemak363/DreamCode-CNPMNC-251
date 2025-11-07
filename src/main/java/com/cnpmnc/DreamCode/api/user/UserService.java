package com.cnpmnc.DreamCode.api.user;

import com.cnpmnc.DreamCode.dto.request.AccidentReportRequest;
import com.cnpmnc.DreamCode.dto.response.AccidentLogResponse;
import com.cnpmnc.DreamCode.dto.response.AssetResponse;
import com.cnpmnc.DreamCode.dto.response.UserResponse;
import com.cnpmnc.DreamCode.mapper.UserMapper;
import com.cnpmnc.DreamCode.model.Asset;
import com.cnpmnc.DreamCode.model.AssetAccidentLog;
import com.cnpmnc.DreamCode.model.User;
import com.cnpmnc.DreamCode.model.enumType.AccidentStatus;
import com.cnpmnc.DreamCode.repository.AccidentLogRepository;
import com.cnpmnc.DreamCode.repository.AssetRepository;
import com.cnpmnc.DreamCode.repository.AssetUsageLogRepository;
import com.cnpmnc.DreamCode.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    
    UserRepository userRepository;
    AssetUsageLogRepository usageLogRepository;
    AssetRepository assetRepository;
    AccidentLogRepository accidentLogRepository;
    UserMapper userMapper;

    public UserResponse getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return userMapper.toUserResponse(user);
    }

    /**
     * Lấy danh sách assets đang được user sử dụng (endTime = null)
     */
    public Page<AssetResponse> getMyAssets(Integer userId, int page, int size) {
        Page<Asset> assets = assetRepository.findActiveAssetsByUserId(
            userId,
            LocalDateTime.now(),
            PageRequest.of(page, size, Sort.by("name").ascending())
        );
        
        return assets.map(this::toAssetResponse);
    }

    /**
     * Lấy chi tiết 1 asset nếu user đang sử dụng
     */
    public AssetResponse getMyAssetDetail(Integer userId, Integer assetId) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
        
        boolean isCurrentUser = usageLogRepository.existsActiveUsageByAssetAndUser(
            assetId, userId, LocalDateTime.now()
        );
        
        if (!isCurrentUser) {
            throw new IllegalStateException("You are not currently using this asset");
        }
        
        return toAssetResponse(asset);
    }

    /**
     * Báo cáo sự cố asset
     */
    public AccidentLogResponse createAccidentReport(Integer userId, AccidentReportRequest request) {
        // 1. Validate asset tồn tại
        Asset asset = assetRepository.findById(request.getAssetId())
            .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
        
        // 2. Kiểm tra user có đang sử dụng asset không
        boolean isCurrentUser = usageLogRepository.existsActiveUsageByAssetAndUser(
            request.getAssetId(), 
            userId, 
            LocalDateTime.now()
        );
        
        if (!isCurrentUser) {
            throw new IllegalStateException("You are not currently using this asset");
        }
        
        // 3. Lấy thông tin user để snapshot
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // 4. Tạo accident log với snapshot data
        AssetAccidentLog log = new AssetAccidentLog();
        log.setAsset(asset);
        log.setTitle(request.getTitle());
        log.setDescription(request.getDescription());
        log.setImageKeys(request.getImageKeys());
        log.setStatus(AccidentStatus.PENDING);
        
        // Snapshot reporter info (không dùng foreign key để tránh bị xóa khi user/dept bị xóa)
        log.setReporterUserName(user.getUserName());
        if (user.getDepartment() != null) {
            log.setReporterDepartmentName(user.getDepartment().getName());
        }
        
        log = accidentLogRepository.save(log);
        
        return toAccidentLogResponse(log);
    }

    /**
     * Lấy danh sách accident logs của user
     */
    public Page<AccidentLogResponse> getMyAccidentLogs(Integer userId, int page, int size) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Page<AssetAccidentLog> logs = accidentLogRepository.findByReporterUserName(
            user.getUserName(),
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
        
        return logs.map(this::toAccidentLogResponse);
    }

    /**
     * Lấy chi tiết 1 accident log nếu là người tạo
     */
    public AccidentLogResponse getAccidentDetail(Integer userId, Integer accidentId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        AssetAccidentLog log = accidentLogRepository.findById(accidentId)
            .orElseThrow(() -> new IllegalArgumentException("Accident log not found"));
        
        // Kiểm tra xem user có phải là người tạo report không
        if (!log.getReporterUserName().equals(user.getUserName())) {
            throw new IllegalArgumentException("You are not authorized to view this accident report");
        }
        
        return toAccidentLogResponse(log);
    }

    // ========== HELPER METHODS ==========
    
    private AssetResponse toAssetResponse(Asset asset) {
        return AssetResponse.builder()
            .id(asset.getId())
            .name(asset.getName())
            .location(asset.getLocation())
            .description(asset.getDescription())
            .purchaseDate(asset.getPurchaseDate())
            .value(asset.getValue())
            .status(asset.getStatus())
            .categoryId(asset.getCategory() != null ? asset.getCategory().getId() : null)
            .categoryName(asset.getCategory() != null ? asset.getCategory().getName() : null)
            .supplierId(asset.getSupplier() != null ? asset.getSupplier().getId() : null)
            .supplierName(asset.getSupplier() != null ? asset.getSupplier().getName() : null)
            .departmentId(asset.getDepartment() != null ? asset.getDepartment().getId() : null)
            .departmentName(asset.getDepartment() != null ? asset.getDepartment().getName() : null)
            .createdAt(asset.getCreatedAt())
            .updatedAt(asset.getUpdatedAt())
            .build();
    }
    
    private AccidentLogResponse toAccidentLogResponse(AssetAccidentLog log) {
        return AccidentLogResponse.builder()
            .id(log.getId())
            .title(log.getTitle())
            .description(log.getDescription())
            .imageKeys(log.getImageKeys())
            .status(log.getStatus())
            .resolutionNotes(log.getResolutionNotes())
            .createdAt(log.getCreatedAt())
            .updatedAt(log.getUpdatedAt())
            .assetId(log.getAsset() != null ? log.getAsset().getId() : null)
            .assetName(log.getAsset() != null ? log.getAsset().getName() : null)
            .assetLocation(log.getAsset() != null ? log.getAsset().getLocation() : null)
            .reporterUserId(null) // Không có foreign key
            .reporterUserName(log.getReporterUserName())
            .reporterDepartmentName(log.getReporterDepartmentName())
            .build();
    }
}