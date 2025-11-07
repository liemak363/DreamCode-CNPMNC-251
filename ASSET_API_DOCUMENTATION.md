# 📦 Asset Management API Documentation

## 📚 Mục lục
- [Thông tin chung](#thông-tin-chung)
- [API Endpoints](#api-endpoints)
  - [CRUD Operations](#1-crud-operations)
  - [Asset Operations](#2-asset-operations)
  - [History & Reports](#3-history--reports)
- [Business Rules](#business-rules)
- [Validation Rules](#validation-rules)
- [Workflows](#workflows)
- [Testing Guide](#testing-guide)

---

## Thông tin chung

### 🔧 Base URL
```
http://localhost:8080
```

### 🔐 Authentication
- **Type**: Bearer Token (JWT)
- **Header**: `Authorization: Bearer <your_jwt_token>`
- **Get Token**: `POST /auth/login`

### 📊 Response Format
- **Success**: JSON object hoặc Page object (với pagination)
- **Error**: JSON với message string

---

## API Endpoints

## 1. CRUD Operations

### 1.1 Health Check
Kiểm tra API hoạt động bình thường.

**Endpoint:** `GET /api/assets/health`

**Authorization:** None

**Response:**
```
assets-ok
```

---

### 1.2 Get All Assets (Search & Filter)
Lấy danh sách tài sản với khả năng tìm kiếm, lọc và phân trang.

**Endpoint:** `GET /api/assets`

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `name` | String | No | - | Tìm kiếm theo tên (case-insensitive, partial match) |
| `departmentId` | Integer | No | - | Lọc theo phòng ban |
| `categoryId` | Integer | No | - | Lọc theo danh mục |
| `page` | Integer | No | 0 | Số trang (bắt đầu từ 0) |
| `size` | Integer | No | 10 | Số items mỗi trang |

**Example Requests:**
```http
# Lấy tất cả
GET /api/assets?page=0&size=10

# Tìm theo tên
GET /api/assets?name=laptop

# Lọc theo department
GET /api/assets?departmentId=8

# Lọc theo category
GET /api/assets?categoryId=1

# Kết hợp nhiều filter
GET /api/assets?name=msi&departmentId=9&categoryId=4&page=0&size=5
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 2,
      "name": "MSI laptop",
      "location": "Financial department",
      "description": "Sample asset 2",
      "imageKeys": ["image02"],
      "purchaseDate": "2025-11-06T17:21:37.386+00:00",
      "value": 120.0,
      "createdAt": "2025-11-07T00:21:37.386",
      "updatedAt": null,
      "department": {
        "id": 9,
        "name": "Financial Department"
      },
      "category": {
        "id": 4,
        "name": "Laptop Gaming MSI"
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 2,
  "empty": false
}
```

---

### 1.3 Get Asset by ID
Lấy thông tin chi tiết một tài sản.

**Endpoint:** `GET /api/assets/{id}`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Integer | ID của tài sản |

**Example:**
```http
GET /api/assets/1
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Asus laptop",
  "location": "IT department",
  "description": "Sample asset",
  "imageKeys": ["image01"],
  "purchaseDate": "2025-11-06T17:21:37.386+00:00",
  "value": 100.0,
  "createdAt": "2025-11-07T00:21:37.386",
  "updatedAt": null,
  "department": {
    "id": 8,
    "name": "Technical Department"
  },
  "category": {
    "id": 1,
    "name": "Laptop"
  }
}
```

**Error Response:** `404 Not Found`
```json
"Asset not found with id: 999"
```

---

### 1.4 Create Asset
Tạo tài sản mới.

**Endpoint:** `POST /api/assets`

**Request Body:**
```json
{
  "name": "Dell XPS 15",
  "location": "Office Room 301",
  "description": "High-performance laptop for development",
  "imageKeys": ["dell-xps-01.jpg", "dell-xps-02.jpg"],
  "purchaseDate": "2025-11-07T00:00:00.000Z",
  "value": 2500.00,
  "departmentId": 8,
  "categoryId": 1,
  "supplierId": 1
}
```

**Validation:**
- `name`: Required, not blank
- `location`: Required
- `purchaseDate`: Required
- `value`: Required, must be > 0
- `departmentId`: Required, must exist
- `categoryId`: Required, must exist
- `supplierId`: Required, must exist

**Response:** `201 Created`
```json
{
  "id": 3,
  "name": "Dell XPS 15",
  "location": "Office Room 301",
  "description": "High-performance laptop for development",
  "imageKeys": ["dell-xps-01.jpg", "dell-xps-02.jpg"],
  "purchaseDate": "2025-11-07T00:00:00.000+00:00",
  "value": 2500.0,
  "createdAt": "2025-11-07T10:30:00.000",
  "updatedAt": null,
  "department": {
    "id": 8,
    "name": "Technical Department"
  },
  "category": {
    "id": 1,
    "name": "Laptop"
  }
}
```

**Error Response:** `400 Bad Request`
```json
"Department not found with id: 999"
```

---

### 1.5 Update Asset
Cập nhật thông tin tài sản.

**Endpoint:** `PUT /api/assets/{id}`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Integer | ID của tài sản cần update |

**Request Body:** (Tất cả fields đều optional, chỉ gửi fields cần update)
```json
{
  "name": "Asus ROG Updated",
  "location": "IT Department - Room 205",
  "description": "Updated gaming laptop",
  "imageKeys": ["asus-rog-updated.jpg"],
  "purchaseDate": "2025-11-07T00:00:00.000Z",
  "value": 1800.00,
  "departmentId": 8,
  "categoryId": 1,
  "supplierId": 1
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Asus ROG Updated",
  "location": "IT Department - Room 205",
  "description": "Updated gaming laptop",
  "imageKeys": ["asus-rog-updated.jpg"],
  "purchaseDate": "2025-11-07T00:00:00.000+00:00",
  "value": 1800.0,
  "createdAt": "2025-11-07T00:21:37.386",
  "updatedAt": "2025-11-07T10:45:00.000",
  "department": {
    "id": 8,
    "name": "Technical Department"
  },
  "category": {
    "id": 1,
    "name": "Laptop"
  }
}
```

---

### 1.6 Delete Asset
Xóa tài sản.

**Endpoint:** `DELETE /api/assets/{id}`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Integer | ID của tài sản cần xóa |

**Example:**
```http
DELETE /api/assets/3
```

**Response:** `204 No Content`

**Error Response:** `404 Not Found`
```json
"Asset not found with id: 999"
```

---

## 2. Asset Operations

### 2.1 Assign Asset to User
Gán tài sản cho người dùng.

**Endpoint:** `POST /api/assets/assign`

**Request Body:**
```json
{
  "assetId": 1,
  "assigneeId": 2,
  "notes": "Assigned for remote work"
}
```

**Validation:**
- `assetId`: Required, must exist
- `assigneeId`: Required, must exist
- Asset không được gán cho người khác đang sử dụng

**Response:** `201 Created`
```json
{
  "id": 10,
  "beginTime": "2025-11-07T10:00:00.000",
  "endTime": null,
  "notes": "Assigned for remote work",
  "approvalStatus": "APPROVED",
  "asset": {
    "id": 1,
    "name": "Asus laptop",
    "location": "IT department"
  },
  "assignee": {
    "id": 2,
    "userName": "john_doe"
  },
  "assignedBy": {
    "id": 1,
    "userName": "admin"
  }
}
```

**Business Rules:**
- Mỗi tài sản chỉ được gán cho 1 user tại 1 thời điểm
- `endTime = null` nghĩa là gán vô thời hạn
- Phải thu hồi trước khi gán lại

---

### 2.2 Reclaim Asset
Thu hồi tài sản từ người dùng.

**Endpoint:** `POST /api/assets/reclaim`

**Request Body:**
```json
{
  "usageLogId": 10,
  "notes": "Asset returned in good condition"
}
```

**Validation:**
- `usageLogId`: Required, must exist
- Usage log phải đang active (endTime = null)

**Response:** `200 OK`
```json
{
  "id": 10,
  "beginTime": "2025-11-07T10:00:00.000",
  "endTime": "2025-11-07T16:30:00.000",
  "notes": "Asset returned in good condition",
  "approvalStatus": "APPROVED",
  "asset": {
    "id": 1,
    "name": "Asus laptop",
    "location": "IT department"
  },
  "assignee": {
    "id": 2,
    "userName": "john_doe"
  }
}
```

**Business Rules:**
- Chỉ có thể thu hồi tài sản đang được gán (endTime = null)
- Sau khi thu hồi, tài sản có thể gán lại cho người khác

---

### 2.3 Transfer Asset
Điều chuyển tài sản sang người dùng khác.

**Endpoint:** `POST /api/assets/transfer`

**Request Body:**
```json
{
  "usageLogId": 10,
  "newAssigneeId": 3,
  "notes": "Transferred to new team member"
}
```

**Validation:**
- `usageLogId`: Required, must exist and active
- `newAssigneeId`: Required, must exist

**Response:** `200 OK`
```json
{
  "id": 11,
  "beginTime": "2025-11-07T16:35:00.000",
  "endTime": null,
  "notes": "Transferred to new team member",
  "approvalStatus": "APPROVED",
  "asset": {
    "id": 1,
    "name": "Asus laptop",
    "location": "IT department"
  },
  "assignee": {
    "id": 3,
    "userName": "jane_smith"
  }
}
```

---

## 3. History & Reports

### 3.1 Get Asset History
Lấy lịch sử sử dụng của một tài sản.

**Endpoint:** `GET /api/assets/{id}/history`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Integer | ID của tài sản |

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | Integer | No | 0 | Số trang |
| `size` | Integer | No | 10 | Số items mỗi trang |

**Example:**
```http
GET /api/assets/1/history?page=0&size=10
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 12,
      "beginTime": "2025-10-01T09:00:00.000",
      "endTime": "2025-11-06T18:00:00.000",
      "notes": "Used for project ABC",
      "approvalStatus": "APPROVED",
      "asset": {
        "id": 1,
        "name": "Asus laptop"
      },
      "assignee": {
        "id": 2,
        "userName": "john_doe"
      }
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

---

### 3.2 Get User Asset History
Lấy lịch sử tài sản của một người dùng.

**Endpoint:** `GET /api/assets/user/{userId}/history`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `userId` | Integer | ID của user |

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | Integer | No | 0 | Số trang |
| `size` | Integer | No | 10 | Số items mỗi trang |

**Example:**
```http
GET /api/assets/user/2/history?page=0&size=10
```

**Response:** `200 OK` (Giống format Get Asset History)

---

## Business Rules

### 📌 Asset Assignment Rules
1. **Gán tài sản:**
   - Mỗi tài sản chỉ được gán cho 1 user tại 1 thời điểm
   - Muốn gán lại phải thu hồi trước
   - `endTime = null` nghĩa là gán vô thời hạn

2. **Thu hồi:**
   - Chỉ có thể thu hồi tài sản đang active (endTime = null)
   - Sau khi thu hồi có thể gán lại ngay

3. **Điều chuyển:**
   - Tự động thu hồi từ user cũ và gán cho user mới
   - Tạo 2 records: 1 record kết thúc, 1 record mới bắt đầu

4. **Xóa tài sản:**
   - Có thể xóa bất kỳ lúc nào
   - Lịch sử sử dụng sẽ bị xóa theo (cascade)

---

## Validation Rules

### ✅ AssetCreationRequest
```json
{
  "name": "Required, not blank",
  "location": "Required",
  "description": "Optional",
  "imageKeys": "Optional, array of strings",
  "purchaseDate": "Required, ISO 8601 format",
  "value": "Required, must be > 0",
  "departmentId": "Required, must exist",
  "categoryId": "Required, must exist",
  "supplierId": "Required, must exist"
}
```

### ✅ AssetUpdateRequest
```json
{
  "name": "Optional",
  "location": "Optional",
  "description": "Optional",
  "imageKeys": "Optional",
  "purchaseDate": "Optional",
  "value": "Optional, must be > 0 if provided",
  "departmentId": "Optional, must exist if provided",
  "categoryId": "Optional, must exist if provided",
  "supplierId": "Optional, must exist if provided"
}
```

### ✅ AssetAssignmentRequest
```json
{
  "assetId": "Required, must exist",
  "assigneeId": "Required, must exist",
  "notes": "Optional"
}
```

### ✅ AssetReclamationRequest
```json
{
  "usageLogId": "Required, must exist and be active",
  "notes": "Optional"
}
```

### ✅ AssetTransferRequest
```json
{
  "usageLogId": "Required, must exist and be active",
  "newAssigneeId": "Required, must exist",
  "notes": "Optional"
}
```

---

## Workflows

### 🔄 Workflow 1: Tạo và gán tài sản mới
```
1. POST /api/assets
   → Tạo tài sản mới
   
2. POST /api/assets/assign
   → Gán cho user
   
3. GET /api/assets/1/history
   → Xem lịch sử gán
```

### 🔄 Workflow 2: Thu hồi và gán lại
```
1. POST /api/assets/reclaim
   → Thu hồi từ user hiện tại
   
2. POST /api/assets/assign
   → Gán cho user mới
```

### 🔄 Workflow 3: Điều chuyển trực tiếp
```
1. POST /api/assets/transfer
   → Chuyển từ user A sang user B
   (Tự động thu hồi và gán lại)
```

### 🔄 Workflow 4: Tra cứu và báo cáo
```
1. GET /api/assets?departmentId=8
   → Xem tài sản của phòng ban
   
2. GET /api/assets/user/2/history
   → Xem user đã dùng tài sản gì
   
3. GET /api/assets/1/history
   → Xem ai đã dùng tài sản này
```

---

## Testing Guide

### 📊 HTTP Status Codes

| Code | Meaning | When |
|------|---------|------|
| `200` | OK | GET, PUT, POST (reclaim/transfer) thành công |
| `201` | Created | POST (create/assign) thành công |
| `204` | No Content | DELETE thành công |
| `400` | Bad Request | Validation lỗi, business rule vi phạm |
| `404` | Not Found | Resource không tồn tại |
| `401` | Unauthorized | Không có token hoặc token hết hạn |
| `403` | Forbidden | Không có quyền truy cập |

### 🧪 Test Scenarios

#### ✅ Happy Path Tests:
1. Tạo tài sản mới thành công
2. Lấy danh sách tài sản với pagination
3. Tìm kiếm tài sản theo tên (case-insensitive)
4. Lọc tài sản theo department
5. Lọc tài sản theo category
6. Kết hợp nhiều filter
7. Lấy chi tiết tài sản by ID
8. Cập nhật tài sản thành công
9. Gán tài sản cho user
10. Thu hồi tài sản
11. Điều chuyển tài sản
12. Xem lịch sử tài sản
13. Xem lịch sử user
14. Xóa tài sản

#### ❌ Error Case Tests:
1. Tạo tài sản với value < 0
2. Tạo tài sản với department không tồn tại
3. Gán tài sản đã được gán
4. Thu hồi tài sản đã thu hồi
5. Lấy tài sản với ID không tồn tại
6. Update tài sản không tồn tại
7. Xóa tài sản không tồn tại
8. Gọi API không có token
9. Gọi API với token hết hạn
10. Thiếu required fields

### 🔗 Postman Collection
Import file `Asset-API-Postman-Collection.json` vào Postman để test tất cả endpoints.

**Quick Start:**
1. Import collection vào Postman
2. Login để lấy JWT token (tự động lưu)
3. Test các endpoints (token tự động gắn vào header)

---

## 📝 Notes

- Tất cả datetime theo format ISO 8601
- Pagination mặc định: page=0, size=10
- Sort mặc định: id DESC (mới nhất trước)
- Search name: case-insensitive, partial match
- imageKeys: array of string, có thể rỗng

---

**Last Updated:** 2025-11-07  
**Version:** 1.0  
**Author:** DreamCode Team
