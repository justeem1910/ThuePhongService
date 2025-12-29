# HƯỚNG DẪN CÀI ĐẶT HỆ THỐNG THUÊ PHÒNG TRÊN DOCKER

## Mục lục
1. [Giới thiệu](#giới-thiệu)
2. [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
3. [Cài đặt Docker và Docker Compose](#cài-đặt-docker-và-docker-compose)
4. [Chuẩn bị môi trường](#chuẩn-bị-môi-trường)
5. [Build và chạy ứng dụng](#build-và-chạy-ứng-dụng)
6. [Kiểm tra hệ thống](#kiểm-tra-hệ-thống)
7. [Troubleshooting](#troubleshooting)
8. [Cấu trúc hệ thống](#cấu-trúc-hệ-thống)

---

## Giới thiệu

Hệ thống Thuê Phòng là một ứng dụng microservices được xây dựng bằng Spring Boot, sử dụng kiến trúc microservices với các thành phần:

- **Eureka Server**: Service Discovery
- **API Gateway**: Điểm vào duy nhất cho tất cả các request
- **Student Service**: Quản lý thông tin sinh viên
- **Room Service**: Quản lý thông tin phòng
- **RentRoom Service**: Quản lý hợp đồng thuê phòng
- **PostgreSQL**: Database
- **PgAdmin**: Công cụ quản lý database

---

## Yêu cầu hệ thống

### Phần cứng tối thiểu:
- **CPU**: 2 cores trở lên
- **RAM**: 4GB trở lên (khuyến nghị 8GB)
- **Ổ cứng**: 10GB dung lượng trống
- **Hệ điều hành**: Windows 10/11, macOS, hoặc Linux

### Phần mềm cần thiết:
1. **Docker Desktop** (hoặc Docker Engine + Docker Compose)
   - Phiên bản: Docker 20.10 trở lên
   - Docker Compose: 2.0 trở lên

2. **Maven** (để build ứng dụng)
   - Phiên bản: 3.6 trở lên
   - Java: JDK 17 hoặc 18

3. **Git** (để clone repository)

---

## Cài đặt Docker và Docker Compose

### Bước 1: Cài đặt Docker Desktop (Windows/macOS)

#### Windows:
1. Tải Docker Desktop từ: https://www.docker.com/products/docker-desktop
2. Chạy file installer `Docker Desktop Installer.exe`
3. Chọn "Use WSL 2 instead of Hyper-V" (nếu có)
4. Khởi động lại máy tính nếu được yêu cầu
5. Mở Docker Desktop và đợi Docker khởi động hoàn tất

#### macOS:
1. Tải Docker Desktop từ: https://www.docker.com/products/docker-desktop
2. Mở file `.dmg` và kéo Docker vào Applications
3. Mở Docker Desktop từ Applications
4. Đợi Docker khởi động hoàn tất

#### Linux (Ubuntu/Debian):
```bash
# Cập nhật package list
sudo apt-get update

# Cài đặt các package cần thiết
sudo apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

# Thêm Docker's official GPG key
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Setup repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Cài đặt Docker Engine và Docker Compose
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Thêm user vào docker group (để chạy docker không cần sudo)
sudo usermod -aG docker $USER

# Khởi động lại session hoặc đăng xuất/đăng nhập lại
```

### Bước 2: Kiểm tra cài đặt Docker

Mở terminal/command prompt và chạy các lệnh sau:

```bash
# Kiểm tra Docker version
docker --version

# Kiểm tra Docker Compose version
docker compose version

# Kiểm tra Docker đang chạy
docker ps
```

Kết quả mong đợi:
- Docker version 20.10.x hoặc cao hơn
- Docker Compose version 2.x.x hoặc cao hơn
- Lệnh `docker ps` chạy thành công (có thể không có container nào)

---

## Chuẩn bị môi trường

### Bước 1: Clone repository

```bash
# Clone repository từ GitHub
git clone https://github.com/justeem1910/ThuePhongService.git

# Di chuyển vào thư mục dự án
cd ThuePhongService
```

### Bước 2: Tạo file init.sql cho database

Tạo thư mục `docker` nếu chưa có và tạo file `docker/init.sql`:

```bash
# Windows PowerShell
New-Item -ItemType Directory -Force -Path docker
```

Tạo file `docker/init.sql` với nội dung sau:

```sql
-- Tạo các database cho các microservices
CREATE DATABASE "StudentDB";
CREATE DATABASE "RoomDB";
CREATE DATABASE "RentRoomDB";

-- Cấp quyền cho user postgres
GRANT ALL PRIVILEGES ON DATABASE "StudentDB" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "RoomDB" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "RentRoomDB" TO postgres;
```

**Lưu ý**: File này sẽ tự động chạy khi container PostgreSQL khởi động lần đầu tiên.

### Bước 3: Build các microservices

Trước khi chạy Docker Compose, bạn cần build các JAR files cho các services:

```bash
# Build tất cả các modules
mvn clean install -DskipTests

# Hoặc build từng service riêng lẻ:
# Build eureka-server
cd eureka-server
mvn clean package -DskipTests
cd ..

# Build gateway
cd gateway
mvn clean package -DskipTests
cd ..

# Build student-service
cd student-service
mvn clean package -DskipTests
cd ..

# Build room-service
cd room-service
mvn clean package -DskipTests
cd ..

# Build rentRoom-service
cd rentRoom-service
mvn clean package -DskipTests
cd ..
```

**Lưu ý**: 
- Đảm bảo bạn đã cài đặt Maven và Java JDK 17/18
- Các file JAR sẽ được tạo trong thư mục `target/` của mỗi service
- Nếu gặp lỗi, kiểm tra lại Java version: `java -version` (phải là 17 hoặc 18)

---

## Build và chạy ứng dụng

### Bước 1: Kiểm tra file docker-compose.yml

Đảm bảo file `docker-compose.yml` nằm ở thư mục gốc của dự án và có đầy đủ các services.

### Bước 2: Build và khởi động tất cả services

```bash
# Build và khởi động tất cả services
docker compose up --build

# Hoặc chạy ở chế độ background (detached mode)
docker compose up --build -d
```

**Giải thích**:
- `--build`: Build lại các Docker images trước khi chạy
- `-d`: Chạy ở chế độ background (detached mode)

### Bước 3: Kiểm tra logs

Nếu chạy ở chế độ background, bạn có thể xem logs:

```bash
# Xem logs của tất cả services
docker compose logs -f

# Xem logs của một service cụ thể
docker compose logs -f eureka-server
docker compose logs -f gateway
docker compose logs -f student-service
docker compose logs -f room-service
docker compose logs -f rentroom-service
docker compose logs -f postgres
```

### Bước 4: Kiểm tra trạng thái containers

```bash
# Xem danh sách các containers đang chạy
docker compose ps

# Hoặc dùng lệnh Docker thông thường
docker ps
```

Kết quả mong đợi: Tất cả các containers phải ở trạng thái "Up" hoặc "healthy".

---

## Kiểm tra hệ thống

### Bước 1: Kiểm tra Eureka Server

Mở trình duyệt và truy cập:
```
http://localhost:8761
```

Bạn sẽ thấy Eureka Dashboard. Kiểm tra xem các services sau đã đăng ký chưa:
- **GATEWAY-SERVICE**
- **STUDENT-SERVICE**
- **ROOM-SERVICE**
- **RENTROOM-SERVICE**

### Bước 2: Kiểm tra PostgreSQL

```bash
# Kiểm tra container PostgreSQL đang chạy
docker ps | grep postgres

# Kết nối vào PostgreSQL container
docker exec -it postgres-thuephong psql -U postgres

# Trong PostgreSQL shell, kiểm tra các database
\l

# Thoát khỏi PostgreSQL shell
\q
```

### Bước 3: Kiểm tra PgAdmin

Mở trình duyệt và truy cập:
```
http://localhost:5050
```

Đăng nhập với thông tin:
- **Email**: admin@admin.com
- **Password**: admin

Sau đó thêm server PostgreSQL:
- **Host**: postgres (hoặc postgres-thuephong)
- **Port**: 5432
- **Username**: postgres
- **Password**: hlong1910

### Bước 4: Kiểm tra API Gateway

Kiểm tra API Gateway đang chạy:
```bash
# Test health check (nếu có)
curl http://localhost:8889/actuator/health

# Hoặc mở trình duyệt
http://localhost:8889
```

### Bước 5: Test các API endpoints

Sau khi tất cả services đã đăng ký với Eureka, bạn có thể test các API:

```bash
# Test Student Service qua Gateway
curl http://localhost:8889/api/sinhvien

# Test Room Service qua Gateway
curl http://localhost:8889/api/phong

# Test RentRoom Service qua Gateway
curl http://localhost:8889/api/hopdong
```

---

## Troubleshooting

### Lỗi 1: Port đã được sử dụng

**Triệu chứng**: 
```
Error: bind: address already in use
```

**Giải pháp**:
1. Kiểm tra port nào đang được sử dụng:
   ```bash
   # Windows
   netstat -ano | findstr :8761
   netstat -ano | findstr :8889
   netstat -ano | findstr :5432
   
   # Linux/macOS
   lsof -i :8761
   lsof -i :8889
   lsof -i :5432
   ```

2. Dừng process đang sử dụng port hoặc thay đổi port trong `docker-compose.yml`

### Lỗi 2: Container không build được

**Triệu chứng**:
```
ERROR: failed to solve: failed to compute cache key
```

**Giải pháp**:
1. Đảm bảo đã build JAR files trước:
   ```bash
   mvn clean install -DskipTests
   ```

2. Kiểm tra file JAR có tồn tại trong thư mục `target/`:
   ```bash
   # Windows
   dir eureka-server\target\*.jar
   dir gateway\target\*.jar
   
   # Linux/macOS
   ls eureka-server/target/*.jar
   ls gateway/target/*.jar
   ```

### Lỗi 3: Services không đăng ký với Eureka

**Triệu chứng**: Services không xuất hiện trên Eureka Dashboard

**Giải pháp**:
1. Kiểm tra Eureka Server đã chạy chưa:
   ```bash
   docker compose logs eureka-server
   ```

2. Kiểm tra network:
   ```bash
   docker network ls
   docker network inspect thuephong-service_thuephong-net
   ```

3. Đảm bảo các services đợi Eureka Server khởi động xong:
   - Trong `docker-compose.yml`, các services đã có `depends_on: - eureka-server`
   - Nếu vẫn lỗi, có thể cần thêm healthcheck hoặc đợi thêm vài giây

### Lỗi 4: Database connection failed

**Triệu chứng**:
```
Connection refused: connect
```

**Giải pháp**:
1. Kiểm tra PostgreSQL container đang chạy:
   ```bash
   docker compose ps postgres
   ```

2. Kiểm tra logs của PostgreSQL:
   ```bash
   docker compose logs postgres
   ```

3. Kiểm tra file `init.sql` đã được mount đúng chưa:
   ```bash
   docker exec -it postgres-thuephong ls /docker-entrypoint-initdb.d/
   ```

4. Kiểm tra các database đã được tạo chưa:
   ```bash
   docker exec -it postgres-thuephong psql -U postgres -c "\l"
   ```

### Lỗi 5: Out of memory

**Triệu chứng**:
```
Container killed: out of memory
```

**Giải pháp**:
1. Tăng memory cho Docker Desktop:
   - Mở Docker Desktop → Settings → Resources
   - Tăng Memory lên ít nhất 4GB (khuyến nghị 8GB)

2. Hoặc giảm số lượng services chạy cùng lúc

### Lỗi 6: Maven build failed

**Triệu chứng**:
```
[ERROR] Failed to execute goal...
```

**Giải pháp**:
1. Kiểm tra Java version (phải là 17 hoặc 18):
   ```bash
   java -version
   ```

2. Kiểm tra Maven version:
   ```bash
   mvn -version
   ```

3. Xóa thư mục `.m2/repository` và build lại:
   ```bash
   # Windows
   rmdir /s /q %USERPROFILE%\.m2\repository
   
   # Linux/macOS
   rm -rf ~/.m2/repository
   
   # Build lại
   mvn clean install -DskipTests
   ```

### Lệnh hữu ích khác

```bash
# Dừng tất cả containers
docker compose down

# Dừng và xóa volumes (xóa cả database data)
docker compose down -v

# Xem logs real-time của một service
docker compose logs -f <service-name>

# Restart một service cụ thể
docker compose restart <service-name>

# Rebuild một service cụ thể
docker compose up --build -d <service-name>

# Xem resource usage
docker stats

# Xóa tất cả containers, images, volumes (cẩn thận!)
docker system prune -a --volumes
```

---

## Cấu trúc hệ thống

### Ports được sử dụng:

| Service | Port | URL |
|---------|------|-----|
| Eureka Server | 8761 | http://localhost:8761 |
| API Gateway | 8889 | http://localhost:8889 |
| Student Service | 8084 | http://localhost:8084 |
| Room Service | 8085 | http://localhost:8085 |
| RentRoom Service | 8086 | http://localhost:8086 |
| PostgreSQL | 5432 | localhost:5432 |
| PgAdmin | 5050 | http://localhost:5050 |

### Database:

| Database | Service |
|----------|---------|
| StudentDB | student-service |
| RoomDB | room-service |
| RentRoomDB | rentroom-service |

### API Endpoints qua Gateway:

| Endpoint | Service | Mô tả |
|----------|---------|-------|
| `/api/sinhvien/**` | student-service | Quản lý sinh viên |
| `/api/phong/**` | room-service | Quản lý phòng |
| `/api/hopdong/**` | rentroom-service | Quản lý hợp đồng thuê phòng |

### Thông tin đăng nhập mặc định:

**PostgreSQL:**
- Username: `postgres`
- Password: `hlong1910`

**PgAdmin:**
- Email: `admin@admin.com`
- Password: `admin`

**Lưu ý bảo mật**: Đổi các mật khẩu mặc định trước khi deploy lên môi trường production!

---

## Tắt hệ thống

```bash
# Dừng tất cả containers (giữ lại data)
docker compose stop

# Dừng và xóa containers (giữ lại data)
docker compose down

# Dừng và xóa containers + volumes (xóa cả database data)
docker compose down -v
```

---

## Khởi động lại hệ thống

```bash
# Khởi động lại từ đầu
docker compose up --build -d

# Hoặc chỉ start các containers đã có
docker compose start
```

---

## Liên hệ và hỗ trợ

Nếu gặp vấn đề trong quá trình cài đặt, vui lòng:
1. Kiểm tra lại các bước trong hướng dẫn
2. Xem phần Troubleshooting
3. Kiểm tra logs của các services
4. Tạo issue trên GitHub repository

---

**Chúc bạn cài đặt thành công! 🚀**

