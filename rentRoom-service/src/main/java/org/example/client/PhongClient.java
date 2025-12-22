package org.example.client;

import org.example.dto.PhongDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-service")
public interface PhongClient {
    @GetMapping("/api/phong/{id}")
    PhongDto getRoomById(@PathVariable("id") int id);
}