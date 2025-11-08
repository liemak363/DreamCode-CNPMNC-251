package com.cnpmnc.DreamCode.api.assets;

import com.cnpmnc.DreamCode.dto.request.*;
import com.cnpmnc.DreamCode.dto.response.*;
import com.cnpmnc.DreamCode.mapper.AssignAssetMapper;
import com.cnpmnc.DreamCode.mapper.RetireAssetMapper;
import com.cnpmnc.DreamCode.mapper.RevokeAssetMapper;
import com.cnpmnc.DreamCode.model.*;
import com.cnpmnc.DreamCode.model.enumType.AssetStatus;
import com.cnpmnc.DreamCode.repository.*;
import com.cnpmnc.DreamCode.security.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    RevokeAssetMapper revokeAssetMapper;
    RetireAssetMapper retireAssetMapper;
    AssetRepository assetRepository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    DepartmentRepository departmentRepository;
    SupplierRepository supplierRepository;
    AssetRevokeLogRepository assetRevokeLogRepository;
    AssetRetireLogRepository assetRetiredLogRepository;

    // ========== TỪ NHÁNH MAIN: Assign & Revoke ==========

    @Transactional
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

    @Transactional
    public RevokeAssetResponse revokeAsset(RevokeAssetRequest request) {
        // Find the active asset usage log for the given asset ID
        AssetUsageLog assetUsageLog = assetUsageLogRepository.findByAssetIdAndEndTimeIsNull(request.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("No active assignment found for asset ID " + request.getAssetId()));


        // Update the asset status to IN_STOCK
        Asset asset = assetUsageLog.getAsset();
        asset.setStatus(AssetStatus.IN_STOCK);


        AssetRevokeLog assetRevokeLog = revokeAssetMapper.toAssetRevokeLog(request);
        Integer revokedById = getCurrentUserId();
        User revokedBy = userRepository.findById(revokedById)
                .orElseThrow(() -> new IllegalArgumentException("Revoker not found."));
        assetRevokeLog.setRevokedBy(revokedBy);
        assetRevokeLog.setAsset(asset);
        // Set the current time as beginTime
        if (request.getRevokedTime() == null) {
            assetRevokeLog.setRevokedTime(LocalDateTime.now());
        }
        // Set the end time to the current time
        assetUsageLog.setEndTime(assetRevokeLog.getRevokedTime());
        // Save the updated asset usage log
        assetUsageLog = assetUsageLogRepository.save(assetUsageLog);
        assetRevokeLog = assetRevokeLogRepository.save(assetRevokeLog);
        // Map to RevokeAssetResponse
        RevokeAssetResponse response = revokeAssetMapper.toRevokeAssetResponse(assetRevokeLog);
        response.setAssetId(asset.getId());
        response.setRevokedById(revokedById);
        return response;
    }

    @Transactional
    public RetireAssetResponse retireAsset(RetireAssetRequest request) {
        // Check if asset exists
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("Asset with ID " + request.getAssetId() + " does not exist."));

        // Check if asset status is IN_STOCK
        if (!AssetStatus.IN_STOCK.equals(asset.getStatus())) {
            throw new IllegalArgumentException("Asset with ID " + request.getAssetId() + " is not in stock and cannot be retired.");
        }

        // Create and set asset retired log
        AssetRetireLog retiredLog = retireAssetMapper.toAssetRetireLog(request);
        retiredLog.setAsset(asset);
        retiredLog.setRetiredTime(request.getRetiredTime() != null ? request.getRetiredTime() : LocalDateTime.now());

        // Retrieve user ID from token
        Integer retiredById = getCurrentUserId();
        User retiredBy = userRepository.findById(retiredById)
                .orElseThrow(() -> new IllegalArgumentException("Retirer not found."));
        retiredLog.setRetiredBy(retiredBy);

        // Save the retired log
        assetRetiredLogRepository.save(retiredLog);

        // Update asset status to RETIRED
        asset.setStatus(AssetStatus.RETIRED);
        assetRepository.save(asset);
        RetireAssetResponse response = retireAssetMapper.toRetireAssetResponse(retiredLog);
        response.setAssetId(asset.getId());
        response.setRetiredById(retiredById);
        response.setRetiredTime(retiredLog.getRetiredTime());
        return response;
    }

    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    // ========== TỪ NHÁNH THANG: CRUD tài sản ==========

    // 1. Tạo tài sản mới
    @Transactional
    public AssetResponse createAsset(AssetCreationRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.getCategoryId()));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + request.getDepartmentId()));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + request.getSupplierId()));

        Asset asset = new Asset();
        asset.setName(request.getName());
        asset.setLocation(request.getLocation());
        asset.setDescription(request.getDescription());
        asset.setImageKeys(request.getImageKeys());
        asset.setPurchaseDate(request.getPurchaseDate());
        asset.setValue(request.getValue());
        asset.setStatus(AssetStatus.IN_STOCK);
        asset.setCategory(category);
        asset.setDepartment(department);
        asset.setSupplier(supplier);

        Asset saved = assetRepository.save(asset);
        return toAssetResponse(saved);
    }

    // 2. Danh sách tra cứu tài sản (với tìm kiếm)
    public Page<AssetResponse> searchAssets(String name, Integer departmentId, Integer categoryId,
                                            int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Asset> assets = assetRepository.searchAssets(name, departmentId, categoryId, pageable);
        return assets.map(this::toAssetResponse);
    }

    // 3. Lấy chi tiết tài sản
    public AssetResponse getAsset(Integer id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + id));
        return toAssetResponse(asset);
    }

    // 4. Cập nhật tài sản
    @Transactional
    public AssetResponse updateAsset(Integer id, AssetUpdateRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + id));

        if (StringUtils.hasText(request.getName())) {
            asset.setName(request.getName());
        }
        if (StringUtils.hasText(request.getLocation())) {
            asset.setLocation(request.getLocation());
        }
        if (request.getDescription() != null) {
            asset.setDescription(request.getDescription());
        }
        if (request.getImageKeys() != null) {
            asset.setImageKeys(request.getImageKeys());
        }
        if (request.getPurchaseDate() != null) {
            asset.setPurchaseDate(request.getPurchaseDate());
        }
        if (request.getValue() != null) {
            asset.setValue(request.getValue());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            asset.setCategory(category);
        }
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));
            asset.setDepartment(department);
        }
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
            asset.setSupplier(supplier);
        }

        Asset saved = assetRepository.save(asset);
        return toAssetResponse(saved);
    }

    // 5. Xóa tài sản
    @Transactional
    public void deleteAsset(Integer id) {
        if (!assetRepository.existsById(id)) {
            throw new IllegalArgumentException("Asset not found: " + id);
        }
        assetRepository.deleteById(id);
    }

    // 6. Lấy lịch sử sử dụng tài sản
    public Page<AssetUsageLogResponse> getAssetUsageLogs(Integer assetId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AssetUsageLog> logs = assetUsageLogRepository.findByAssetIdOrderByCreatedAtDesc(assetId, pageable);
        return logs.map(this::toAssetUsageLogResponse);
    }

    // Helper method: Convert Asset to AssetResponse
    private AssetResponse toAssetResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .name(asset.getName())
                .location(asset.getLocation())
                .description(asset.getDescription())
                .status(asset.getStatus())
                .imageKeys(asset.getImageKeys())
                .purchaseDate(asset.getPurchaseDate())
                .value(asset.getValue())
                .categoryId(asset.getCategory().getId())
                .categoryName(asset.getCategory().getName())
                .category(AssetResponse.CategoryInfo.builder()
                        .id(asset.getCategory().getId())
                        .name(asset.getCategory().getName())
                        .build())
                .departmentId(asset.getDepartment().getId())
                .departmentName(asset.getDepartment().getName())
                .department(AssetResponse.DepartmentInfo.builder()
                        .id(asset.getDepartment().getId())
                        .name(asset.getDepartment().getName())
                        .build())
                .supplierId(asset.getSupplier().getId())
                .supplierName(asset.getSupplier().getName())
                .build();
    }

    // Helper method: Convert AssetUsageLog to AssetUsageLogResponse
    private AssetUsageLogResponse toAssetUsageLogResponse(AssetUsageLog log) {
        return AssetUsageLogResponse.builder()
                .id(log.getId())
                .assetId(log.getAsset().getId())
                .assetName(log.getAsset().getName())
                .beginTime(log.getBeginTime())
                .endTime(log.getEndTime())
                .approvalStatus(log.getApprovalStatus() != null ? log.getApprovalStatus().name() : null)
                .notes(log.getNotes())
                .users(log.getUsers().stream()
                        .map(user -> AssetUsageLogResponse.UserInfo.builder()
                                .id(user.getId())
                                .userName(user.getUserName())
                                .build())
                        .collect(Collectors.toList()))
                .approvedBy(log.getApprovedBy() != null ?
                        AssetUsageLogResponse.UserInfo.builder()
                                .id(log.getApprovedBy().getId())
                                .userName(log.getApprovedBy().getUserName())
                                .build() : null)
                .build();
    }

}

