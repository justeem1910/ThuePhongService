# 📋 MÔ TẢ LUỒNG XỬ LÝ TỪ CLIENT ĐẾN SERVER

## 🎯 Tổng quan kiến trúc

```
[Client/Browser] 
    ↓ HTTP Request
[API Gateway:8889] 
    ↓ Routing
[Microservices] 
    ↓ Feign Client (Inter-service communication)
[Other Microservices]
    ↓ JDBC
[PostgreSQL Database]
```

---

## 🔄 LUỒNG CHI TIẾT: TẠO HỢP ĐỒNG THUÊ PHÒNG MỚI

### **Ví dụ:** Client tạo hợp đồng thuê phòng mới qua Frontend

**Request:** `POST http://localhost:8889/api/hopdong`
**Body:**
```json
{
  "sinhVien": 1,
  "phong": 101,
  "ngayBatDau": "2024-01-01",
  "ngayKetThuc": "2024-12-31",
  "trangThai": "ACTIVE"
}
```

---

## 📍 BƯỚC 1: CLIENT GỬI REQUEST

### **1.1. Frontend (ThuePhongFE) - WebController**

**File:** `ThuePhongFE/src/main/java/org/example/WebController.java`

```java
@PostMapping("/save")
public String saveContract(@ModelAttribute HopDongDTO hopDong, Model model) {
    // Gọi POST http://localhost:8889/api/hopdong
    restTemplate.postForObject(GATEWAY_URL, hopDong, String.class);
    return "redirect:/";
}
```

**Xử lý:**
- User điền form tại `/add` và submit
- `WebController` nhận dữ liệu từ form
- Sử dụng `RestTemplate` gửi HTTP POST đến Gateway (port 8889)
- Gateway URL: `http://localhost:8889/api/hopdong`

---

## 📍 BƯỚC 2: API GATEWAY NHẬN VÀ ROUTING

### **2.1. Gateway nhận request**

**File:** `gateway/src/main/resources/application.yml`

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: rentroom-service
          uri: lb://rentroom-service  # Load balancer từ Eureka
          predicates:
            - Path=/api/hopdong/**
```

**Xử lý:**
1. Gateway (port 8889) nhận request `POST /api/hopdong`
2. Kiểm tra route pattern → Match với `/api/hopdong/**`
3. Tìm service `rentroom-service` trong Eureka Registry
4. Load balancer chọn một instance của `rentroom-service`
5. Forward request đến service đó (thường port 8086)

**Lưu ý:** 
- AuthFilter hiện đang bị comment, nếu bật sẽ xác thực JWT token trước khi routing
- CORS đã được cấu hình để cho phép cross-origin requests

---

## 📍 BƯỚC 3: RENTROOM-SERVICE XỬ LÝ REQUEST

### **3.1. Controller nhận request**

**File:** `rentRoom-service/src/main/java/org/example/controller/HopDongThueController.java`

```java
@PostMapping
public String add(@RequestBody HopDongThue hd) {
    service.add(hd);
    return "Thêm hợp đồng thành công!";
}
```

**Xử lý:**
- `HopDongThueController` nhận POST request
- Spring tự động deserialize JSON body thành object `HopDongThue`
- Gọi method `service.add(hd)` để xử lý business logic

---

### **3.2. Service Layer - Business Logic & Validation**

**File:** `rentRoom-service/src/main/java/org/example/service/HopDongThueService.java`

```java
public int add(HopDongThue hd) {
    // 1. Kiểm tra Sinh viên có tồn tại không?
    try {
        SinhVienDto sv = sinhVienClient.getStudentById(hd.getSinhVien());
        if (sv == null) {
            throw new RuntimeException("Sinh viên không tồn tại!");
        }
    } catch (Exception e) {
        throw new RuntimeException("Lỗi: Không tìm thấy sinh viên có ID = " + hd.getSinhVien());
    }

    // 2. Kiểm tra Phòng có tồn tại không?
    try {
        PhongDto phong = phongClient.getRoomById(hd.getPhong());
        if (phong == null) {
            throw new RuntimeException("Phòng không tồn tại!");
        }
    } catch (Exception e) {
        throw new RuntimeException("Lỗi: Không tìm thấy phòng có ID = " + hd.getPhong());
    }

    // 3. Nếu cả 2 đều tồn tại → Lưu vào database
    return repository.insert(hd);
}
```

**Xử lý:**
1. **Validation Step 1:** Gọi `sinhVienClient.getStudentById()` để kiểm tra sinh viên
2. **Validation Step 2:** Gọi `phongClient.getRoomById()` để kiểm tra phòng
3. **Nếu validation thành công:** Gọi `repository.insert()` để lưu vào database
4. **Nếu validation thất bại:** Throw exception → Trả về lỗi cho client

---

## 📍 BƯỚC 4: INTER-SERVICE COMMUNICATION (Feign Client)

### **4.1. Gọi Student Service**

**File:** `rentRoom-service/src/main/java/org/example/client/SinhVienClient.java`

```java
@FeignClient(name = "student-service")
public interface SinhVienClient {
    @GetMapping("/api/sinhvien/{id}")
    SinhVienDto getStudentById(@PathVariable("id") int id);
}
```

**Luồng xử lý:**
1. Feign Client tạo HTTP request động
2. Tìm service `student-service` trong Eureka Registry
3. Gửi request: `GET http://student-service/api/sinhvien/1`
4. Eureka resolve thành địa chỉ thực tế (ví dụ: `http://localhost:8084/api/sinhvien/1`)

### **4.2. Student Service xử lý**

**File:** `student-service/src/main/java/org/example/controller/SinhVienController.java`

```java
@GetMapping("/{id}")
public SinhVien getById(@PathVariable int id) {
    return service.getById(id);
}
```

**File:** `student-service/src/main/java/org/example/service/SinhVienService.java`

```java
public SinhVien getById(int id) {
    return repository.findById(id);
}
```

**Xử lý:**
- Controller nhận request `GET /api/sinhvien/1`
- Service gọi repository để query database
- Repository thực hiện SQL: `SELECT * FROM sinh_vien WHERE ma_sv = 1`
- Trả về object `SinhVien` hoặc `null` nếu không tìm thấy

### **4.3. Response trả về RentRoom Service**

- Nếu tìm thấy: Trả về `SinhVienDto` → Validation thành công
- Nếu không tìm thấy: Feign throw `FeignException` (404) → Validation thất bại

### **4.4. Tương tự với Room Service**

**File:** `rentRoom-service/src/main/java/org/example/client/PhongClient.java`

```java
@FeignClient(name = "room-service")
public interface PhongClient {
    @GetMapping("/api/phong/{id}")
    PhongDto getRoomById(@PathVariable("id") int id);
}
```

**Luồng tương tự:**
1. Feign gọi `GET http://room-service/api/phong/101`
2. Room Service query database: `SELECT * FROM phong WHERE ma_phong = 101`
3. Trả về `PhongDto` hoặc throw exception

---

## 📍 BƯỚC 5: LƯU VÀO DATABASE

### **5.1. Repository Layer**

**File:** `rentRoom-service/src/main/java/org/example/repository/HopDongThueRepository.java`

```java
public int insert(HopDongThue hd) {
    String sql = "INSERT INTO hop_dong_thue(ma_sv, ma_phong, ngay_bat_dau, ngay_ket_thuc, trang_thai) VALUES (?, ?, ?, ?, ?)";
    return jdbcTemplate.update(sql,
            hd.getSinhVien(),
            hd.getPhong(),
            hd.getNgayBatDau(),
            hd.getNgayKetThuc(),
            hd.getTrangThai());
}
```

**Xử lý:**
- Sử dụng `JdbcTemplate` để thực thi SQL
- Prepared statement để tránh SQL injection
- Insert vào bảng `hop_dong_thue` trong database `RentRoomDB`
- Trả về số dòng bị ảnh hưởng (1 nếu thành công)

**Database Connection:**
- URL: `jdbc:postgresql://localhost:5432/RentRoomDB`
- Username: `postgres`
- Password: `hlong1910`

---

## 📍 BƯỚC 6: RESPONSE TRẢ VỀ CLIENT

### **6.1. Response Path**

```
Repository.insert() 
    → Service.add() 
    → Controller.add() 
    → Gateway 
    → Frontend WebController 
    → Browser
```

### **6.2. Success Response**

**Controller trả về:**
```java
return "Thêm hợp đồng thành công!";
```

**HTTP Response:**
- Status: `200 OK`
- Body: `"Thêm hợp đồng thành công!"`
- Frontend nhận response → Redirect về trang chủ `/`

### **6.3. Error Response**

**Nếu validation thất bại:**
```java
throw new RuntimeException("Lỗi: Không tìm thấy sinh viên có ID = 1");
```

**HTTP Response:**
- Status: `500 Internal Server Error` hoặc `400 Bad Request`
- Body: Error message
- Frontend catch exception → Hiển thị lỗi trên form

---

## 🔄 LUỒNG KHÁC: LẤY DANH SÁCH HỢP ĐỒNG

### **Request:** `GET http://localhost:8889/api/hopdong`

**Luồng xử lý:**

1. **Client:** Browser gọi `/` → `WebController.index()`
2. **Frontend:** `RestTemplate.getForObject(GATEWAY_URL, HopDongDTO[].class)`
3. **Gateway:** Route đến `rentroom-service`
4. **Controller:** `HopDongThueController.getAll()`
5. **Service:** `HopDongThueService.getAll()`
6. **Repository:** `SELECT * FROM hop_dong_thue`
7. **Database:** Trả về danh sách hợp đồng
8. **Response:** JSON array → Frontend render HTML table

---

## 🔄 LUỒNG KHÁC: TÌM PHÒNG TRỐNG THEO GIỚI TÍNH

### **Request:** `GET http://localhost:8889/api/phong/trong?gioiTinh=Nam`

**Luồng xử lý:**

1. **Gateway:** Route đến `room-service` (path `/api/phong/**`)
2. **Controller:** `PhongController.getPhongTrongTheoGioiTinh("Nam")`
3. **Service:** `PhongService.getPhongTrongTheoGioiTinh("Nam")`
4. **Repository:** Query database với điều kiện giới tính
5. **Response:** Danh sách phòng trống phù hợp

---

## 🎯 ĐIỂM QUAN TRỌNG

### **1. Service Discovery (Eureka)**
- Tất cả services đăng ký với Eureka Server (port 8761)
- Gateway và Feign Client sử dụng service name để tìm service
- Load balancing tự động khi có nhiều instance

### **2. Inter-Service Communication**
- **Feign Client:** Giao tiếp đồng bộ giữa các services
- **Service Name:** Sử dụng `spring.application.name` thay vì hardcode IP/Port
- **Error Handling:** Try-catch để xử lý khi service không khả dụng

### **3. Database Isolation**
- Mỗi service có database riêng:
  - `StudentDB` → student-service
  - `RoomDB` → room-service
  - `RentRoomDB` → rentroom-service
- Đảm bảo tính độc lập và có thể scale riêng biệt

### **4. Validation Logic**
- Business logic nằm ở Service layer
- Validation cross-service (kiểm tra sinh viên/phòng tồn tại)
- Transaction có thể được thêm nếu cần rollback

### **5. Error Propagation**
- Exception từ service → Controller → Gateway → Client
- Frontend xử lý error và hiển thị thông báo phù hợp

---

## 📊 SƠ ĐỒ LUỒNG TỔNG QUAN

```
┌─────────────┐
│   Browser   │
└──────┬──────┘
       │ HTTP POST /api/hopdong
       ↓
┌─────────────────┐
│  API Gateway    │ ← Eureka Discovery
│   (Port 8889)   │
└────────┬────────┘
         │ Route to rentroom-service
         ↓
┌─────────────────────┐
│  RentRoom Service   │
│   (Port 8086)       │
│  ┌───────────────┐  │
│  │  Controller   │  │
│  └───────┬───────┘  │
│          ↓          │
│  ┌───────────────┐  │
│  │   Service     │  │
│  └───────┬───────┘  │
│          │          │
│    ┌─────┴─────┐    │
│    ↓           ↓    │
│ ┌──────┐   ┌──────┐│
│ │Feign │   │Feign ││
│ │Client│   │Client││
│ └──┬───┘   └──┬───┘│
└────┼──────────┼────┘
     │          │
     ↓          ↓
┌─────────┐ ┌─────────┐
│Student  │ │ Room    │
│Service  │ │ Service │
│(8084)   │ │ (8085)  │
└────┬────┘ └────┬────┘
     │          │
     ↓          ↓
┌─────────┐ ┌─────────┐
│StudentDB│ │ RoomDB  │
└─────────┘ └─────────┘
     │
     │ (After validation)
     ↓
┌─────────────────────┐
│  RentRoom Service   │
│  ┌───────────────┐  │
│  │  Repository   │  │
│  └───────┬───────┘  │
└──────────┼──────────┘
           ↓
    ┌──────────────┐
    │  RentRoomDB  │
    └──────────────┘
```

---

## 🔍 CÁC TRƯỜNG HỢP XỬ LÝ

### **Case 1: Tạo hợp đồng thành công**
✅ Sinh viên tồn tại + Phòng tồn tại → Insert thành công → Response 200

### **Case 2: Sinh viên không tồn tại**
❌ Feign Client nhận 404 → Throw exception → Response 500 với message lỗi

### **Case 3: Phòng không tồn tại**
❌ Feign Client nhận 404 → Throw exception → Response 500 với message lỗi

### **Case 4: Service không khả dụng**
❌ Eureka không tìm thấy service → Feign throw exception → Response 500

### **Case 5: Database connection error**
❌ JDBC exception → Service throw → Controller trả về error → Client nhận lỗi

---

## 💡 TỐI ƯU HÓA & BEST PRACTICES

1. **Caching:** Có thể cache thông tin sinh viên/phòng để giảm inter-service calls
2. **Circuit Breaker:** Thêm Resilience4j để xử lý khi service down
3. **Async Processing:** Có thể dùng message queue cho các tác vụ không cần đồng bộ
4. **Distributed Tracing:** Thêm Zipkin/Sleuth để trace request qua các services
5. **API Versioning:** Thêm version vào API path (`/api/v1/hopdong`)


