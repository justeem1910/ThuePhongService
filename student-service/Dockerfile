# Sử dụng Amazon Corretto (Bản Alpine siêu nhẹ, hỗ trợ tốt Mac M1/M2/M3)
FROM amazoncorretto:17-alpine-jdk

# Copy file jar (giữ nguyên logic cũ)
COPY target/*.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java","-jar","/app.jar"]