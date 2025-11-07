package com.cnpmnc.DreamCode.api.assets;

import com.cnpmnc.DreamCode.dto.request.*;
import com.cnpmnc.DreamCode.dto.response.AssetResponse;
import com.cnpmnc.DreamCode.dto.response.AssetUsageLogResponse;
import com.cnpmnc.DreamCode.model.*;
import com.cnpmnc.DreamCode.model.enumType.AssetStatus;
import com.cnpmnc.DreamCode.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
public class AssetService {
    private final AssetRepository assetRepository;
    private final AssetUsageLogRepository usageLogRepository;
    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;

    public AssetService(AssetRepository assetRepository,
                       AssetUsageLogRepository usageLogRepository,
                       CategoryRepository categoryRepository,
                       DepartmentRepository departmentRepository,
                       UserRepository userRepository,
                       SupplierRepository supplierRepository) {
        this.assetRepository = assetRepository;
        this.usageLogRepository = usageLogRepository;
        this.categoryRepository = categoryRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
    }

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
        asset.setStatus(AssetStatus.IN_STOCK); // Mặc định là trong kho
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
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.getCategoryId()));
            asset.setCategory(category);
        }
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found: " + request.getDepartmentId()));
            asset.setDepartment(department);
        }

        Asset updated = assetRepository.save(asset);
        return toAssetResponse(updated);
    }

    // 5. Xóa tài sản
    @Transactional
    public void deleteAsset(Integer id) {
        if (!assetRepository.existsById(id)) {
            throw new IllegalArgumentException("Asset not found: " + id);
        }
        assetRepository.deleteById(id);
    }

    // 9. Lịch sử tài sản
    public Page<AssetUsageLogResponse> getAssetHistory(Integer assetId, int page, int size) {
        if (!assetRepository.existsById(assetId)) {
            throw new IllegalArgumentException("Asset not found: " + assetId);
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AssetUsageLog> logs = usageLogRepository.findByAssetIdOrderByCreatedAtDesc(assetId, pageable);
        return logs.map(this::toUsageLogResponse);
    }

    // 10. Lịch sử user sử dụng tài sản
    public Page<AssetUsageLogResponse> getUserAssetHistory(Integer userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AssetUsageLog> logs = usageLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return logs.map(this::toUsageLogResponse);
    }

    // Mapper methods
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
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .department(AssetResponse.DepartmentInfo.builder()
                        .id(asset.getDepartment().getId())
                        .name(asset.getDepartment().getName())
                        .build())
                .category(AssetResponse.CategoryInfo.builder()
                        .id(asset.getCategory().getId())
                        .name(asset.getCategory().getName())
                        .build())
                .build();
    }

    private AssetUsageLogResponse toUsageLogResponse(AssetUsageLog log) {
        return AssetUsageLogResponse.builder()
                .id(log.getId())
                .beginTime(log.getBeginTime())
                .endTime(log.getEndTime())
                .notes(log.getNotes())
                .approvalStatus(log.getApprovalStatus())
                .approvalNotes(log.getApprovalNotes())
                .createdAt(log.getCreatedAt())
                .asset(AssetUsageLogResponse.AssetInfo.builder()
                        .id(log.getAsset().getId())
                        .name(log.getAsset().getName())
                        .location(log.getAsset().getLocation())
                        .build())
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
