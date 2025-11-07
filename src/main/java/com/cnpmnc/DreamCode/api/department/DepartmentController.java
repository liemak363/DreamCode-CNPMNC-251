package com.cnpmnc.DreamCode.api.department;

import com.cnpmnc.DreamCode.dto.response.*;
import com.cnpmnc.DreamCode.model.*;
import com.cnpmnc.DreamCode.model.enumType.ApprovalStatus;
import com.cnpmnc.DreamCode.repository.*;
import com.cnpmnc.DreamCode.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final AssetRequestRepository requestRepository;
    private final InventoryCheckRepository inventoryRepository;
    private final DepartmentRepository departmentRepository;
    
    @GetMapping("/health")
    public String health() {
        return "department-ok";
    }
    
    // Helper methods
    private User getUser(Authentication auth) {
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
        return userRepository.findById(ud.getId()).orElseThrow();
    }
    
    private Department getManagedDepartment(User user) {
        return departmentRepository.findByManager(user)
                .orElseThrow(() -> new IllegalArgumentException("You are not managing any department"));
    }
    
    // DEBUG: Kiểm tra thông tin user hiện tại
    @GetMapping("/me")
    public Map<String, Object> getCurrentUserInfo(Authentication auth) {
        User user = getUser(auth);
        Department dept = departmentRepository.findByManager(user).orElse(null);
        
        return Map.of(
            "userId", user.getId(),
            "userName", user.getUserName(),
            "roles", user.getRoles().stream().map(r -> r.getName()).toList(),
            "hasManagedDepartment", dept != null,
            "managedDepartmentId", dept != null ? dept.getId() : null,
            "managedDepartmentName", dept != null ? dept.getName() : null
        );
    }
    
    @GetMapping("/test")
    public Map<String, Object> test(Authentication auth) {
        User user = getUser(auth);
        return Map.of(
            "message", "Test OK",
            "userId", user.getId(),
            "userName", user.getUserName()
        );
    }
    
    // ========== 1. QUẢN LÝ TÀI SẢN PHÒNG BAN ==========
    
    @GetMapping("/assets")
    public ResponseEntity<?> getAssets(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            User user = getUser(auth);
            Department dept = getManagedDepartment(user);
            
            Page<Asset> assets = assetRepository.findByDepartment(dept, PageRequest.of(page, size));
            
            // Convert sang DTO
            Page<AssetResponse> response = assets.map(DepartmentMapper::toAssetSimpleResponse);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }
    
    @GetMapping("/assets/{assetId}")
    public ResponseEntity<?> getAssetDetail(
            Authentication auth,
            @PathVariable Integer assetId) {
        try {
            User user = getUser(auth);
            Department dept = getManagedDepartment(user);
            
            Asset asset = assetRepository.findById(assetId)
                    .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
            
            // Kiểm tra asset có thuộc department không
            if (!asset.getDepartment().getId().equals(dept.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "This asset does not belong to your department"));
            }
            
            return ResponseEntity.ok(DepartmentMapper.toAssetSimpleResponse(asset));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ========== 2. TẠO REQUEST CHO ASSET_MANAGER ==========
    
    @PostMapping("/requests")
    public ResponseEntity<?> createRequest(
            Authentication auth, 
            @RequestBody Map<String, Object> body) {
        try {
            User user = getUser(auth);
            Department dept = getManagedDepartment(user);
            
            String type = (String) body.get("type"); // PROVISION, RECALL, TRANSFER
            if (type == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "type is required"));
            }
            
            AssetRequest req = new AssetRequest();
            req.setType(type);
            req.setStatus(ApprovalStatus.PENDING);
            req.setRequester(user); // DEPT_MANAGER tạo
            req.setDepartment(dept);
            req.setReason((String) body.get("reason"));
            req.setPriority((Integer) body.getOrDefault("priority", 2));
            
            // Nếu có assetId (RECALL/TRANSFER)
            if (body.get("assetId") != null) {
                Integer assetId = (Integer) body.get("assetId");
                Asset asset = assetRepository.findById(assetId).orElse(null);
                if (asset != null && !asset.getDepartment().getId().equals(dept.getId())) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Asset does not belong to your department"));
                }
                req.setAsset(asset);
            }
            
            req = requestRepository.save(req);
            
            return ResponseEntity.ok(DepartmentMapper.toAssetRequestResponse(req));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/requests")
    public ResponseEntity<?> getMyRequests(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            User user = getUser(auth);
            Page<AssetRequest> requests = requestRepository.findByRequesterId(user.getId(), PageRequest.of(page, size));
            Page<AssetRequestResponse> response = requests.map(DepartmentMapper::toAssetRequestResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ========== 3. KIỂM KÊ TÀI SẢN ==========
    
    @GetMapping("/inventory")
    public ResponseEntity<?> getInventory(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            User user = getUser(auth);
            Department dept = getManagedDepartment(user);
            
            Page<InventoryCheck> checks = inventoryRepository.findByDepartment(dept, PageRequest.of(page, size));
            Page<InventoryCheckResponse> response = checks.map(DepartmentMapper::toInventoryCheckResponse);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/inventory/{id}")
    public ResponseEntity<?> updateInventory(
            Authentication auth,
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        try {
            User user = getUser(auth);
            Department dept = getManagedDepartment(user);
            
            InventoryCheck inv = inventoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Inventory check not found"));
            
            // Kiểm tra có phải department của mình không
            if (!inv.getDepartment().getId().equals(dept.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not your department's inventory"));
            }
            
            inv.setStatus(body.get("status")); 
            inv.setNotes(body.get("notes"));
            inv.setCheckedBy(user);
            
            inv = inventoryRepository.save(inv);
            
            return ResponseEntity.ok(DepartmentMapper.toInventoryCheckResponse(inv));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ========== 4. DUYỆT REQUEST TỪ USER ==========
    
    @GetMapping("/user-requests")
    public ResponseEntity<?> getUserRequests(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            User user = getUser(auth);
            Department dept = getManagedDepartment(user);
            
            // Lấy request PENDING từ USER thuộc phòng mình
            Page<AssetRequest> requests = requestRepository.findByDepartmentIdAndStatus(
                    dept.getId(), ApprovalStatus.PENDING, PageRequest.of(page, size));
            
            Page<AssetRequestResponse> response = requests.map(DepartmentMapper::toAssetRequestResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/user-requests/{id}/approve")
    public ResponseEntity<?> approveUserRequest(
            Authentication auth,
            @PathVariable Integer id,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            User user = getUser(auth);
            Department dept = getManagedDepartment(user);
            
            AssetRequest req = requestRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Request not found"));
            
            if (!req.getDepartment().getId().equals(dept.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not your department's request"));
            }
            
            req.setStatus(ApprovalStatus.APPROVED);
            if (body != null) {
                req.setNotes(body.get("notes"));
            }
            
            req = requestRepository.save(req);
            
            // TODO: Có thể tự động forward request cho ASSET_MANAGER ở đây
            
            return ResponseEntity.ok(DepartmentMapper.toAssetRequestResponse(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/user-requests/{id}/reject")
    public ResponseEntity<?> rejectUserRequest(
            Authentication auth,
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        try {
            User user = getUser(auth);
            Department dept = getManagedDepartment(user);
            
            AssetRequest req = requestRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Request not found"));
            
            if (!req.getDepartment().getId().equals(dept.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not your department's request"));
            }
            
            req.setStatus(ApprovalStatus.REJECTED);
            req.setNotes(body.get("notes"));
            
            req = requestRepository.save(req);
            
            return ResponseEntity.ok(DepartmentMapper.toAssetRequestResponse(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}