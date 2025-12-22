package org.example.client;

import org.example.dto.SinhVienDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name = "student-service" phải trùng với spring.application.name trong yml của bên Sinh viên
@FeignClient(name = "student-service")
public interface SinhVienClient {
    // Đường dẫn này phải khớp với Controller bên Student Service
    @GetMapping("/api/sinhvien/{id}")
    SinhVienDto getStudentById(@PathVariable("id") int id);
}