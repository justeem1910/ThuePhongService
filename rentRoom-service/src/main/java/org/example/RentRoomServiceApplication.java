package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
// 👇 IMPORT QUAN TRỌNG
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients // 👇 BẮT BUỘC PHẢI CÓ DÒNG NÀY
public class RentRoomServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RentRoomServiceApplication.class, args);
    }
}