package com.cnpmnc.DreamCode.api.assets;

import com.cnpmnc.DreamCode.dto.request.*;
import com.cnpmnc.DreamCode.dto.response.AssetResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
public class AssetsController {
    
    private final AssetService assetService;

    public AssetsController(AssetService assetService) {
        this.assetService = assetService;
    }

    // Health check
    @GetMapping("/health")
    public String health() {
        return "assets-ok";
    }

    // 1. Tạo tài sản mới
    @PostMapping
    public ResponseEntity<?> createAsset(@Valid @RequestBody AssetCreationRequest request) {
        try {
            AssetResponse created = assetService.createAsset(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // 2. Danh sách tra cứu tài sản (có filter)
    @GetMapping
    public Page<AssetResponse> searchAssets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return assetService.searchAssets(name, departmentId, categoryId, page, size);
    }

    // 3. Lấy chi tiết tài sản
    @GetMapping("/{id}")
    public ResponseEntity<?> getAsset(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(assetService.getAsset(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // 4. Cập nhật tài sản
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAsset(@PathVariable Integer id, 
                                        @Valid @RequestBody AssetUpdateRequest request) {
        try {
            return ResponseEntity.ok(assetService.updateAsset(id, request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // 5. Xóa tài sản
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Integer id) {
        try {
            assetService.deleteAsset(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // 9. Lịch sử tài sản
    @GetMapping("/{id}/history")
    public ResponseEntity<?> getAssetHistory(@PathVariable Integer id,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(assetService.getAssetHistory(id, page, size));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // 10. Lịch sử user sử dụng tài sản
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<?> getUserAssetHistory(@PathVariable Integer userId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(assetService.getUserAssetHistory(userId, page, size));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
