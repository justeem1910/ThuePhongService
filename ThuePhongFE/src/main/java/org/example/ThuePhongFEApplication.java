package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration; // Import dòng này

// Thêm phần exclude này để bảo Spring Boot đừng tìm Database nữa
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ThuePhongFEApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThuePhongFEApplication.class, args);
    }
}