# DreamCode CNPMNC 251 – Hướng dẫn Login & RBAC

Tài liệu này mô tả cách hoạt động của cơ chế đăng nhập (JWT), phân quyền (RBAC), cách cấu hình môi trường (.env), các endpoint liên quan, và cách test nhanh bằng Postman/cURL.

## Tổng quan

- Xác thực: JWT Bearer, stateless (không dùng session server-side).
- Endpoint chính:
  - POST `/auth/login`: nhận username/password, trả về `token` (JWT).
  - POST `/auth/logout`: nhận Bearer token hiện tại và đưa vào blacklist đến khi hết hạn.
- RBAC theo path (role-based access control): uỷ quyền bằng vai trò trong JWT.
- Chuẩn hoá lỗi: trả JSON rõ ràng cho 400/401/403: validation, invalid credentials, invalid/expired token, forbidden.

## Biến môi trường (.env)

Ứng dụng hỗ trợ nạp cấu hình từ `.env` (đã ignore khỏi git). Tạo file `.env` ở thư mục gốc dự án:

```
# Server
SERVER_PORT=8080

# Database (PostgreSQL)
DB_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USERNAME=<db_user>
DB_PASSWORD=<db_password>

# JWT
JWT_SECRET=<chuoi_bi_mat_du>=
JWT_EXP_SECONDS=86400

# Admin mặc định
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123

# Seed user mẫu
SEED_SAMPLE_USERS=true
```

Ghi chú:

- `JWT_SECRET` là chuỗi bí mật dùng ký JWT (HMAC). Hãy đặt giá trị đủ mạnh. (Có thể để bất kỳ, service sẽ mã hoá base64 nội bộ.)
- `JWT_EXP_SECONDS` là thời gian sống của access token (giây).
- Nếu dùng cơ sở dữ liệu Neon, điền `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` theo thông tin Neon.

## Cấu hình chính (application.yaml)

- Datasource đọc từ `DB_URL/DB_USERNAME/DB_PASSWORD`.
- `spring.jpa.hibernate.ddl-auto=validate` và Liquibase bật mặc định.
- Server port đọc từ `SERVER_PORT`.

## Seed tài khoản và vai trò

Khi khởi động, nếu chưa tồn tại sẽ seed:

- Admin: `ADMIN_USERNAME`/`ADMIN_PASSWORD` (mặc định `admin`/`admin123`) với role `ADMIN`.
- Nếu `SEED_SAMPLE_USERS=true`:
  - `viewer1` / `viewer123` → `VIEWER`
  - `assetmgr1` / `asset123` → `ASSET_MANAGER`
  - `deptmgr1` / `dept123` → `DEPT_MANAGER`
  - `user1` / `user123` → `USER`

## Payload & Claim của JWT

- Subject: `username`.
- Claims bổ sung:
  - `uid`: ID người dùng.
  - `roles`: danh sách role (ví dụ: `["ADMIN"]`).

## Endpoint chi tiết

- Public:
  - `POST /auth/login`
  - `GET /api/ping`
- RBAC theo domain:
  - `GET /api/reports/**` → `VIEWER` hoặc `ADMIN`
  - `GET /api/assets/**` → `ASSET_MANAGER` hoặc `ADMIN`
  - `GET /api/department/**` → `DEPT_MANAGER` hoặc `ADMIN`
  - `GET /api/user/**` → `USER`, `DEPT_MANAGER`, `ASSET_MANAGER`, hoặc `ADMIN`
  - `GET /api/admin/**` → chỉ `ADMIN`
- Logout:
  - `POST /auth/logout` (yêu cầu Bearer token hợp lệ). Token sẽ bị đưa vào blacklist đến khi hết hạn.

## Request/Response mẫu

### Đăng nhập

Request:

```
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Response 200:

```
{
  "token": "<JWT>"
}
```

### Gọi API có bảo vệ

Request:

```
GET /api/user/health
Authorization: Bearer <JWT>
```

Response 200:

```
"user-ok"
```

### Đăng xuất

Request:

```
POST /auth/logout
Authorization: Bearer <JWT>
```

Response 204 No Content.

Sau khi logout, token cũ bị blacklist và sẽ nhận 401 nếu dùng lại.

## Xử lý lỗi chuẩn hoá

- 400 Validation (ví dụ thiếu username):

```
{
  "timestamp": "2025-11-04T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "username: username is required",
  "path": "/auth/login"
}
```

- 401 Sai thông tin đăng nhập:

```
{
  "timestamp": "...",
  "status": 401,
  "error": "Unauthorized",
  "message": "invalid username or password",
  "path": "/auth/login"
}
```

- 401 Token không hợp lệ/hết hạn (ở filter):

```
{
  "status": 401,
  "error": "Unauthorized",
  "message": "invalid or expired token"
}
```

- 401 Token bị blacklist (sau khi logout):

```
{
  "status": 401,
  "error": "Unauthorized",
  "message": "token blacklisted"
}
```

- 403 Không đủ quyền:

```
{
  "timestamp": "...",
  "status": 403,
  "error": "Forbidden",
  "message": "forbidden",
  "path": "/api/admin/health"
}
```

## Chạy ứng dụng

- Yêu cầu: JDK 21+, Maven Wrapper đã kèm sẵn.
- Khởi chạy:

```
./mvnw spring-boot:run
```

- Đóng gói (bỏ qua test):

```
./mvnw -DskipTests package
```

- Chạy jar:

```
java -jar target/*.jar
```

Trên Windows (cmd):

````
.\n```
(hoặc dùng PowerShell với `./mvnw ...`)

## Ghi chú bảo mật
- Blacklist lưu in-memory → sẽ reset khi ứng dụng khởi động lại. Nếu cần, có thể chuyển sang lưu DB/Redis (TTL theo `exp`).
- Hãy đặt `JWT_SECRET` đủ mạnh và xoay vòng định kỳ. Không commit `.env`.
- Cân nhắc thêm: refresh token, rate limiting/lockout, CORS & security headers, audit log.

## Kiểm thử nhanh (gợi ý Postman)
1) Đăng nhập `POST /auth/login` → lấy `token`.
2) Gọi endpoint theo role với header `Authorization: Bearer <token>`.
3) Đăng xuất `POST /auth/logout` → thử gọi lại với token cũ để thấy 401.

---
Nếu cần mở rộng (refresh token, phân quyền theo phòng ban ở tầng service/repository), mình có thể bổ sung theo yêu cầu.
````
