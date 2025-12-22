package org.example.controller;

import org.example.model.Phong;
import org.example.service.PhongService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phong")
public class PhongController {
    private final PhongService service;

    public PhongController(PhongService service) {
        this.service = service;
    }

    @GetMapping
    public List<Phong> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Phong getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public String add(@RequestBody Phong p) {
        service.add(p);
        return "Thêm phòng thành công!";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable int id, @RequestBody Phong p) {
        p.setMaPhong(id);
        service.update(p);
        return "Cập nhật phòng thành công!";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        service.delete(id);
        return "Xoá phòng thành công!";
    }

    @GetMapping("/trong")
    public List<Phong> getPhongTrongTheoGioiTinh(@RequestParam String gioiTinh) {
        return service.getPhongTrongTheoGioiTinh(gioiTinh);
    }
}

