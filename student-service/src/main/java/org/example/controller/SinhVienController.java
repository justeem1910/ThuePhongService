package org.example.controller;

import org.example.model.SinhVien;
import org.example.service.SinhVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sinhvien")
public class SinhVienController {

    private final SinhVienService service;

    public SinhVienController(SinhVienService service) {
        this.service = service;
    }

    @GetMapping
    public List<SinhVien> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SinhVien getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody SinhVien sv) {
        try {
            service.add(sv);
            return ResponseEntity.ok("Thêm sinh viên thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody SinhVien sv) {
        sv.setMaSv(id);
        service.update(sv);
        return ResponseEntity.ok("Cập nhật sinh viên thành công!");
    }

    @GetMapping("/search")
    public List<SinhVien> search(@RequestParam(value = "keyword", required = false) String keyword) {
        return service.search(keyword);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.ok("Xoá sinh viên thành công!");
    }
}